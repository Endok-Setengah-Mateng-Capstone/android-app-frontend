package id.sch.man3merangin.ujian.work

import androidx.work.testing.TestListenableWorkerBuilder
import id.sch.man3merangin.ujian.UjianApplication
import id.sch.man3merangin.ujian.data.db.AppDatabase
import id.sch.man3merangin.ujian.data.db.OutboxEntity
import id.sch.man3merangin.ujian.data.net.ExamApi
import id.sch.man3merangin.ujian.data.net.FakeExamApi
import id.sch.man3merangin.ujian.data.net.SyncItem
import id.sch.man3merangin.ujian.data.net.SyncResult
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = UjianApplication::class)
class OutboxOwnershipTest {
    private val app get() = RuntimeEnvironment.getApplication() as UjianApplication
    private lateinit var db: AppDatabase
    private val sent = mutableListOf<Pair<String, List<String>>>() // token to ids

    @Before fun setUp() {
        db = AppDatabase.inMemory(app)
        app.database = db
        val fake = FakeExamApi()
        app.api = object : ExamApi by fake {
            override suspend fun sync(batch: List<SyncItem>, sessionToken: String): SyncResult {
                sent += sessionToken to batch.map { it.id }
                return SyncResult(batch.map { it.id })
            }
        }
    }
    @After fun tearDown() { db.close() }

    private fun row(id: String, nisn: String, at: Long) = runBlocking {
        db.outboxDao().insert(OutboxEntity(id, "ANSWER", "{}", at, 0, nisn))
    }
    private fun run() = runBlocking { TestListenableWorkerBuilder<SyncWorker>(app).build().doWork() }
    private fun pendingIds() = runBlocking { db.outboxDao().pending(100).map { it.id } }

    @Test fun rowsOfAAreSentWithTokenOfAAfterBLogsIn() {
        row("a1", "A", 1)
        row("b1", "B", 2)
        app.session.rememberToken("A", "tokA")
        app.session.nisn = "B"; app.session.token = "tokB"; app.session.rememberToken("B", "tokB")
        run()
        assertEquals(setOf("tokA" to listOf("a1"), "tokB" to listOf("b1")), sent.toSet())
    }

    @Test fun logoutKeepsTokenWhileRowsPending() {
        app.session.nisn = "A"; app.session.token = "tokA"; app.session.rememberToken("A", "tokA")
        row("a1", "A", 1)
        app.session.clear()
        assertEquals("tokA", app.session.tokenFor("A"))
    }

    @Test fun tokenForgottenWhenOutboxOfNisnEmpty() {
        app.session.rememberToken("A", "tokA")
        row("a1", "A", 1)
        run()
        assertEquals(emptyList<String>(), pendingIds())
        assertNull(app.session.tokenFor("A"))
    }

    @Test fun nisnWithoutTokenIsSkippedAndKept() {
        row("a1", "A", 1)
        row("b1", "B", 2)
        app.session.rememberToken("B", "tokB")
        run()
        assertEquals(listOf("tokB" to listOf("b1")), sent)
        assertEquals(listOf("a1"), pendingIds())
    }
}
