package id.sch.man3merangin.ujian.data.net

import id.sch.man3merangin.ujian.data.crypto.Envelope
import kotlinx.serialization.Serializable

class ApiException(val code: Int, message: String = "HTTP $code") : Exception(message)
class DeviceMismatchException : Exception("Perangkat ini belum diizinkan. Hubungi pengawas.")

@Serializable
data class LoginResult(
    val sessionToken: String,
    val nisn: String,
    val name: String
)

@Serializable
data class ExamSummary(
    val examId: String,
    val title: String = "Ujian Madrasah",
    val version: Int,
    val startAt: Long,
    val endAt: Long
)

@Serializable
data class SyncItem(
    val id: String,
    val kind: String, // ANSWER, VIOLATION, SUBMIT
    val payloadJson: String
)

@Serializable
data class SyncResult(
    val acceptedIds: List<String>
)

@Serializable
data class RemoteAnswer(
    val questionId: String,
    val selected: List<String>,
    val answeredAt: Long
)

@Serializable
data class RemoteAnswers(
    val answers: List<RemoteAnswer>,
    val submittedAt: Long?
)

interface ExamApi {
    suspend fun login(nisn: String, password: String): LoginResult
    suspend fun listExams(): List<ExamSummary>
    suspend fun downloadPackage(examId: String): Envelope
    suspend fun sync(batch: List<SyncItem>): SyncResult
    suspend fun serverTime(): Long
    suspend fun ready(examId: String, version: Int)
    suspend fun heartbeat(examId: String, answeredCount: Int): Long
    suspend fun myAnswers(examId: String): RemoteAnswers
}
