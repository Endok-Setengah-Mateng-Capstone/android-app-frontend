package id.sch.man3merangin.ujian.data

import androidx.room.withTransaction
import id.sch.man3merangin.ujian.data.db.AnswerEntity
import id.sch.man3merangin.ujian.data.db.AppDatabase
import id.sch.man3merangin.ujian.data.db.OutboxEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

class ExamEndedException : Exception("Waktu ujian telah berakhir")

@Serializable
data class AnswerPayload(
    val examId: String,
    val questionId: String,
    val selected: List<String>,
    val answeredAt: Long
)

@Serializable
data class SubmitPayload(
    val examId: String,
    val submittedAt: Long
)

class AnswerRepository(
    private val db: AppDatabase,
    // TODO(Task 10b): inject TrustedClock-based time
    private val clock: () -> Long = { System.currentTimeMillis() },
    private val uuid: () -> String = { UUID.randomUUID().toString() }
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun save(examId: String, nisn: String, questionId: String, selected: Set<String>) = withContext(Dispatchers.IO) {
        val now = clock()
        val sortedList = selected.sorted()
        val joined = sortedList.joinToString(",")

        val answerEntity = AnswerEntity(
            examId = examId,
            nisn = nisn,
            questionId = questionId,
            selected = joined,
            answeredAt = now
        )

        val payload = AnswerPayload(
            examId = examId,
            questionId = questionId,
            selected = sortedList,
            answeredAt = now
        )

        val outboxEntity = OutboxEntity(
            id = uuid(),
            kind = "ANSWER",
            payloadJson = json.encodeToString(payload),
            createdAt = now,
            nisn = nisn
        )

        // withTransaction = suspend-friendly runInTransaction: one atomic Room transaction
        db.withTransaction {
            db.answerDao().upsert(answerEntity)
            db.outboxDao().insert(outboxEntity)
        }
    }

    suspend fun load(examId: String, nisn: String): Map<String, Set<String>> = withContext(Dispatchers.IO) {
        val list = db.answerDao().forUser(examId, nisn)
        list.associate { entity ->
            val set = if (entity.selected.isBlank()) {
                emptySet()
            } else {
                entity.selected.split(",").filter { it.isNotBlank() }.toSet()
            }
            entity.questionId to set
        }
    }

    suspend fun submit(examId: String, nisn: String) = withContext(Dispatchers.IO) {
        val now = clock()
        val payload = SubmitPayload(
            examId = examId,
            submittedAt = now
        )
        val outboxEntity = OutboxEntity(
            id = uuid(),
            kind = "SUBMIT",
            payloadJson = json.encodeToString(payload),
            createdAt = now,
            nisn = nisn
        )
        db.outboxDao().insert(outboxEntity)
    }
}
