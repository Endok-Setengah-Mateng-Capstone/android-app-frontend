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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.sch.man3merangin.ujian.ui.theme.ErrorRed
import id.sch.man3merangin.ujian.ui.theme.ErrorRedContainer
import id.sch.man3merangin.ujian.ui.theme.OnPrimary
import id.sch.man3merangin.ujian.ui.theme.OnSurface
import id.sch.man3merangin.ujian.ui.theme.OnSurfaceVariant
import id.sch.man3merangin.ujian.ui.theme.Primary
import id.sch.man3merangin.ujian.ui.theme.Secondary
import id.sch.man3merangin.ujian.ui.theme.SurfaceContainerHigh
import id.sch.man3merangin.ujian.ui.theme.SurfaceContainerLow
import id.sch.man3merangin.ujian.ui.theme.SurfaceContainerLowest
import id.sch.man3merangin.ujian.ui.theme.WarningAmber
import id.sch.man3merangin.ujian.ui.theme.WarningAmberContainer
import id.sch.man3merangin.ujian.violation.Detected
import id.sch.man3merangin.ujian.violation.ViolationCategory

@Composable
fun ViolationWarningDialog(
    detected: Detected,
    violationCount: Int = 1,
    maxViolations: Int = 3,
    onDismiss: () -> Unit
) {
    val isCritical = violationCount >= maxViolations
    val remainingChances = (maxViolations - violationCount).coerceAtLeast(0)

    val categoryLabel = when (detected.category) {
        ViolationCategory.APP_EXIT -> "Tombol Home / Keluar Aplikasi"
        ViolationCategory.SPLIT_SCREEN -> "Layar Terbagi (Split Screen)"
        ViolationCategory.NOTIFICATION_PANEL -> "Panel Notifikasi / Layar Tidak Fokus"
        ViolationCategory.FLOATING_PIP -> "Jendela Melayang (PIP)"
        ViolationCategory.COPY_PASTE -> "Aktivitas Salin / Tempel"
        else -> detected.category.name
    }

    val warningMessage = if (isCritical) {
        "PERHATIAN: Anda telah mencapai batas maksimal peringatan ($maxViolations kali). Pelanggaran berikutnya akan menyebabkan ujian otomatis dikumpulkan ke server pengawas."
    } else {
        "Perangkat mendeteksi anomali pada layar ujian. Sisa toleransi Anda: $remainingChances kali sebelum tindakan otomatis."
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable(enabled = false) {},
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(16.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Top Icon Indicator
                Surface(
                    shape = CircleShape,
                    color = if (isCritical) ErrorRedContainer else WarningAmberContainer,
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (isCritical) ErrorRed else WarningAmber,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // Prominent Warning Counter Badge
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isCritical) ErrorRedContainer else WarningAmberContainer
                ) {
                    Text(
                        text = "PERINGATAN KE-$violationCount DARI $maxViolations",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCritical) ErrorRed else WarningAmber,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        letterSpacing = 0.5.sp
                    )
                }

                Text(
                    text = "Aktivitas Terdeteksi",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "MAN 3 Merangin • Sistem Integritas CBT",
                    fontSize = 11.sp,
                    color = OnSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = warningMessage,
                    fontSize = 12.sp,
                    color = if (isCritical) ErrorRed else OnSurface,
                    fontWeight = if (isCritical) FontWeight.SemiBold else FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    lineHeight = 17.sp
                )

                // Detailed Info Card (Matches Stitch S-06)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Jenis Kejadian", fontSize = 11.sp, color = OnSurfaceVariant)
                            Text(
                                text = categoryLabel,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCritical) ErrorRed else WarningAmber
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Toleransi Tersisa", fontSize = 11.sp, color = OnSurfaceVariant)
                            Text(
                                text = if (remainingChances > 0) "$remainingChances kali kesempatan" else "Toleransi Habis",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (remainingChances > 0) Primary else ErrorRed
                            )
                        }

                        if (detected.durationMs > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Durasi Di Luar App", fontSize = 11.sp, color = OnSurfaceVariant)
                                Text("${detected.durationMs / 1000} Detik", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Primary)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Status Jawaban", fontSize = 11.sp, color = OnSurfaceVariant)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Secondary, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("Aman di memori lokal", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Secondary)
                            }
                        }

                        Text(
                            text = "Catatan: Seluruh aktivitas dicatat objektif pada server pengawas.",
                            fontSize = 10.sp,
                            color = OnSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                // Action Buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCritical) ErrorRed else Secondary,
                            contentColor = OnPrimary
                        )
                    ) {
                        Icon(Icons.Default.PlayCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Kembali Mengerjakan Ujian", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.SupportAgent, contentDescription = null, modifier = Modifier.size(18.dp), tint = OnSurfaceVariant)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Lapor Gangguan ke Pengawas", fontSize = 12.sp, color = OnSurfaceVariant)
                    }
                }
            }
        }
    }
}
