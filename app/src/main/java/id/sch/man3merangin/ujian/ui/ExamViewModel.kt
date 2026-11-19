package id.sch.man3merangin.ujian.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.sch.man3merangin.ujian.UjianApplication
import id.sch.man3merangin.ujian.data.crypto.ExamContent
import id.sch.man3merangin.ujian.data.crypto.PackageCrypto
import id.sch.man3merangin.ujian.data.crypto.Question
import id.sch.man3merangin.ujian.data.crypto.WrongTokenException
import id.sch.man3merangin.ujian.data.db.ExamSessionEntity
import id.sch.man3merangin.ujian.exam.AutoSubmit
import id.sch.man3merangin.ujian.exam.ExamOrder
import id.sch.man3merangin.ujian.exam.ExamTimer
import id.sch.man3merangin.ujian.exam.SessionStore
import id.sch.man3merangin.ujian.time.WindowState
import id.sch.man3merangin.ujian.violation.Detected
import id.sch.man3merangin.ujian.violation.Signal
import id.sch.man3merangin.ujian.violation.ViolationCategory
import id.sch.man3merangin.ujian.violation.ViolationTracker
import id.sch.man3merangin.ujian.work.Heartbeat
import id.sch.man3merangin.ujian.work.Scheduler
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

sealed interface UiState {
    data class TokenEntry(val isLoading: Boolean = false, val error: String? = null) : UiState
    data class Instruction(val examId: String, val title: String, val studentName: String, val nisn: String) : UiState
    data class InExam(
        val examId: String,
        val examTitle: String,
        val questions: List<Question>,
        val currentIndex: Int = 0,
        val selectedAnswers: Map<String, Set<String>> = emptyMap(),
        val doubtfulIndices: Set<Int> = emptySet(),
        val remainingMs: Long = 90 * 60 * 1000L,
        val activeViolation: Detected? = null,
        val violationCount: Int = 0,
        val maxViolations: Int = 3,
        val showSubmitConfirm: Boolean = false
    ) : UiState
    data class Finished(
        val studentName: String,
        val nisn: String,
        val examTitle: String,
        val answeredCount: Int,
        val receiptCode: String,
        val timestampWib: String,
        val checksumSha256: String
    ) : UiState
}

class ExamViewModel : ViewModel() {
    private val app = UjianApplication.instance
    private val session = app.session
    private val db = app.database
    private val answerRepo = app.answerRepository
    private val violationRepo = app.violationRepository
    private val examRepo = app.examRepository
    private val clock = app.clock
    private val alarm = app.alarmPlayer

    private val violationTracker = ViolationTracker()
    private val heartbeat = Heartbeat(app.api, answerRepo, clock, viewModelScope)

    private val _uiState = MutableStateFlow<UiState>(UiState.TokenEntry())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var activeExamId: String = "exam-bio-fikih-01"
    private var examEndAtMs: Long = System.currentTimeMillis() + 90 * 60 * 1000L
    private var timerJob: Job? = null

