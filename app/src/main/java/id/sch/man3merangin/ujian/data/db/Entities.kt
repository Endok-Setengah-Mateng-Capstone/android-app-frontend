package id.sch.man3merangin.ujian.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exam_packages")
data class ExamPackageEntity(
    @PrimaryKey val examId: String,
    val title: String,
    val startAt: Long,
    val endAt: Long,
    val envelopeJson: String,
    val version: Int
)

@Entity(
    tableName = "answers",
    primaryKeys = ["examId", "nisn", "questionId"]
)
data class AnswerEntity(
    val examId: String,
    val nisn: String,
    val questionId: String,
    val selected: String, // Comma-joined sorted option IDs, "" for none
    val answeredAt: Long
)

@Entity(tableName = "violations")
data class ViolationEntity(
    @PrimaryKey val id: String,
    val examId: String,
    val nisn: String,
    val category: String,
    val startedAt: Long,
    val durationMs: Long,
    val questionNo: Int
)

@Entity(tableName = "sync_outbox")
data class OutboxEntity(
    @PrimaryKey val id: String,
    val kind: String, // ANSWER, VIOLATION, SUBMIT
    val payloadJson: String,
    val createdAt: Long,
    val attempts: Int = 0,
    val nisn: String = "" // owner of the row; '' only for rows created before migration 1->2
)

@Entity(
    tableName = "exam_sessions",
    primaryKeys = ["examId", "nisn"]
)
data class ExamSessionEntity(
    val examId: String,
    val nisn: String,
    val remainingMs: Long,
    val snapshotElapsed: Long,
    val clockUnverified: Boolean
)
