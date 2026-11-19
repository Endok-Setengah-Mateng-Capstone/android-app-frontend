package id.sch.man3merangin.ujian.violation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ViolationTrackerTest {

    @Test
    fun leaveThenStartIsAppExitWithDuration() {
        val tracker = ViolationTracker()
        tracker.onSignal(Signal.UserLeaveHint, 1000L)
        val detected = tracker.onSignal(Signal.Started, 4500L)

        assertEquals(1, detected.size)
        assertEquals(ViolationCategory.APP_EXIT, detected[0].category)
        assertEquals(3500L, detected[0].durationMs)
    }

    @Test
    fun focusLossWithoutStopIsNotificationPanel() {
        val tracker = ViolationTracker()
        tracker.onSignal(Signal.FocusLost, 2000L)
        val detected = tracker.onSignal(Signal.FocusGained, 4000L)

        assertEquals(1, detected.size)
        assertEquals(ViolationCategory.NOTIFICATION_PANEL, detected[0].category)
        assertEquals(2000L, detected[0].durationMs)
    }

    @Test
    fun ringingBeforeLeaveBecomesIncomingCall() {
        val tracker = ViolationTracker()
        tracker.onSignal(Signal.PhoneRinging, 1000L)
        tracker.onSignal(Signal.UserLeaveHint, 2000L)
        val detected = tracker.onSignal(Signal.Started, 5000L)

        assertEquals(1, detected.size)
        assertEquals(ViolationCategory.INCOMING_CALL, detected[0].category)
        assertFalse(detected[0].category.isViolation)
    }

    @Test
    fun rotationNeverOpensSpan() {
        val tracker = ViolationTracker()
        val detectedRotation = tracker.onSignal(Signal.ConfigRotation, 1000L)
        assertEquals(1, detectedRotation.size)
        assertEquals(ViolationCategory.ROTATION, detectedRotation[0].category)

        tracker.onSignal(Signal.FocusLost, 1100L)
        val detected = tracker.onSignal(Signal.FocusGained, 1300L)
        assertEquals(0, detected.size)
    }

    @Test
    fun splitScreenEmitsOnExitWithDuration() {
        val tracker = ViolationTracker()
        val instant = tracker.onSignal(Signal.MultiWindow(true), 1000L)
        assertEquals(1, instant.size)
        assertEquals(ViolationCategory.SPLIT_SCREEN, instant[0].category)

        val closed = tracker.onSignal(Signal.MultiWindow(false), 5000L)
        assertEquals(1, closed.size)
        assertEquals(ViolationCategory.SPLIT_SCREEN, closed[0].category)
        assertEquals(4000L, closed[0].durationMs)
    }

    @Test
    fun categoriesFlagAlarmCorrectly() {
        assertTrue(ViolationCategory.APP_EXIT.isViolation)
        assertTrue(ViolationCategory.SPLIT_SCREEN.isViolation)
        assertTrue(ViolationCategory.FLOATING_PIP.isViolation)
        assertTrue(ViolationCategory.NOTIFICATION_PANEL.isViolation)
        assertTrue(ViolationCategory.COPY_PASTE.isViolation)

        assertFalse(ViolationCategory.INCOMING_CALL.isViolation)
        assertFalse(ViolationCategory.ROTATION.isViolation)
        assertFalse(ViolationCategory.NETWORK_LOST.isViolation)
        assertFalse(ViolationCategory.LOW_BATTERY.isViolation)
        assertFalse(ViolationCategory.CLOCK_UNVERIFIED.isViolation)
    }

    @Test
    fun PipEntersAndExitsEmitsFloatingPip() {
        val tracker = ViolationTracker()
        val entered = tracker.onSignal(Signal.Pip(true), 1000L)
        assertEquals(listOf(ViolationCategory.FLOATING_PIP), entered.map { it.category })
        assertEquals(0, tracker.onSignal(Signal.Pip(false), 3000L).size)
    }

    @Test
    fun ringingOlderThanFiveSecondsDoesNotReclassify() {
        val tracker = ViolationTracker()
        tracker.onSignal(Signal.PhoneRinging, 1000L)
        tracker.onSignal(Signal.UserLeaveHint, 7000L)
        val detected = tracker.onSignal(Signal.Started, 9000L)
        assertEquals(ViolationCategory.APP_EXIT, detected.single().category)
    }

    @Test
    fun doubleStopDoesNotOpenTwoSpans() {
        val tracker = ViolationTracker()
        tracker.onSignal(Signal.UserLeaveHint, 1000L)
        tracker.onSignal(Signal.Stopped, 1100L)
        val detected = tracker.onSignal(Signal.Started, 4500L)
        assertEquals(1, detected.size)
        assertEquals(1000L, detected[0].startedAt)
        assertEquals(3500L, detected[0].durationMs)
        assertEquals(0, tracker.onSignal(Signal.Started, 5000L).size)
    }

    @Test
    fun clipboardIsInstantCopyPaste() {
        val d = ViolationTracker().onSignal(Signal.ClipboardChanged, 1234L).single()
        assertEquals(ViolationCategory.COPY_PASTE, d.category)
        assertEquals(1234L, d.startedAt)
        assertEquals(0L, d.durationMs)
    }
}
