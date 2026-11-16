package id.sch.man3merangin.ujian.exam

import android.os.SystemClock
import id.sch.man3merangin.ujian.data.AnswerRepository
import id.sch.man3merangin.ujian.data.db.ExamSessionEntity
import id.sch.man3merangin.ujian.time.TrustedClock

interface SessionStore {
    fun get(examId: String, nisn: String): ExamSessionEntity?
    fun put(session: ExamSessionEntity)
}

class ExamTimer(
    private val clock: TrustedClock,
    private val elapsed: () -> Long = { SystemClock.elapsedRealtime() },
    private val wall: () -> Long = { System.currentTimeMillis() },
    private val store: SessionStore
) {
    fun remainingMs(examId: String, nisn: String, endAt: Long): Long {
        val serverNow = clock.nowMs()
        if (serverNow != null) {
            val rem = (endAt - serverNow).coerceAtLeast(0L)
            persist(examId, nisn, rem, clockUnverified = false)
            return rem
        }

        // Unsynced clock fallback
        val session = store.get(examId, nisn)
        val curElapsed = elapsed()
        val curWall = wall()
        val wallCap = (endAt - curWall).coerceAtLeast(0L)

        if (session == null) {
            val fallback = wallCap
            persist(examId, nisn, fallback, clockUnverified = true)
            return fallback
        }

        val remaining = if (curElapsed < session.snapshotElapsed) {
            // Reboot detected
            session.remainingMs
        } else {
            session.remainingMs - (curElapsed - session.snapshotElapsed)
        }

        val effective = remaining.coerceAtLeast(0L).coerceAtMost(wallCap)
        persist(examId, nisn, effective, clockUnverified = true)
        return effective
    }

    fun persist(examId: String, nisn: String, remainingMs: Long, clockUnverified: Boolean = false) {
        store.put(
            ExamSessionEntity(
                examId = examId,
                nisn = nisn,
                remainingMs = remainingMs,
                snapshotElapsed = elapsed(),
                clockUnverified = clockUnverified
            )
        )
    }
}

object AutoSubmit {
    private val submittedMap = mutableSetOf<String>()

    suspend fun runIfDue(
        examId: String,
        nisn: String,
        timer: ExamTimer,
        endAt: Long,
        answers: AnswerRepository
    ): Boolean {
        val key = "$examId:$nisn"
        if (submittedMap.contains(key)) return false

        val remaining = timer.remainingMs(examId, nisn, endAt)
        if (remaining <= 0) {
            answers.submit(examId, nisn)
            submittedMap.add(key)
            return true
        }
        return false
    }
}
