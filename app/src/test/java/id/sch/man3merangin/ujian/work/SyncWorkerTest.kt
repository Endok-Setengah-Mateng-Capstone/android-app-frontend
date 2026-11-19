package id.sch.man3merangin.ujian.work

import androidx.work.ListenableWorker.Result
import androidx.work.testing.TestListenableWorkerBuilder
import id.sch.man3merangin.ujian.UjianApplication
import id.sch.man3merangin.ujian.data.db.AppDatabase
import id.sch.man3merangin.ujian.data.db.OutboxEntity
import id.sch.man3merangin.ujian.data.net.ApiException
import id.sch.man3merangin.ujian.data.net.DeviceMismatchException
import id.sch.man3merangin.ujian.data.net.ExamApi
import id.sch.man3merangin.ujian.data.net.FakeExamApi
import id.sch.man3merangin.ujian.data.net.SyncItem
import id.sch.man3merangin.ujian.data.net.SyncResult
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = UjianApplication::class)
class SyncWorkerTest {
    private val app get() = RuntimeEnvironment.getApplication() as UjianApplication
    private lateinit var db: AppDatabase
    private lateinit var fake: FakeExamApi

    @Before fun setUp() {
        db = AppDatabase.inMemory(app)
        fake = FakeExamApi()
        app.session.rememberToken("A", "tokA")
        app.database = db
        app.api = fake
    }
    @After fun tearDown() { db.close() }

    private fun seed(n: Int) = runBlocking {
        repeat(n) { db.outboxDao().insert(OutboxEntity("id$it", "ANSWER", "{}", it.toLong(), 0, "A")) }
    }
    private fun run() = runBlocking { TestListenableWorkerBuilder<SyncWorker>(app).build().doWork() }
    private fun ids() = runBlocking { db.outboxDao().pending(100).map { it.id } }

    @Test fun deletesOnlyAcceptedIds() {
        seed(3)
        app.api = object : ExamApi by fake {
            override suspend fun sync(batch: List<SyncItem>, sessionToken: String) = SyncResult(listOf("id0", "id2"))
        }
        assertEquals(Result.success(), run())
        assertEquals(listOf("id1"), ids())
    }

    @Test fun ioExceptionReturnsRetryAndKeepsRows() {
        seed(2)
        app.api = object : ExamApi by fake {
            override suspend fun sync(batch: List<SyncItem>, sessionToken: String): SyncResult = throw IOException("down")
        }
        assertEquals(Result.retry(), run())
        assertEquals(listOf("id0", "id1"), ids())
    }

    @Test fun retryResendsSameIds() {
        seed(2)
        val sent = mutableListOf<List<String>>()
        var fail = true
        app.api = object : ExamApi by fake {
            // server accepted, client timed out
            override suspend fun sync(batch: List<SyncItem>, sessionToken: String): SyncResult {
                sent += batch.map { it.id }
                if (fail) throw IOException("timeout")
                return SyncResult(batch.map { it.id })
            }
        }
        assertEquals(Result.retry(), run())
        fail = false
        assertEquals(Result.success(), run())
        assertEquals(listOf(listOf("id0", "id1"), listOf("id0", "id1")), sent)
        assertEquals(emptyList<String>(), ids())
    }

    @Test fun emptyOutboxIsSuccess() {
        assertEquals(Result.success(), run())
        assertEquals(0, fake.receivedSyncItems.size)
    }

    @Test fun unauthorizedReturnsRetryAndKeepsRows() {
        for (code in listOf(401, 403)) {
            db.clearAllTables()
            app.session.rememberToken("A", "tokA")
            seed(2)
            app.api = object : ExamApi by fake {
                override suspend fun sync(batch: List<SyncItem>, sessionToken: String): SyncResult = throw ApiException(code)
            }
            assertEquals(Result.retry(), run())
            assertEquals(listOf("id0", "id1"), ids())
        }
    }

    @Test fun deviceMismatchKeepsRowsAndSucceeds() {
        seed(1)
        fake.shouldThrow409 = true
        assertEquals(Result.success(), run())
        assertEquals(listOf("id0"), ids())
    }

    @Test fun batchIsCappedAt50() {
        seed(60)
        assertEquals(Result.success(), run())
        assertEquals(50, fake.receivedSyncItems.size)
    }
}
