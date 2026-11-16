package id.sch.man3merangin.ujian.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.sch.man3merangin.ujian.data.crypto.Option
import id.sch.man3merangin.ujian.data.crypto.Question
import id.sch.man3merangin.ujian.data.crypto.QuestionType
import id.sch.man3merangin.ujian.ui.theme.ErrorRed
import id.sch.man3merangin.ujian.ui.theme.ErrorRedContainer
import id.sch.man3merangin.ujian.ui.theme.OnPrimary
import id.sch.man3merangin.ujian.ui.theme.OnSecondaryContainer
import id.sch.man3merangin.ujian.ui.theme.OnSurface
import id.sch.man3merangin.ujian.ui.theme.OnSurfaceVariant
import id.sch.man3merangin.ujian.ui.theme.Outline
import id.sch.man3merangin.ujian.ui.theme.OutlineVariant
import id.sch.man3merangin.ujian.ui.theme.Primary
import id.sch.man3merangin.ujian.ui.theme.Secondary
import id.sch.man3merangin.ujian.ui.theme.SecondaryContainer
import id.sch.man3merangin.ujian.ui.theme.Surface
import id.sch.man3merangin.ujian.ui.theme.SurfaceContainer
import id.sch.man3merangin.ujian.ui.theme.SurfaceContainerHigh
import id.sch.man3merangin.ujian.ui.theme.SurfaceContainerLow
import id.sch.man3merangin.ujian.ui.theme.SurfaceContainerLowest
import id.sch.man3merangin.ujian.ui.theme.WarningAmber
import id.sch.man3merangin.ujian.ui.theme.WarningAmberContainer

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

    val answeredIndices = remember(selectedAnswers, questions) {
        questions.mapIndexedNotNull { idx, q ->
            val sel = selectedAnswers[q.id]
            if (!sel.isNullOrEmpty()) idx else null
        }.toSet()
    }

    // Format remaining time (HH:mm:ss)
    val totalSeconds = (remainingMs / 1000).coerceAtLeast(0)
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    val timeFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds)
    val isTimeLow = totalSeconds < 300 // under 5 minutes

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
        // Top App Bar: Subject, Countdown Timer, Nav Grid Button
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = SurfaceContainerLowest,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Exam title & status
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = examTitle,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary,
                        maxLines = 1
                    )
                    Text(
                        text = "Lembar Soal Ujian",
                        fontSize = 11.sp,
                        color = OnSurfaceVariant
                    )
                }

                // Countdown Timer Box
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isTimeLow) ErrorRedContainer else SurfaceContainerHigh
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            tint = if (isTimeLow) ErrorRed else Primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = timeFormatted,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = if (isTimeLow) ErrorRed else Primary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Navigation button: Daftar Soal [14/40]
                Button(
                    onClick = { showNavSheet = true },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Secondary,
                        contentColor = OnPrimary
                    ),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.GridView, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${currentIndex + 1}/${questions.size}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Question stimulus & options
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Question Header Card: Question No & Points
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = SurfaceContainerHigh
                ) {
                    Text(
                        text = "Soal No. ${currentIndex + 1} dari ${questions.size}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Secondary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = SurfaceContainer
                ) {
                    Text(
                        text = "Bobot: ${currentQuestion.points} Poin",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Question text
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = currentQuestion.text,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        color = Primary,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (currentQuestion.type == QuestionType.MULTI) {
                            "Pilihan Ganda Kompleks (Pilih satu atau lebih jawaban yang benar)"
                        } else {
                            "Pilihan Ganda (Pilih satu jawaban yang paling tepat)"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Secondary
                    )
                }
            }

            // Options List
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                val optionLetters = listOf("A", "B", "C", "D", "E")
                currentQuestion.options.forEachIndexed { optIndex, option ->
                    val letter = optionLetters.getOrElse(optIndex) { "${optIndex + 1}" }
                    val isSelected = currentSelections.contains(option.id)

                    OptionCard(
                        letter = letter,
                        option = option,
                        isSelected = isSelected,
                        isMulti = currentQuestion.type == QuestionType.MULTI,
                        onClick = {
                            onOptionSelected(
                                currentQuestion.id,
                                option.id,
                                currentQuestion.type == QuestionType.MULTI
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Banner to submit early
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = SurfaceContainerLow,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onRequestSubmit() }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.TaskAlt, contentDescription = null, tint = Secondary, modifier = Modifier.size(20.dp))
                    Text(
                        text = "Ingin menyelesaikan ujian sekarang? Kumpulkan Jawaban",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Secondary
                    )
                }
            }
        }

        // Bottom Navigation Bar: Sebelumnya, Ragu-ragu, Berikutnya
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = SurfaceContainerLowest,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Button Sebelumnya
                OutlinedButton(
                    onClick = { onNavigateIndex(currentIndex - 1) },
                    enabled = currentIndex > 0,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Sebelumnya", fontSize = 12.sp)
                }

                // Button Ragu-ragu
                Button(
                    onClick = { onToggleDoubtful(currentIndex) },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDoubtful) WarningAmber else SurfaceContainerHigh,
                        contentColor = if (isDoubtful) Color.White else OnSurface
                    ),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = if (isDoubtful) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ragu-ragu", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                // Button Berikutnya / Selesai
                Button(
                    onClick = {
                        if (currentIndex < questions.size - 1) {
                            onNavigateIndex(currentIndex + 1)
                        } else {
                            onRequestSubmit()
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SecondaryContainer,
                        contentColor = OnSecondaryContainer
                    ),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = if (currentIndex < questions.size - 1) "Berikutnya" else "Kumpulkan",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) Secondary else SurfaceContainerHigh,
                shape = RoundedCornerShape(10.dp)
            ),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) SurfaceContainerLow else SurfaceContainerLowest
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Option indicator badge (A, B, C, D)
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Secondary else SurfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = letter,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) OnPrimary else Primary
                )
            }

            // Option text
            Text(
                text = option.text,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = OnSurface,
                modifier = Modifier.weight(1f),
                lineHeight = 18.sp
            )

            // Selection icon: RadioButton or Checkbox
            if (isMulti) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = null,
                    colors = CheckboxDefaults.colors(checkedColor = Secondary)
                )
            } else {
                RadioButton(
                    selected = isSelected,
                    onClick = null,
                    colors = RadioButtonDefaults.colors(selectedColor = Secondary)
                )
            }
        }
    }
}
