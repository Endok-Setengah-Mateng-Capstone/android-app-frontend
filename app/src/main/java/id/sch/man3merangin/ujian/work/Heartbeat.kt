package id.sch.man3merangin.ujian.work

import id.sch.man3merangin.ujian.data.AnswerRepository
import id.sch.man3merangin.ujian.data.net.ExamApi
import id.sch.man3merangin.ujian.time.TrustedClock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class Heartbeat(
    private val api: ExamApi,
    private val answers: AnswerRepository,
    private val clock: TrustedClock,
    private val scope: CoroutineScope
) {
    private var job: Job? = null

    fun start(examId: String, nisn: String) {
        stop()
        job = scope.launch {
            while (isActive) {
                delay(30_000L)
                try {
                    val answeredCount = answers.load(examId, nisn).count { it.value.isNotEmpty() }
                    val serverTime = api.heartbeat(examId, answeredCount)
                    clock.sync(serverTime)
                } catch (_: Exception) {
                    // Ignore failure, do not queue
                }
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
    }
}
