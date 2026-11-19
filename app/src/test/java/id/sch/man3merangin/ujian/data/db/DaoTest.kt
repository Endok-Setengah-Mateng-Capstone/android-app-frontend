package id.sch.man3merangin.ujian.data.db

import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class DaoTest {
    private lateinit var db: AppDatabase

    @Before fun setUp() { db = AppDatabase.inMemory(RuntimeEnvironment.getApplication()) }
    @After fun tearDown() { db.close() }

    private fun answer(nisn: String, selected: String, at: Long) = AnswerEntity("e1", nisn, "q1", selected, at)

    @Test fun answerUpsertKeepsLatest() = runBlocking {
        db.answerDao().upsert(answer("A", "a", 1))
        db.answerDao().upsert(answer("A", "b", 2))
        val rows = db.answerDao().forUser("e1", "A")
        assertEquals(1, rows.size)
        assertEquals("b", rows[0].selected)
    }

    @Test fun outboxPendingOrderedByCreatedAt() = runBlocking {
        val dao = db.outboxDao()
        dao.insert(OutboxEntity("x", "ANSWER", "{}", 30))
        dao.insert(OutboxEntity("y", "ANSWER", "{}", 10))
        dao.insert(OutboxEntity("z", "ANSWER", "{}", 20))
        assertEquals(listOf("y", "z", "x"), dao.pending(10).map { it.id })
        assertEquals(listOf("y", "z"), dao.pending(2).map { it.id })
        dao.bumpAttempts(listOf("y"))
        assertEquals(1, dao.pending(1)[0].attempts)
        dao.delete(listOf("y", "z"))
        assertEquals(listOf("x"), dao.pending(10).map { it.id })
    }

    @Test fun answersIsolatedPerNisn() = runBlocking {
        db.answerDao().upsert(answer("A", "a", 1))
        assertEquals(0, db.answerDao().forUser("e1", "B").size)
    }
}
