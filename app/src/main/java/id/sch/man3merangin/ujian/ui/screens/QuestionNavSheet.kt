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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.sch.man3merangin.ujian.ui.theme.GreenActive
import id.sch.man3merangin.ujian.ui.theme.GreenActiveContainer
import id.sch.man3merangin.ujian.ui.theme.OnPrimary
import id.sch.man3merangin.ujian.ui.theme.OnSurface
import id.sch.man3merangin.ujian.ui.theme.OnSurfaceVariant
import id.sch.man3merangin.ujian.ui.theme.Outline
import id.sch.man3merangin.ujian.ui.theme.OutlineVariant
import id.sch.man3merangin.ujian.ui.theme.Primary
import id.sch.man3merangin.ujian.ui.theme.Secondary
import id.sch.man3merangin.ujian.ui.theme.SurfaceContainer
import id.sch.man3merangin.ujian.ui.theme.SurfaceContainerHigh
import id.sch.man3merangin.ujian.ui.theme.SurfaceContainerLowest
import id.sch.man3merangin.ujian.ui.theme.WarningAmber
import id.sch.man3merangin.ujian.ui.theme.WarningAmberContainer

@Composable
fun QuestionNavSheet(
    totalQuestions: Int,
    currentIndex: Int,
    answeredIndices: Set<Int>,
    doubtfulIndices: Set<Int>,
    onSelectQuestion: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = false) {}, // Prevent dismiss when tapping inside sheet
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            color = SurfaceContainerLowest,
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Lembar Kisi Navigasi Soal",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                        Text(
                            text = "Pilih nomor soal untuk berpindah navigasi secara cepat",
                            fontSize = 11.sp,
                            color = OnSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

            Spacer(modifier = Modifier.height(10.dp))

            // Legend status
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceContainer, RoundedCornerShape(10.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendIndicator(
                    color = GreenActive,
                    label = "${answeredIndices.size} Terjawab"
                )
                LegendIndicator(
                    color = WarningAmber,
                    label = "${doubtfulIndices.size} Ragu-ragu"
                )
                val unanswered = totalQuestions - answeredIndices.size
                LegendIndicator(
                    color = OutlineVariant,
                    label = "$unanswered Belum"
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 5-column grid for questions 1..total
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
                    .padding(bottom = 16.dp)
            ) {
                items(totalQuestions) { index ->
                    val questionNo = index + 1
                    val isCurrent = index == currentIndex
                    val isAnswered = answeredIndices.contains(index)
                    val isDoubtful = doubtfulIndices.contains(index)

                    val bgColor = when {
                        isDoubtful -> WarningAmberContainer
                        isAnswered -> GreenActiveContainer
                        else -> SurfaceContainerHigh
                    }
                    val textColor = when {
                        isDoubtful -> WarningAmber
                        isAnswered -> GreenActive
                        else -> OnSurface
                    }

                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(bgColor)
                            .border(
                                width = if (isCurrent) 2.dp else 1.dp,
                                color = if (isCurrent) Secondary else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                onSelectQuestion(index)
                                onDismiss()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "$questionNo",
                                fontSize = 14.sp,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.SemiBold,
                                color = if (isCurrent) Secondary else textColor,
                                textAlign = TextAlign.Center
                            )
                            if (isDoubtful) {
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .background(WarningAmber, CircleShape)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
}

@Composable
private fun LegendIndicator(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, CircleShape)
        )
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = OnSurface
        )
    }
}
