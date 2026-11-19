package id.sch.man3merangin.ujian.data

import id.sch.man3merangin.ujian.data.crypto.Envelope
import id.sch.man3merangin.ujian.data.db.AnswerEntity
import id.sch.man3merangin.ujian.data.db.AppDatabase
import id.sch.man3merangin.ujian.data.db.ExamPackageEntity
import id.sch.man3merangin.ujian.data.net.ExamApi
import id.sch.man3merangin.ujian.time.TrustedClock
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

sealed interface DownloadResult {
    data class Success(val examId: String) : DownloadResult
    data class Error(val message: String) : DownloadResult
}

class ExamRepository(
    private val api: ExamApi,
    private val db: AppDatabase,
    private val clock: TrustedClock
) {
    private val json = Json { ignoreUnknownKeys = true }

    fun packages(): Flow<List<ExamPackageEntity>> = db.examPackageDao().getAllFlow()

    suspend fun refresh(): Int {
        var count = 0
        try {
            val serverTime = api.serverTime()
            clock.sync(serverTime)

            val remoteExams = api.listExams()
            db.examPackageDao().setPublished(remoteExams.map { it.examId })
            for (exam in remoteExams) {
                val local = db.examPackageDao().getById(exam.examId)
                if (local == null || exam.version > local.version) {
                    val env = api.downloadPackage(exam.examId)
                    val entity = ExamPackageEntity(
                        examId = exam.examId,
                        title = env.title,
                        startAt = env.startAt,
                        endAt = env.endAt,
                        envelopeJson = json.encodeToString(env),
                        version = exam.version
                    )
                    db.examPackageDao().insert(entity)
                    try {
                        api.ready(exam.examId, exam.version)
                    } catch (_: Exception) {}
                    count++
                }
            }
        } catch (_: Exception) {}
        return count
    }

    suspend fun downloadNow(examId: String, version: Int): DownloadResult {
        return try {
            val env = api.downloadPackage(examId)
            val entity = ExamPackageEntity(
                examId = examId,
                title = env.title,
                startAt = env.startAt,
                endAt = env.endAt,
                envelopeJson = json.encodeToString(env),
                version = version
            )
            db.examPackageDao().insert(entity)
            try {
                api.ready(examId, version)
            } catch (_: Exception) {}
            DownloadResult.Success(examId)
        } catch (e: Exception) {
            DownloadResult.Error(e.message ?: "Gagal mengunduh paket ujian")
        }
    }

    suspend fun restoreAnswers(examId: String, nisn: String) {
        try {
            val remote = api.myAnswers(examId)
            val localAnswers = db.answerDao().forUser(examId, nisn).associateBy { it.questionId }

            for (rem in remote.answers) {
                val loc = localAnswers[rem.questionId]
                if (loc == null || rem.answeredAt > loc.answeredAt) {
                    val joined = rem.selected.sorted().joinToString(",")
                    db.answerDao().upsert(
                        AnswerEntity(
                            examId = examId,
                            nisn = nisn,
                            questionId = rem.questionId,
                            selected = joined,
                            answeredAt = rem.answeredAt
                        )
                    )
                }
            }
        } catch (_: Exception) {}
    }
}
