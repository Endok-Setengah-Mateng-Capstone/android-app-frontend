package id.sch.man3merangin.ujian.time

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TrustedClockTest {

    @Test
    fun nowAdvancesWithElapsedNotWallClock() {
        var fakeElapsed = 10_000L
        val store = MemoryClockStore()
        val clock = TrustedClock(elapsed = { fakeElapsed }, store = store)

        clock.sync(serverNowMs = 1_700_000_000L)
        assertEquals(1_700_000_000L, clock.nowMs())

        fakeElapsed += 5_000L
        assertEquals(1_700_005_000L, clock.nowMs())
    }

    @Test
    fun neverSyncedGivesNotSynced() {
        val store = MemoryClockStore()
        val clock = TrustedClock(elapsed = { 10_000L }, store = store)

        assertNull(clock.nowMs())
        assertEquals(WindowState.NOT_SYNCED, clock.windowState(100L, 500L))
    }

    @Test
    fun windowOpenBetweenStartAndEnd() {
        var fakeElapsed = 10_000L
        val store = MemoryClockStore()
        val clock = TrustedClock(elapsed = { fakeElapsed }, store = store)

        clock.sync(serverNowMs = 2_000L)
        assertEquals(WindowState.BEFORE, clock.windowState(3_000L, 10_000L))

        fakeElapsed += 2_000L // now is 4_000L
        assertEquals(WindowState.OPEN, clock.windowState(3_000L, 10_000L))

        fakeElapsed += 10_000L // now is 14_000L
        assertEquals(WindowState.ENDED, clock.windowState(3_000L, 10_000L))
    }

    @Test
    fun rebootResetsToNotSynced() {
        var fakeElapsed = 50_000L
        val store = MemoryClockStore()
        val clock = TrustedClock(elapsed = { fakeElapsed }, store = store)

        clock.sync(serverNowMs = 1_000_000L)
        assertEquals(1_000_000L, clock.nowMs())

        // Simulated phone reboot: elapsed drops below elapsedAtSync
        fakeElapsed = 5_000L
        assertNull(clock.nowMs())
    }
}
