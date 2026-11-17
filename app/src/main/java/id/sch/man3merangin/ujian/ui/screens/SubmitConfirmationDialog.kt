package id.sch.man3merangin.ujian.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import id.sch.man3merangin.ujian.ui.theme.Space
import id.sch.man3merangin.ujian.ui.theme.Success
import id.sch.man3merangin.ujian.ui.theme.SuccessContainer
import id.sch.man3merangin.ujian.ui.theme.TapTarget
import id.sch.man3merangin.ujian.ui.theme.Warning
import id.sch.man3merangin.ujian.ui.theme.WarningContainer

@Composable
fun SubmitConfirmationDialog(
    totalQuestions: Int,
    answeredCount: Int,
    doubtfulCount: Int,
    onConfirmSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    val unansweredCount = (totalQuestions - answeredCount).coerceAtLeast(0)
    var isConfirmed by remember { mutableStateOf(false) }
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.scrim.copy(alpha = 0.7f))
            .clickable { onDismiss() }
            .semantics { paneTitle = "Kumpulkan jawaban" },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clickable(enabled = false) {}
                .padding(Space.l),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = colors.surfaceContainerLowest),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Space.xl),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Space.l)
            ) {
                IconBadge(Icons.Default.CheckCircle, size = 56.dp)

                Text(
                    "Kumpulkan jawaban?",
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.onSurface,
                    textAlign = TextAlign.Center
                )
                Text(
                    if (unansweredCount > 0) {
                        "Masih ada $unansweredCount soal kosong. Jawaban yang sudah dikumpulkan tidak bisa diubah lagi."
                    } else {
                        "Jawaban yang sudah dikumpulkan tidak bisa diubah lagi."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Space.s)
                ) {
                    Metric("Terjawab", answeredCount, Success, SuccessContainer, Modifier.weight(1f))
                    Metric("Ragu-ragu", doubtfulCount, Warning, WarningContainer, Modifier.weight(1f))
                    Metric("Kosong", unansweredCount, colors.onSurfaceVariant, colors.surfaceContainerHigh, Modifier.weight(1f))
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = TapTarget)
                        .background(colors.surfaceContainerLow, MaterialTheme.shapes.small)
                        .toggleable(value = isConfirmed, role = Role.Checkbox, onValueChange = { isConfirmed = it })
                        .padding(horizontal = Space.s, vertical = Space.xs)
                ) {
                    Checkbox(checked = isConfirmed, onCheckedChange = null, modifier = Modifier.padding(Space.s))
                    Text(
                        "Saya sudah memeriksa jawaban dan siap mengumpulkan.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onSurface
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(Space.s)
                ) {
                    Button(
                        onClick = onConfirmSubmit,
                        enabled = isConfirmed,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = TapTarget),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text("Kumpulkan sekarang")
                    }
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = TapTarget),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text("Periksa lagi")
                    }
                }
            }
        }
    }
}

@Composable
private fun Metric(title: String, value: Int, color: Color, bg: Color, modifier: Modifier = Modifier) {
    Surface(shape = MaterialTheme.shapes.small, color = bg, modifier = modifier) {
        Column(
            modifier = Modifier.padding(vertical = Space.s),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("$value", style = MaterialTheme.typography.titleMedium, color = color)
            Text(title, style = MaterialTheme.typography.labelMedium, color = color)
        }
    }
}
