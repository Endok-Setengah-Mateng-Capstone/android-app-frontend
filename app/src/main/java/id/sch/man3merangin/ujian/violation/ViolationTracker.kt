package id.sch.man3merangin.ujian.violation

enum class ViolationCategory(val isViolation: Boolean) {
    APP_EXIT(true),
    SPLIT_SCREEN(true),
    FLOATING_PIP(true),
    NOTIFICATION_PANEL(true),
    COPY_PASTE(true),

    INCOMING_CALL(false),
    ROTATION(false),
    NETWORK_LOST(false),
    LOW_BATTERY(false),
    CLOCK_UNVERIFIED(false)
}

sealed interface Signal {
    data object UserLeaveHint : Signal
    data object Stopped : Signal
    data object Started : Signal
    data object FocusLost : Signal
    data object FocusGained : Signal
    data class MultiWindow(val inMulti: Boolean) : Signal
    data class Pip(val inPip: Boolean) : Signal
    data object ClipboardChanged : Signal
    data object ConfigRotation : Signal
    data object PhoneRinging : Signal
    data object NetworkLost : Signal
}

data class Detected(
    val category: ViolationCategory,
    val startedAt: Long,
    val durationMs: Long
)

class ViolationTracker {
    private var awayStartMs: Long? = null
    private var focusLostMs: Long? = null
    private var multiWindowStartMs: Long? = null
    private var lastPhoneRingingMs: Long? = null
    private var isRotating = false

    fun resetFocusLost() {
        focusLostMs = null
    }

    fun onSignal(s: Signal, nowMs: Long): List<Detected> {
        val detected = mutableListOf<Detected>()

        when (s) {
            is Signal.PhoneRinging -> {
                lastPhoneRingingMs = nowMs
            }

            is Signal.ConfigRotation -> {
                isRotating = true
                detected.add(Detected(ViolationCategory.ROTATION, nowMs, 0L))
            }

            is Signal.UserLeaveHint, is Signal.Stopped -> {
                if (awayStartMs == null && !isRotating) {
                    awayStartMs = nowMs
                }
            }

            is Signal.Started -> {
                val start = awayStartMs
                if (start != null) {
                    val duration = (nowMs - start).coerceAtLeast(0L)
                    val phoneRecent = lastPhoneRingingMs?.let { nowMs - it < 5000L } ?: false
                    val category = if (phoneRecent) ViolationCategory.INCOMING_CALL else ViolationCategory.APP_EXIT
                    detected.add(Detected(category, start, duration))
                    awayStartMs = null
                }
                isRotating = false
                focusLostMs = null
            }

            is Signal.FocusLost -> {
                if (!isRotating) {
                    focusLostMs = nowMs
                }
            }

            is Signal.FocusGained -> {
                val fStart = focusLostMs
                if (fStart != null && awayStartMs == null && !isRotating) {
                    val duration = (nowMs - fStart).coerceAtLeast(0L)
                    val phoneRecent = lastPhoneRingingMs?.let { nowMs - it < 5000L } ?: false
                    val category = if (phoneRecent) ViolationCategory.INCOMING_CALL else ViolationCategory.NOTIFICATION_PANEL
                    if (duration >= 300L) {
                        detected.add(Detected(category, fStart, duration))
                    }
                }
                focusLostMs = null
                isRotating = false
            }

            is Signal.MultiWindow -> {
                if (s.inMulti) {
                    if (multiWindowStartMs == null) {
                        multiWindowStartMs = nowMs
                        detected.add(Detected(ViolationCategory.SPLIT_SCREEN, nowMs, 0L))
                    }
                } else {
                    val start = multiWindowStartMs
                    if (start != null) {
                        val duration = (nowMs - start).coerceAtLeast(0L)
                        detected.add(Detected(ViolationCategory.SPLIT_SCREEN, start, duration))
                        multiWindowStartMs = null
                    }
                }
            }

            is Signal.Pip -> {
                if (s.inPip) {
                    detected.add(Detected(ViolationCategory.FLOATING_PIP, nowMs, 0L))
                }
            }

            is Signal.ClipboardChanged -> {
                detected.add(Detected(ViolationCategory.COPY_PASTE, nowMs, 0L))
            }

            is Signal.NetworkLost -> {
                detected.add(Detected(ViolationCategory.NETWORK_LOST, nowMs, 0L))
            }
        }

        return detected
    }
}
