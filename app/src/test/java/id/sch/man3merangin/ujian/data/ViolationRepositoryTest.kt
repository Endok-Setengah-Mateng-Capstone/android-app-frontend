package id.sch.man3merangin.ujian.data

import androidx.room.Room
import id.sch.man3merangin.ujian.data.db.AppDatabase
import id.sch.man3merangin.ujian.violation.Detected
import id.sch.man3merangin.ujian.violation.ViolationCategory
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ViolationRepositoryTest {
    private val db = AppDatabase.inMemory(RuntimeEnvironment.getApplication())
    @After fun tearDown() { db.close() }

    @Test fun recordWritesViolationAndOutbox() = runBlocking {
        var n = 0
        ViolationRepository(db) { "u${n++}" }
            .record("e1", "A", Detected(ViolationCategory.APP_EXIT, 10L, 20L), 3)
        val v = db.violationDao().forUser("e1", "A").single()
        val o = db.outboxDao().pending(10).single()
        assertEquals("VIOLATION", o.kind)
        assertEquals(v.id, o.id)
        assertEquals("A", o.nisn)
        assertEquals("A", v.nisn)
        val j = Json.parseToJsonElement(o.payloadJson).jsonObject
        assertEquals(setOf("examId", "category", "startedAt", "durationMs", "questionNo"), j.keys)
        assertEquals("APP_EXIT", j["category"]!!.jsonPrimitive.content)
        assertEquals(10L, j["startedAt"]!!.jsonPrimitive.long)
        assertEquals(20L, j["durationMs"]!!.jsonPrimitive.long)
        assertEquals(3, j["questionNo"]!!.jsonPrimitive.int)
    }
}
