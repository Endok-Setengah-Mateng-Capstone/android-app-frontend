package id.sch.man3merangin.ujian.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import id.sch.man3merangin.ujian.data.crypto.Option
import id.sch.man3merangin.ujian.data.crypto.Question
import id.sch.man3merangin.ujian.data.crypto.QuestionType
import id.sch.man3merangin.ujian.ui.theme.MonoStyle
import id.sch.man3merangin.ujian.ui.theme.Space
import id.sch.man3merangin.ujian.ui.theme.TapTarget
import id.sch.man3merangin.ujian.ui.theme.Warning

private val ButtonPadding = PaddingValues(horizontal = Space.m, vertical = Space.s)

@Composable
fun QuestionScreen(
    examTitle: String,
    questions: List<Question>,
    currentIndex: Int,
    selectedAnswers: Map<String, Set<String>>,
    doubtfulIndices: Set<Int>,
    remainingMs: Long,
    onOptionSelected: (questionId: String, optionId: String, isMulti: Boolean) -> Unit,
    onToggleDoubtful: (Int) -> Unit,
    onNavigateIndex: (Int) -> Unit,
    onRequestSubmit: () -> Unit
) {
    var showNavSheet by remember { mutableStateOf(false) }

    val currentQuestion = questions.getOrNull(currentIndex) ?: return
    val currentSelections = selectedAnswers[currentQuestion.id] ?: emptySet()
    val isDoubtful = doubtfulIndices.contains(currentIndex)
    val isMulti = currentQuestion.type == QuestionType.MULTI
    val isLast = currentIndex >= questions.size - 1
    val colors = MaterialTheme.colorScheme

    val answeredIndices = remember(selectedAnswers, questions) {
        questions.mapIndexedNotNull { idx, q ->
            val sel = selectedAnswers[q.id]
            if (!sel.isNullOrEmpty()) idx else null
        }.toSet()
    }

    val totalSeconds = (remainingMs / 1000).coerceAtLeast(0)
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    val timeFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds)
    val isTimeLow = totalSeconds < 300

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = colors.surfaceContainerLowest,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Space.l, vertical = Space.s),
                    horizontalArrangement = Arrangement.spacedBy(Space.s),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = examTitle,
                        style = MaterialTheme.typography.titleSmall,
                        color = colors.onSurface,
                        maxLines = 2,
                        modifier = Modifier.weight(1f)
                    )

                    // Announced once as a whole; the ticking digits stay silent.
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = if (isTimeLow) colors.errorContainer else colors.surfaceContainerHigh,
                        modifier = Modifier.clearAndSetSemantics {
                            contentDescription = "Sisa waktu $hours jam $minutes menit $seconds detik"
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = Space.s, vertical = Space.xs),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Space.xs)
                        ) {
                            val tint = if (isTimeLow) colors.error else colors.onSurface
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
                            Text(
                                text = timeFormatted,
                                style = MonoStyle.merge(MaterialTheme.typography.titleSmall),
                                color = tint
                            )
                        }
                    }

                    Button(
                        onClick = { showNavSheet = true },
                        shape = MaterialTheme.shapes.small,
                        contentPadding = ButtonPadding,
                        modifier = Modifier
                            .heightIn(min = TapTarget)
                            .semantics {
                                contentDescription = "Daftar soal. Sekarang soal ${currentIndex + 1} dari ${questions.size}"
                            }
                    ) {
                        Icon(Icons.Default.GridView, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(Space.xs))
                        Text("${currentIndex + 1}/${questions.size}", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(Space.l),
                verticalArrangement = Arrangement.spacedBy(Space.l)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(Space.xs)) {
                    Text(
                        text = "Soal ${currentIndex + 1} dari ${questions.size} · ${currentQuestion.points} poin",
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.primary
                    )
                    Text(
                        text = currentQuestion.text,
                        style = MaterialTheme.typography.bodyLarge,
                        color = colors.onSurface
                    )
                    Text(
                        text = if (isMulti) "Pilih satu atau lebih jawaban." else "Pilih satu jawaban.",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onSurfaceVariant
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(Space.s)) {
                    val optionLetters = listOf("A", "B", "C", "D", "E")
                    currentQuestion.options.forEachIndexed { optIndex, option ->
                        OptionCard(
                            letter = optionLetters.getOrElse(optIndex) { "${optIndex + 1}" },
                            option = option,
                            isSelected = currentSelections.contains(option.id),
                            isMulti = isMulti,
                            onClick = { onOptionSelected(currentQuestion.id, option.id, isMulti) }
                        )
                    }
                }

                TextButton(
                    onClick = onRequestSubmit,
                    modifier = Modifier
                        .align(Alignment.End)
                        .heightIn(min = TapTarget)
                ) {
                    Text("Kumpulkan jawaban")
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = colors.surfaceContainerLowest,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Space.l, vertical = Space.s),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { onNavigateIndex(currentIndex - 1) },
                        enabled = currentIndex > 0,
                        shape = MaterialTheme.shapes.small,
                        contentPadding = ButtonPadding,
                        modifier = Modifier.heightIn(min = TapTarget)
                    ) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = null)
                        Text("Sebelumnya", style = MaterialTheme.typography.labelLarge)
                    }

                    Button(
                        onClick = { onToggleDoubtful(currentIndex) },
                        shape = MaterialTheme.shapes.small,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDoubtful) Warning else colors.surfaceContainerHigh,
                            contentColor = if (isDoubtful) colors.onPrimary else colors.onSurface
                        ),
                        contentPadding = ButtonPadding,
                        modifier = Modifier
                            .heightIn(min = TapTarget)
                            .semantics { stateDescription = if (isDoubtful) "Ditandai ragu-ragu" else "Belum ditandai" }
                    ) {
                        Icon(
                            imageVector = if (isDoubtful) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(Space.xs))
                        Text("Ragu-ragu", style = MaterialTheme.typography.labelLarge)
                    }

                    Button(
                        onClick = { if (isLast) onRequestSubmit() else onNavigateIndex(currentIndex + 1) },
                        shape = MaterialTheme.shapes.small,
                        contentPadding = ButtonPadding,
                        modifier = Modifier.heightIn(min = TapTarget)
                    ) {
                        Text(if (isLast) "Kumpulkan" else "Berikutnya", style = MaterialTheme.typography.labelLarge)
                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                    }
                }
            }
        }

        if (showNavSheet) {
            QuestionNavSheet(
                totalQuestions = questions.size,
                currentIndex = currentIndex,
                answeredIndices = answeredIndices,
                doubtfulIndices = doubtfulIndices,
                onSelectQuestion = {
                    onNavigateIndex(it)
                    showNavSheet = false
                },
                onDismiss = { showNavSheet = false }
            )
        }
    }
}

@Composable
private fun OptionCard(
    letter: String,
    option: Option,
    isSelected: Boolean,
    isMulti: Boolean,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    // One tap target for the whole row; the radio/checkbox is only a visual cue.
    val select = if (isMulti) {
        Modifier.toggleable(value = isSelected, role = Role.Checkbox, onValueChange = { onClick() })
    } else {
        Modifier.selectable(selected = isSelected, role = Role.RadioButton, onClick = onClick)
    }

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = if (isSelected) colors.surfaceContainerLow else colors.surfaceContainerLowest,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) colors.primary else colors.outlineVariant,
                shape = MaterialTheme.shapes.medium
            )
            .then(select)
    ) {
        Row(
            modifier = Modifier
                .heightIn(min = TapTarget)
                .padding(horizontal = Space.m, vertical = Space.s),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Space.m)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(if (isSelected) colors.primary else colors.surfaceContainerHigh, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = letter,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isSelected) colors.onPrimary else colors.onSurface
                )
            }

            Text(
                text = option.text,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurface,
                modifier = Modifier.weight(1f)
            )

            if (isMulti) {
                Checkbox(checked = isSelected, onCheckedChange = null)
            } else {
                RadioButton(selected = isSelected, onClick = null)
            }
        }
    }
}
