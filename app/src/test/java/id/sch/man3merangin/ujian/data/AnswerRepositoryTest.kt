package id.sch.man3merangin.ujian.data

import android.content.Context
import androidx.room.Room
import id.sch.man3merangin.ujian.data.db.AppDatabase
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AnswerRepositoryTest {
    private val ctx: Context = RuntimeEnvironment.getApplication()
    private var db = AppDatabase.inMemory(ctx)
    private var n = 0
    private fun repo(d: AppDatabase = db) = AnswerRepository(d, { 1000L }, { "u${n++}" })

    @After fun tearDown() { db.close() }

    @Test fun saveWritesAnswerAndOutboxAtomically() = runBlocking {
        repo().save("e1", "A", "q1", setOf("b", "a"))
        assertEquals("a,b", db.answerDao().forUser("e1", "A").single().selected)
        val o = db.outboxDao().pending(10).single()
        assertEquals("ANSWER", o.kind)
        assertEquals("u0", o.id)
        val j = Json.parseToJsonElement(o.payloadJson).jsonObject
        assertEquals(setOf("examId", "questionId", "selected", "answeredAt"), j.keys)
        assertEquals(listOf("a", "b"), j["selected"]!!.jsonArray.map { it.jsonPrimitive.content })
        assertEquals("1000", j["answeredAt"]!!.jsonPrimitive.content)
    }

    @Test fun survivesDbReopen() = runBlocking {
        db.close()
        val file = "reopen-test.db"
        fun open() = Room.databaseBuilder(ctx, AppDatabase::class.java, file).allowMainThreadQueries().build()
        var d = open()
        repo(d).save("e1", "A", "q1", setOf("a"))
        d.close()
        d = open()
        db = d
        assertEquals(mapOf("q1" to setOf("a")), repo(d).load("e1", "A"))
        assertEquals(1, d.outboxDao().count())
    }

    @Test fun emptySelectionIsStoredAsNone() = runBlocking {
        val r = repo()
        r.save("e1", "A", "q1", setOf("a", "b"))
        r.save("e1", "A", "q1", emptySet())
        assertEquals(mapOf("q1" to emptySet<String>()), r.load("e1", "A"))
        assertEquals("", db.answerDao().forUser("e1", "A").single().selected)
        assertEquals(2, db.outboxDao().count())
        assertEquals(2, db.outboxDao().pending(10).map { it.id }.toSet().size)
    }

    @Test fun loadIsolatedPerNisn() = runBlocking {
        val r = repo()
        r.save("e1", "A", "q1", setOf("a"))
        assertTrue(r.load("e1", "B").isEmpty())
    }

    @Test fun submitEnqueuesSubmitItem() = runBlocking {
        repo().submit("e1", "A")
        val o = db.outboxDao().pending(10).single()
        assertEquals("SUBMIT", o.kind)
        val j = Json.parseToJsonElement(o.payloadJson).jsonObject
        assertEquals(setOf("examId", "submittedAt"), j.keys)
    }
}
