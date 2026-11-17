package id.sch.man3merangin.ujian.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import id.sch.man3merangin.ujian.ui.theme.Space
import id.sch.man3merangin.ujian.ui.theme.TapTarget
import id.sch.man3merangin.ujian.ui.theme.Warning
import id.sch.man3merangin.ujian.ui.theme.WarningContainer
import id.sch.man3merangin.ujian.violation.Detected
import id.sch.man3merangin.ujian.violation.ViolationCategory

/**
 * Shown when a violation is recorded. The system never locks or submits on its own,
 * so the text only tells the student what was logged. [maxViolations] only decides
 * when the dialog switches to the stronger error color.
 */
@Composable
fun ViolationWarningDialog(
    detected: Detected,
    violationCount: Int = 1,
    maxViolations: Int = 3,
    onDismiss: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val isCritical = violationCount >= maxViolations
    val accent = if (isCritical) colors.error else Warning
    val accentContainer = if (isCritical) colors.errorContainer else WarningContainer

    val categoryLabel = when (detected.category) {
        ViolationCategory.APP_EXIT -> "Keluar dari aplikasi"
        ViolationCategory.SPLIT_SCREEN -> "Layar terbagi (split screen)"
        ViolationCategory.NOTIFICATION_PANEL -> "Panel notifikasi atau layar kehilangan fokus"
        ViolationCategory.FLOATING_PIP -> "Jendela melayang (PiP)"
        ViolationCategory.COPY_PASTE -> "Salin atau tempel"
        else -> detected.category.name
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.scrim.copy(alpha = 0.7f))
            .clickable(enabled = false) {}
            .semantics {
                paneTitle = "Aktivitas tercatat"
                liveRegion = LiveRegionMode.Assertive
            },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
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
                verticalArrangement = Arrangement.spacedBy(Space.m)
            ) {
                IconBadge(Icons.Default.Warning, size = 56.dp, tint = accent, container = accentContainer)

                Text(
                    "Peringatan ke-$violationCount",
                    style = MaterialTheme.typography.labelLarge,
                    color = accent,
                    modifier = Modifier
                        .background(accentContainer, MaterialTheme.shapes.large)
                        .padding(horizontal = Space.m, vertical = Space.xs)
                )

                Text(
                    "Aktivitas tercatat",
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.onSurface,
                    textAlign = TextAlign.Center
                )
                Text(
                    "Kejadian ini dicatat dan akan dilihat guru. Ujian tidak dikunci; guru yang memutuskan tindak lanjutnya.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurface,
                    textAlign = TextAlign.Center
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = colors.surfaceContainerLow)
                ) {
                    Column(
                        modifier = Modifier.padding(Space.m),
                        verticalArrangement = Arrangement.spacedBy(Space.s)
                    ) {
                        DetailRow("Kejadian") {
                            Text(
                                categoryLabel,
                                style = MaterialTheme.typography.labelMedium,
                                color = accent,
                                textAlign = TextAlign.End
                            )
                        }
                        if (detected.durationMs > 0) {
                            DetailRow("Lama di luar aplikasi") {
                                Text(
                                    "${detected.durationMs / 1000} detik",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = colors.onSurface
                                )
                            }
                        }
                        DetailRow("Jawaban") {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = colors.primary, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(Space.xs))
                            Text(
                                "Aman, tersimpan di HP",
                                style = MaterialTheme.typography.labelMedium,
                                color = colors.primary
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(Space.s)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = TapTarget),
                        shape = MaterialTheme.shapes.small,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCritical) colors.error else colors.primary,
                            contentColor = colors.onPrimary
                        )
                    ) {
                        Icon(Icons.Default.PlayCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(Space.s))
                        Text("Lanjut mengerjakan")
                    }
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = TapTarget),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Icon(Icons.Default.SupportAgent, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(Space.s))
                        Text("Lapor ke pengawas")
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: @Composable () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(end = Space.m)
        )
        value()
    }
}
