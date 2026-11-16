package id.sch.man3merangin.ujian.exam

import id.sch.man3merangin.ujian.data.db.ExamSessionEntity
import id.sch.man3merangin.ujian.time.MemoryClockStore
import id.sch.man3merangin.ujian.time.TrustedClock
import org.junit.Assert.assertEquals
import org.junit.Test

class ExamTimerTest {

    private class FakeSessionStore : SessionStore {
        val map = mutableMapOf<String, ExamSessionEntity>()
        override fun get(examId: String, nisn: String): ExamSessionEntity? = map["$examId:$nisn"]
        override fun put(session: ExamSessionEntity) {
            map["${session.examId}:${session.nisn}"] = session
        }
    }

    @Test
    fun syncedUsesServerOffset() {
        var fakeElapsed = 10_000L
        val clockStore = MemoryClockStore()
        val clock = TrustedClock(elapsed = { fakeElapsed }, store = clockStore)
        val sessionStore = FakeSessionStore()

        val timer = ExamTimer(
            clock = clock,
            elapsed = { fakeElapsed },
            wall = { 1_000_000L },
            store = sessionStore
        )

        clock.sync(serverNowMs = 1_000_000L)
        val endAt = 1_060_000L // 60s window

        val remaining1 = timer.remainingMs("exam-1", "nisn-1", endAt)
        assertEquals(60_000L, remaining1)

        fakeElapsed += 20_000L // 20s pass
        val remaining2 = timer.remainingMs("exam-1", "nisn-1", endAt)
        assertEquals(40_000L, remaining2)
    }

    @Test
    fun unsyncedSameBootCountsDown() {
        var fakeElapsed = 10_000L
        val clockStore = MemoryClockStore() // unsynced
        val clock = TrustedClock(elapsed = { fakeElapsed }, store = clockStore)
        val sessionStore = FakeSessionStore()

        val timer = ExamTimer(
            clock = clock,
            elapsed = { fakeElapsed },
            wall = { 1_000_000L },
            store = sessionStore
        )

        val endAt = 1_060_000L // 60s
        val rem1 = timer.remainingMs("exam-1", "nisn-1", endAt)
        assertEquals(60_000L, rem1)

        fakeElapsed += 15_000L // 15s pass in same boot
        val rem2 = timer.remainingMs("exam-1", "nisn-1", endAt)
        assertEquals(45_000L, rem2)
    }

    @Test
    fun rebootResumesWithStoredRemaining() {
        var fakeElapsed = 50_000L
        val clockStore = MemoryClockStore()
        val clock = TrustedClock(elapsed = { fakeElapsed }, store = clockStore)
        val sessionStore = FakeSessionStore()

        var fakeWall = 1_000_000L
        val endAt = 1_060_000L

        val timer1 = ExamTimer(clock, { fakeElapsed }, { fakeWall }, sessionStore)
        timer1.remainingMs("exam-1", "nisn-1", endAt) // stores remaining 60_000L at snapshot 50_000L

        // Reboot: elapsed drops to 5_000L (< 50_000L)
        fakeElapsed = 5_000L
        fakeWall += 2_000L // phone off for 2s, but unsynced keeps stored remaining (time while phone off not counted)
        val timer2 = ExamTimer(clock, { fakeElapsed }, { fakeWall }, sessionStore)

        val remAfterReboot = timer2.remainingMs("exam-1", "nisn-1", endAt)
        assertEquals(58_000L, remAfterReboot) // capped by wallClock (endAt - fakeWall = 1_060_000 - 1_002_000 = 58_000L)
    }
}
