package id.sch.man3merangin.ujian.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import id.sch.man3merangin.ujian.ui.screens.ExamInstructionScreen
import id.sch.man3merangin.ujian.ui.screens.TokenEntryScreen
import id.sch.man3merangin.ujian.ui.theme.UjianAppTheme

class MainActivity : ComponentActivity() {
    private val viewModel: ExamViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            UjianAppTheme {
                val state by viewModel.uiState.collectAsState()

                when (val current = state) {
                    is UiState.TokenEntry -> {
                        TokenEntryScreen(
                            isLoading = current.isLoading,
                            errorMessage = current.error,
                            onValidate = { nisn, session, token ->
                                viewModel.validateToken(nisn, session, token)
                            }
                        )
                    }

                    is UiState.Instruction -> {
                        ExamInstructionScreen(
                            studentName = current.studentName,
                            nisn = current.nisn,
                            examTitle = current.title,
                            onStartExam = {
                                val intent = Intent(this, ExamActivity::class.java).apply {
                                    putExtra("exam_id", current.examId)
                                    putExtra("exam_title", current.title)
                                }
                                startActivity(intent)
                                finish()
                            }
                        )
                    }

                    else -> {
                        TokenEntryScreen(
                            onValidate = { nisn, session, token ->
                                viewModel.validateToken(nisn, session, token)
                            }
                        )
                    }
                }
            }
        }
    }
}
