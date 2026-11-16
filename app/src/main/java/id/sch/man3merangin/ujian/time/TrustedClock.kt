package id.sch.man3merangin.ujian.time

import android.os.SystemClock

enum class WindowState {
    NOT_SYNCED,
    BEFORE,
    OPEN,
    ENDED
}

interface ClockStore {
    var serverMs: Long
    var elapsedAtSync: Long
    var bootId: String?
}

class MemoryClockStore(
    override var serverMs: Long = 0L,
    override var elapsedAtSync: Long = 0L,
    override var bootId: String? = null
) : ClockStore

class TrustedClock(
    private val elapsed: () -> Long = { SystemClock.elapsedRealtime() },
    private val store: ClockStore
) {
    fun sync(serverNowMs: Long) {
        store.serverMs = serverNowMs
        store.elapsedAtSync = elapsed()
    }

    fun nowMs(): Long? {
        if (store.serverMs <= 0L || store.elapsedAtSync <= 0L) {
            return null
        }
        val currentElapsed = elapsed()
        if (currentElapsed < store.elapsedAtSync) {
            // Device rebooted; elapsed reset
            return null
        }
        return store.serverMs + (currentElapsed - store.elapsedAtSync)
    }

    fun windowState(startAt: Long, endAt: Long): WindowState {
        val currentNow = nowMs() ?: return WindowState.NOT_SYNCED
        return when {
            currentNow < startAt -> WindowState.BEFORE
            currentNow > endAt -> WindowState.ENDED
            else -> WindowState.OPEN
        }
    }
}
