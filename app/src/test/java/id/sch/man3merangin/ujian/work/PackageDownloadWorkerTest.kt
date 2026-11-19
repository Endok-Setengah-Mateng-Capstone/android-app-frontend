package id.sch.man3merangin.ujian.work

import id.sch.man3merangin.ujian.UjianApplication
import id.sch.man3merangin.ujian.data.ExamRepository
import id.sch.man3merangin.ujian.data.crypto.Envelope
import id.sch.man3merangin.ujian.data.db.AppDatabase
import id.sch.man3merangin.ujian.data.net.ExamApi
import id.sch.man3merangin.ujian.data.net.ExamSummary
import id.sch.man3merangin.ujian.data.net.FakeExamApi
import id.sch.man3merangin.ujian.time.MemoryClockStore
import id.sch.man3merangin.ujian.time.TrustedClock
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = UjianApplication::class)
class PackageDownloadWorkerTest {
    private lateinit var db: AppDatabase
    private lateinit var fake: FakeExamApi
    private lateinit var clock: TrustedClock
    private var listing = mutableListOf<ExamSummary>()
    private var downloads = 0
    private var fail = false

    private fun exam(id: String, v: Int) = ExamSummary(id, "T$id", v, 0, 1)
    private val api = object : ExamApi by (FakeExamApi().also { fake = it }) {
        override suspend fun listExams(): List<ExamSummary> = if (fail) throw IOException("down") else listing
        override suspend fun serverTime(): Long = if (fail) throw IOException("down") else 5_000_000L
        override suspend fun downloadPackage(examId: String): Envelope { downloads++; return fake.downloadPackage(examId) }
    }
    private lateinit var repo: ExamRepository

    @Before fun setUp() {
        db = AppDatabase.inMemory(RuntimeEnvironment.getApplication())
        clock = TrustedClock(elapsed = { 1_000L }, store = MemoryClockStore())
        repo = ExamRepository(api, db, clock)
    }
    @After fun tearDown() { db.close() }

    @Test fun downloadsNewExamOnly() = runBlocking {
        listing = mutableListOf(exam("e1", 1))
        assertEquals(1, repo.refresh())
        assertEquals(1, downloads)
        assertNotNull(db.examPackageDao().getById("e1"))
    }

    @Test fun skipsWhenVersionUnchanged() = runBlocking {
        listing = mutableListOf(exam("e1", 1))
        repo.refresh()
        assertEquals(0, repo.refresh())
        assertEquals(1, downloads)
    }

    @Test fun replacesWhenVersionBumped() = runBlocking {
        listing = mutableListOf(exam("e1", 1))
        repo.refresh()
        listing = mutableListOf(exam("e1", 2))
        assertEquals(1, repo.refresh())
        assertEquals(2, db.examPackageDao().getById("e1")!!.version)
        assertEquals(1, db.examPackageDao().getAll().size)
    }

    @Test fun networkFailureKeepsExistingPackages() = runBlocking {
        listing = mutableListOf(exam("e1", 1))
        repo.refresh()
        fail = true
        assertEquals(0, repo.refresh())
        assertEquals(1, repo.packages().first().size)
    }

    @Test fun successfulPullSyncsClock() = runBlocking {
        assertNull(clock.nowMs())
        listing = mutableListOf()
        repo.refresh()
        assertEquals(5_000_000L, clock.nowMs())
    }

    @Test fun unpublishedExamHiddenButPackageKept() = runBlocking {
        listing = mutableListOf(exam("e1", 1), exam("e2", 1))
        repo.refresh()
        listing = mutableListOf(exam("e2", 1))
        repo.refresh()
        assertEquals(listOf("e2"), repo.packages().first().map { it.examId })
        assertNotNull(db.examPackageDao().getById("e1"))
        listing = mutableListOf(exam("e1", 1), exam("e2", 1))
        repo.refresh()
        assertEquals(2, repo.packages().first().size)
    }
}
