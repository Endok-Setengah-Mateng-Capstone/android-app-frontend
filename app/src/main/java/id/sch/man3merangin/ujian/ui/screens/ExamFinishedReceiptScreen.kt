package id.sch.man3merangin.ujian.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DownloadForOffline
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import id.sch.man3merangin.ujian.ui.theme.MonoStyle
import id.sch.man3merangin.ujian.ui.theme.Space
import id.sch.man3merangin.ujian.ui.theme.Success
import id.sch.man3merangin.ujian.ui.theme.SuccessContainer
import id.sch.man3merangin.ujian.ui.theme.TapTarget

@Composable
fun ExamFinishedReceiptScreen(
    studentName: String = "Ahmad Fauzan Al-Ghifari",
    nisn: String = "212210045",
    examTitle: String = "Biologi & Fikih Lingkungan",
    answeredCount: Int = 40,
    receiptCode: String = "TRX-MN3-2024-8849201",
    timestampWib: String = "16 Nov 2026, 09:15:22 WIB",
    checksumSha256: String = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
    onExitSession: () -> Unit
) {
    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
            .padding(Space.l),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Space.l)
    ) {
        IconBadge(Icons.Default.CheckCircle, size = 68.dp, tint = Success, container = SuccessContainer)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Space.xs)
        ) {
            Text(
                "Jawaban sudah dikumpulkan",
                style = MaterialTheme.typography.titleLarge,
                color = colors.onBackground,
                textAlign = TextAlign.Center
            )
            Text(
                "Jawabanmu tersimpan di HP dan dikirim ke server begitu ada jaringan.",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = colors.surfaceContainerLowest),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(Space.l),
                verticalArrangement = Arrangement.spacedBy(Space.m)
            ) {
                Text(
                    "Tanda terima",
                    style = MaterialTheme.typography.titleSmall,
                    color = colors.onSurface
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.surfaceContainerLow, MaterialTheme.shapes.small)
                        .padding(start = Space.m),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Kode", style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
                        Text(
                            receiptCode,
                            style = MonoStyle.merge(MaterialTheme.typography.titleSmall),
                            color = colors.onSurface
                        )
                    }
                    TextButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Receipt", receiptCode))
                            Toast.makeText(context, "Kode disalin", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.heightIn(min = TapTarget)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(Space.xs))
                        Text("Salin")
                    }
                }

                HorizontalDivider(color = colors.outlineVariant)

                ReceiptRow("Nama", studentName)
                ReceiptRow("NISN", nisn)
                ReceiptRow("Mata pelajaran", examTitle)
                ReceiptRow("Selesai", timestampWib)
                ReceiptRow("Soal terjawab", "$answeredCount")

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.surfaceContainerHigh, MaterialTheme.shapes.small)
                        .padding(Space.s)
                ) {
                    Text("Checksum SHA-256", style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
                    Text(
                        checksumSha256,
                        style = MonoStyle.merge(MaterialTheme.typography.bodySmall),
                        color = colors.onSurfaceVariant
                    )
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = colors.surfaceContainerLow)
        ) {
            Column(
                modifier = Modifier.padding(Space.m),
                verticalArrangement = Arrangement.spacedBy(Space.xs)
            ) {
                Text("Setelah ini", style = MaterialTheme.typography.titleSmall, color = colors.onSurface)
                Text(
                    "1. Tunjukkan kode di atas ke pengawas sebelum keluar ruangan.\n2. Jangan buka aplikasi lain sampai pengawas selesai mencatat.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(Space.s)) {
            Button(
                onClick = {
                    Toast.makeText(context, "Tanda terima disimpan ke perangkat", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = TapTarget),
                shape = MaterialTheme.shapes.small
            ) {
                Icon(Icons.Default.DownloadForOffline, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(Space.s))
                Text("Simpan tanda terima")
            }
            OutlinedButton(
                onClick = onExitSession,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = TapTarget),
                shape = MaterialTheme.shapes.small
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(Space.s))
                Text("Keluar")
            }
        }
    }
}

@Composable
private fun ReceiptRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Space.m)
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}
