package id.sch.man3merangin.ujian.data

import androidx.room.withTransaction
import id.sch.man3merangin.ujian.data.db.AppDatabase
import id.sch.man3merangin.ujian.data.db.OutboxEntity
import id.sch.man3merangin.ujian.data.db.ViolationEntity
import id.sch.man3merangin.ujian.violation.Detected
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

@Serializable
data class ViolationPayload(
    val examId: String,
    val category: String,
    val startedAt: Long,
    val durationMs: Long,
    val questionNo: Int
)

class ViolationRepository(
    private val db: AppDatabase,
    private val uuid: () -> String = { UUID.randomUUID().toString() }
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun record(examId: String, nisn: String, d: Detected, questionNo: Int) = withContext(Dispatchers.IO) {
        val violationId = uuid()
        val violationEntity = ViolationEntity(
            id = violationId,
            examId = examId,
            nisn = nisn,
            category = d.category.name,
            startedAt = d.startedAt,
            durationMs = d.durationMs,
            questionNo = questionNo
        )

        val payload = ViolationPayload(
            examId = examId,
            category = d.category.name,
            startedAt = d.startedAt,
            durationMs = d.durationMs,
            questionNo = questionNo
        )

        val outboxEntity = OutboxEntity(
            id = violationId, // server uses the item id as the violation PK
            kind = "VIOLATION",
            payloadJson = json.encodeToString(payload),
            createdAt = d.startedAt,
            nisn = nisn
        )

        db.withTransaction {
            db.violationDao().insert(violationEntity)
            db.outboxDao().insert(outboxEntity)
        }
    }
}
