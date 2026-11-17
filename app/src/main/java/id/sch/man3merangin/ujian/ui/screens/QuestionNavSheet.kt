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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import id.sch.man3merangin.ujian.ui.theme.Space
import id.sch.man3merangin.ujian.ui.theme.Success
import id.sch.man3merangin.ujian.ui.theme.SuccessContainer
import id.sch.man3merangin.ujian.ui.theme.TapTarget
import id.sch.man3merangin.ujian.ui.theme.Warning
import id.sch.man3merangin.ujian.ui.theme.WarningContainer

@Composable
fun QuestionNavSheet(
    totalQuestions: Int,
    currentIndex: Int,
    answeredIndices: Set<Int>,
    doubtfulIndices: Set<Int>,
    onSelectQuestion: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.scrim.copy(alpha = 0.55f))
            .clickable { onDismiss() }
            .semantics { paneTitle = "Daftar soal" },
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = false) {}, // swallow taps so they don't reach the scrim
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            color = colors.surfaceContainerLowest,
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Space.l, vertical = Space.m)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Daftar soal",
                            style = MaterialTheme.typography.titleMedium,
                            color = colors.onSurface
                        )
                        Text(
                            "Ketuk nomor untuk lompat ke soal itu.",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(TapTarget)) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup daftar soal")
                    }
                }

                Spacer(Modifier.size(Space.m))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.surfaceContainer, MaterialTheme.shapes.medium)
                        .padding(Space.s),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LegendItem(Success, "${answeredIndices.size} terjawab")
                    LegendItem(Warning, "${doubtfulIndices.size} ragu-ragu")
                    LegendItem(colors.outline, "${totalQuestions - answeredIndices.size} belum")
                }

                Spacer(Modifier.size(Space.l))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    horizontalArrangement = Arrangement.spacedBy(Space.s),
                    verticalArrangement = Arrangement.spacedBy(Space.s),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 340.dp)
                        .padding(bottom = Space.l)
                ) {
                    items(totalQuestions) { index ->
                        val isCurrent = index == currentIndex
                        val isAnswered = answeredIndices.contains(index)
                        val isDoubtful = doubtfulIndices.contains(index)

                        val bg = when {
                            isDoubtful -> WarningContainer
                            isAnswered -> SuccessContainer
                            else -> colors.surfaceContainerHigh
                        }
                        val fg = when {
                            isDoubtful -> Warning
                            isAnswered -> Success
                            else -> colors.onSurface
                        }
                        val status = when {
                            isDoubtful -> "ragu-ragu"
                            isAnswered -> "terjawab"
                            else -> "belum dijawab"
                        }

                        Box(
                            modifier = Modifier
                                .size(TapTarget + 4.dp)
                                .clip(MaterialTheme.shapes.small)
                                .background(bg)
                                .border(
                                    width = 2.dp,
                                    color = if (isCurrent) colors.primary else Color.Transparent,
                                    shape = MaterialTheme.shapes.small
                                )
                                .clickable(role = Role.Button) {
                                    onSelectQuestion(index)
                                    onDismiss()
                                }
                                .semantics {
                                    contentDescription = "Soal ${index + 1}, $status" +
                                        if (isCurrent) ", sedang dibuka" else ""
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isCurrent) colors.primary else fg
                            )
                            // Marker shape (square = doubtful, dot = answered) so status is not color alone.
                            if (isDoubtful || isAnswered) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(5.dp)
                                        .size(6.dp)
                                        .background(fg, if (isDoubtful) RoundedCornerShape(1.dp) else CircleShape)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Space.xs)
    ) {
        Box(Modifier.size(10.dp).background(color, CircleShape))
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
    }
}