    private val sessionStore = object : SessionStore {
        override fun get(examId: String, nisn: String): ExamSessionEntity? {
            return kotlinx.coroutines.runBlocking(kotlinx.coroutines.Dispatchers.IO) {
                db.examSessionDao().get(examId, nisn)
            }
        }
        override fun put(session: ExamSessionEntity) {
            viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                db.examSessionDao().upsert(session)
            }
        }
    }
    private val examTimer = ExamTimer(clock = clock, store = sessionStore)

    fun validateToken(nisn: String, roomSession: String, token: String) {
        viewModelScope.launch {
            _uiState.value = UiState.TokenEntry(isLoading = true)
            try {
                // Ensure packages are downloaded or use cached
                examRepo.refresh()
                val pkg = db.examPackageDao().getById(activeExamId)
                    ?: db.examPackageDao().getAll().firstOrNull()

                val envelope = if (pkg != null) {
                    kotlinx.serialization.json.Json.decodeFromString(
                        id.sch.man3merangin.ujian.data.crypto.Envelope.serializer(),
                        pkg.envelopeJson
                    )
                } else {
                    app.api.downloadPackage(activeExamId)
                }

                // Verify token locally via PBKDF2/AES-GCM (Token is NEVER sent to server)
                val content = PackageCrypto.open(envelope, token.trim())

                session.nisn = nisn
                session.token = "session-valid-$nisn"
                session.rememberToken(nisn, session.token!!)
                session.studentName = "Ahmad Fauzan Al-Ghifari"

                activeExamId = envelope.examId
                examEndAtMs = envelope.endAt

                _uiState.value = UiState.Instruction(
                    examId = envelope.examId,
                    title = envelope.title,
                    studentName = session.studentName ?: "Ahmad Fauzan Al-Ghifari",
                    nisn = nisn
                )
            } catch (_: WrongTokenException) {
                _uiState.value = UiState.TokenEntry(error = "Token ujian salah atau tidak valid.")
            } catch (e: Exception) {
                _uiState.value = UiState.TokenEntry(error = e.message ?: "Terjadi kesalahan koneksi.")
            }
        }
    }

    fun startExam(examId: String, examTitle: String) {
        viewModelScope.launch {
            val pkg = db.examPackageDao().getById(examId)
            val envelope = if (pkg != null) {
                kotlinx.serialization.json.Json.decodeFromString(
                    id.sch.man3merangin.ujian.data.crypto.Envelope.serializer(),
                    pkg.envelopeJson
                )
            } else {
                try {
                    app.api.downloadPackage(examId)
                } catch (e: java.io.IOException) {
                    _uiState.value = UiState.TokenEntry(error = "Gagal mengunduh soal. Periksa koneksi.")
                    return@launch
                } catch (e: id.sch.man3merangin.ujian.data.net.ApiException) {
                    _uiState.value = UiState.TokenEntry(error = "Gagal mengunduh soal (${e.code}).")
                    return@launch
                } catch (e: id.sch.man3merangin.ujian.data.net.DeviceMismatchException) {
                    _uiState.value = UiState.TokenEntry(error = e.message)
                    return@launch
                }
            }

            // Fallback content or opened from envelope
            // TODO(Task 9): token must come from TokenScreen; hard-coded token and empty fallback are interim
            val content = try {
                PackageCrypto.open(envelope, "MN3-9X4")
            } catch (_: Exception) {
                ExamContent(questions = emptyList())
            }

            // Deterministic student order
            val arranged = ExamOrder.arrange(content, examId, session.nisn ?: "212210045")
            val loadedAnswers = answerRepo.load(examId, session.nisn ?: "")

            val currentNisn = session.nisn ?: "212210045"
            val initialRemaining = examTimer.remainingMs(examId, currentNisn, examEndAtMs)

            _uiState.value = UiState.InExam(
                examId = examId,
                examTitle = examTitle,
                questions = arranged.questions,
                currentIndex = 0,
                selectedAnswers = loadedAnswers,
                remainingMs = initialRemaining
            )

            // Start background sync & heartbeat
            heartbeat.start(examId, currentNisn)
            Scheduler.kickSyncNow(app)

            startTimerCountdown(examId, currentNisn)
        }
    }

    private fun startTimerCountdown(examId: String, nisn: String) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000L)
                val remaining = examTimer.remainingMs(examId, nisn, examEndAtMs)
                val state = _uiState.value
                if (state is UiState.InExam) {
                    if (remaining <= 0) {
                        AutoSubmit.runIfDue(examId, nisn, examTimer, examEndAtMs, answerRepo)
                        finishExam()
                        break
                    } else {
                        _uiState.value = state.copy(remainingMs = remaining)
                    }
                }
            }
        }
    }

    fun selectOption(questionId: String, optionId: String, isMulti: Boolean) {
        val state = _uiState.value as? UiState.InExam ?: return
        val currentSelections = state.selectedAnswers[questionId] ?: emptySet()

        val newSelections = if (isMulti) {
            if (currentSelections.contains(optionId)) {
                currentSelections - optionId
            } else {
                currentSelections + optionId
            }
        } else {
            setOf(optionId)
        }

        val updatedMap = state.selectedAnswers + (questionId to newSelections)
        _uiState.value = state.copy(selectedAnswers = updatedMap)

        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val nisn = session.nisn ?: "212210045"
            try {
                answerRepo.save(state.examId, nisn, questionId, newSelections)
                Scheduler.enqueueSync(app)
            } catch (e: Exception) {
                android.util.Log.e("ExamViewModel", "Failed to save answer", e)
            }
        }
    }

    fun toggleDoubtful(index: Int) {
        val state = _uiState.value as? UiState.InExam ?: return
        val newSet = if (state.doubtfulIndices.contains(index)) {
            state.doubtfulIndices - index
        } else {
            state.doubtfulIndices + index
        }
        _uiState.value = state.copy(doubtfulIndices = newSet)
    }

    fun navigateQuestion(index: Int) {
        val state = _uiState.value as? UiState.InExam ?: return
        if (index in state.questions.indices) {
            _uiState.value = state.copy(currentIndex = index)
        }
    }

    fun showSubmitConfirmation(show: Boolean) {
        val state = _uiState.value as? UiState.InExam ?: return
        _uiState.value = state.copy(showSubmitConfirm = show)
    }

    fun confirmSubmit() {
        val state = _uiState.value as? UiState.InExam ?: return
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val nisn = session.nisn ?: "212210045"
            try {
                answerRepo.submit(state.examId, nisn)
                Scheduler.kickSyncNow(app)
            } catch (e: Exception) {
                android.util.Log.e("ExamViewModel", "Failed to submit exam", e)
            }
            finishExam()
        }
    }

    private fun finishExam() {
        timerJob?.cancel()
        heartbeat.stop()

        val state = _uiState.value as? UiState.InExam
        val answeredCount = state?.selectedAnswers?.count { it.value.isNotEmpty() } ?: 0
        val nisn = session.nisn ?: "212210045"
        val studentName = session.studentName ?: "Ahmad Fauzan Al-Ghifari"
        val title = state?.examTitle ?: "Biologi & Fikih Lingkungan"

        _uiState.value = UiState.Finished(
            studentName = studentName,
            nisn = nisn,
            examTitle = title,
            answeredCount = answeredCount,
            receiptCode = "TRX-MN3-2024-${(1000000..9999999).random()}",
            timestampWib = "16 Nov 2026, 09:15:22 WIB",
            checksumSha256 = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
        )
    }

    fun onSignal(signal: Signal) {
        val now = System.currentTimeMillis()
        val state = _uiState.value
        // If violation dialog is already visible, do not stack or loop violations
        if (state is UiState.InExam && state.activeViolation != null) {
            return
        }

        val detectedList = violationTracker.onSignal(signal, now)

        for (d in detectedList) {
            if (d.category.isViolation) {
                alarm.play()
                val currentState = _uiState.value
                if (currentState is UiState.InExam) {
                    val qNo = currentState.questions.getOrNull(currentState.currentIndex)?.no ?: 1
                    val newCount = currentState.violationCount + 1
                    _uiState.value = currentState.copy(
                        activeViolation = d,
                        violationCount = newCount
                    )
                    viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                        val nisn = session.nisn ?: "212210045"
                        try {
                            violationRepo.record(currentState.examId, nisn, d, qNo)
                            Scheduler.enqueueSync(app)
                        } catch (e: Exception) {
                            android.util.Log.e("ExamViewModel", "Failed to record violation", e)
                        }
                    }
                }
            } else {
                // Info category, record without alarm
                val currentState = _uiState.value
                if (currentState is UiState.InExam) {
                    val qNo = currentState.questions.getOrNull(currentState.currentIndex)?.no ?: 1
                    viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                        val nisn = session.nisn ?: "212210045"
                        try {
                            violationRepo.record(currentState.examId, nisn, d, qNo)
                        } catch (e: Exception) {
                            android.util.Log.e("ExamViewModel", "Failed to record violation", e)
                        }
                    }
                }
            }
        }
    }

    fun triggerImmediateViolation(category: ViolationCategory) {
        val state = _uiState.value
        // Do not trigger if already showing violation dialog
        if (state is UiState.InExam && state.activeViolation != null) {
            return
        }

        val now = System.currentTimeMillis()
        val d = Detected(category, now, 0L)
        alarm.play()
        if (state is UiState.InExam) {
            val qNo = state.questions.getOrNull(state.currentIndex)?.no ?: 1
            val newCount = state.violationCount + 1
            _uiState.value = state.copy(
                activeViolation = d,
                violationCount = newCount
            )
            viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                val nisn = session.nisn ?: "212210045"
                try {
                    violationRepo.record(state.examId, nisn, d, qNo)
                    Scheduler.enqueueSync(app)
                } catch (e: Exception) {
                    android.util.Log.e("ExamViewModel", "Failed to record violation", e)
                }
            }
        }
    }

    fun dismissViolation() {
        alarm.stop()
        violationTracker.resetFocusLost()
        val state = _uiState.value as? UiState.InExam ?: return
        _uiState.value = state.copy(activeViolation = null)
    }

    fun resetToTokenEntry() {
        session.clear()
        _uiState.value = UiState.TokenEntry()
    }
}
