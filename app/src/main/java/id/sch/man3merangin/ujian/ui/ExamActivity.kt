package id.sch.man3merangin.ujian.ui

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import id.sch.man3merangin.ujian.ui.screens.ExamFinishedReceiptScreen
import id.sch.man3merangin.ujian.ui.screens.QuestionScreen
import id.sch.man3merangin.ujian.ui.screens.SubmitConfirmationDialog
import id.sch.man3merangin.ujian.ui.screens.ViolationWarningDialog
import id.sch.man3merangin.ujian.ui.theme.UjianAppTheme
import id.sch.man3merangin.ujian.violation.Signal
import id.sch.man3merangin.ujian.violation.ViolationCategory

class ExamActivity : ComponentActivity() {
    private val viewModel: ExamViewModel by viewModels()
    private var clipboardManager: ClipboardManager? = null
    private var clipListener: ClipboardManager.OnPrimaryClipChangedListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // FLAG_SECURE blocks screenshots, screen recording and the recents thumbnail
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        // Back during the exam counts as leaving the app instead of closing it
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val state = viewModel.uiState.value
                if (state is UiState.InExam) {
                    viewModel.triggerImmediateViolation(ViolationCategory.APP_EXIT)
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })

        // Clipboard listener
        clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        clipListener = ClipboardManager.OnPrimaryClipChangedListener {
            viewModel.onSignal(Signal.ClipboardChanged)
        }
        clipboardManager?.addPrimaryClipChangedListener(clipListener)

        val examId = intent.getStringExtra("exam_id") ?: "exam-bio-fikih-01"
        val examTitle = intent.getStringExtra("exam_title") ?: "Biologi & Fikih Lingkungan"

        viewModel.startExam(examId, examTitle)

        setContent {
            UjianAppTheme {
                val state by viewModel.uiState.collectAsState()

                when (val current = state) {
                    is UiState.InExam -> {
                        QuestionScreen(
                            examTitle = current.examTitle,
                            questions = current.questions,
                            currentIndex = current.currentIndex,
                            selectedAnswers = current.selectedAnswers,
                            doubtfulIndices = current.doubtfulIndices,
                            remainingMs = current.remainingMs,
                            onOptionSelected = { qId, optId, isMulti ->
                                viewModel.selectOption(qId, optId, isMulti)
                            },
                            onToggleDoubtful = { idx ->
                                viewModel.toggleDoubtful(idx)
                            },
                            onNavigateIndex = { idx ->
                                viewModel.navigateQuestion(idx)
                            },
                            onRequestSubmit = {
                                viewModel.showSubmitConfirmation(true)
                            }
                        )

                        current.activeViolation?.let { violation ->
                            ViolationWarningDialog(
                                detected = violation,
                                violationCount = current.violationCount,
                                maxViolations = current.maxViolations,
                                onDismiss = { viewModel.dismissViolation() }
                            )
                        }

                        if (current.showSubmitConfirm) {
                            val answeredCount = current.selectedAnswers.count { it.value.isNotEmpty() }
                            SubmitConfirmationDialog(
                                totalQuestions = current.questions.size,
                                answeredCount = answeredCount,
                                doubtfulCount = current.doubtfulIndices.size,
                                onConfirmSubmit = {
                                    viewModel.showSubmitConfirmation(false)
                                    viewModel.confirmSubmit()
                                },
                                onDismiss = {
                                    viewModel.showSubmitConfirmation(false)
                                }
                            )
                        }
                    }

                    is UiState.Finished -> {
                        ExamFinishedReceiptScreen(
                            studentName = current.studentName,
                            nisn = current.nisn,
                            examTitle = current.examTitle,
                            answeredCount = current.answeredCount,
                            receiptCode = current.receiptCode,
                            timestampWib = current.timestampWib,
                            checksumSha256 = current.checksumSha256,
                            onExitSession = {
                                viewModel.resetToTokenEntry()
                                finish()
                            }
                        )
                    }

                    else -> {}
                }
            }
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        viewModel.onSignal(Signal.UserLeaveHint)
        viewModel.triggerImmediateViolation(ViolationCategory.APP_EXIT)

        // Keep / bring ExamActivity back to front immediately
        try {
            val intent = Intent(this, ExamActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
        } catch (_: Exception) {}
    }

    override fun onStart() {
        super.onStart()
        viewModel.onSignal(Signal.Started)
    }

    override fun onStop() {
        super.onStop()
        viewModel.onSignal(Signal.Stopped)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            viewModel.onSignal(Signal.FocusGained)
        } else {
            viewModel.onSignal(Signal.FocusLost)
        }
    }

    override fun onMultiWindowModeChanged(isInMultiWindowMode: Boolean, newConfig: Configuration) {
        super.onMultiWindowModeChanged(isInMultiWindowMode, newConfig)
        if (isInMultiWindowMode) {
            viewModel.triggerImmediateViolation(ViolationCategory.SPLIT_SCREEN)
        }
        viewModel.onSignal(Signal.MultiWindow(isInMultiWindowMode))
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        if (isInPictureInPictureMode) {
            viewModel.triggerImmediateViolation(ViolationCategory.FLOATING_PIP)
        }
        viewModel.onSignal(Signal.Pip(isInPictureInPictureMode))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        viewModel.onSignal(Signal.ConfigRotation)
    }

    override fun onDestroy() {
        super.onDestroy()
        clipListener?.let { clipboardManager?.removePrimaryClipChangedListener(it) }
    }
}
