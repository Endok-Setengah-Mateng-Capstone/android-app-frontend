package id.sch.man3merangin.ujian.ui.screens

import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DownloadForOffline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.sch.man3merangin.ujian.ui.theme.GreenActive
import id.sch.man3merangin.ujian.ui.theme.GreenActiveContainer
import id.sch.man3merangin.ujian.ui.theme.OnPrimary
import id.sch.man3merangin.ujian.ui.theme.OnSecondaryContainer
import id.sch.man3merangin.ujian.ui.theme.OnSurface
import id.sch.man3merangin.ujian.ui.theme.OnSurfaceVariant
import id.sch.man3merangin.ujian.ui.theme.Primary
import id.sch.man3merangin.ujian.ui.theme.Secondary
import id.sch.man3merangin.ujian.ui.theme.SecondaryContainer
import id.sch.man3merangin.ujian.ui.theme.Surface
import id.sch.man3merangin.ujian.ui.theme.SurfaceContainer
import id.sch.man3merangin.ujian.ui.theme.SurfaceContainerHigh
import id.sch.man3merangin.ujian.ui.theme.SurfaceContainerLow
import id.sch.man3merangin.ujian.ui.theme.SurfaceContainerLowest

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Success Header
        Surface(
            shape = CircleShape,
            color = GreenActiveContainer,
            modifier = Modifier.size(68.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = GreenActive,
                    modifier = Modifier.size(42.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Ujian Berhasil Diserahkan",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Primary,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Seluruh lembar jawaban Anda telah tersimpan dan tervalidasi di server madrasah.",
            fontSize = 12.sp,
            color = OnSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Digital Receipt Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Receipt header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = Secondary, modifier = Modifier.size(20.dp))
                        Text(
                            text = "TANDA TERIMA DIGITAL",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Primary,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Surface(
                        shape = CircleShape,
                        color = GreenActiveContainer
                    ) {
                        Text(
                            text = "Tervalidasi",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenActive,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                // Transaction code box with copy button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceContainerLow, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Kode Tanda Terima", fontSize = 10.sp, color = OnSurfaceVariant)
                        Text(
                            text = receiptCode,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = Primary
                        )
                    }

                    TextButton(
                        onClick = {
                            val clip = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clip.setPrimaryClip(android.content.ClipData.newPlainText("Receipt", receiptCode))
                            Toast.makeText(context, "Kode disalin", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp), tint = Secondary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Salin", fontSize = 12.sp, color = Secondary)
                    }
                }

                Divider(color = SurfaceContainerHigh)

                // Details
                ReceiptRow(label = "Nama Peserta", value = studentName)
                ReceiptRow(label = "NISN", value = nisn)
                ReceiptRow(label = "Mata Pelajaran", value = examTitle)
                ReceiptRow(label = "Waktu Selesai", value = timestampWib)
                ReceiptRow(label = "Status Jawaban", value = "$answeredCount Butir Lengkap")

                // Checksum
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceContainerHigh, RoundedCornerShape(6.dp))
                        .padding(8.dp)
                ) {
                    Text("Integritas SHA-256 Checksum", fontSize = 9.sp, color = OnSurfaceVariant)
                    Text(
                        text = checksumSha256,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        color = OnSurfaceVariant,
                        maxLines = 1
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Instructions Post-Exam
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = Secondary, modifier = Modifier.size(16.dp))
                    Text(
                        text = "Instruksi Pasca Ujian",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                }
                Text(
                    text = "1. Tunjukkan kode tanda terima di atas kepada pengawas ruang sebelum meninggalkan kelas.\n2. Tetap tenang dan jangan membuka aplikasi lain hingga pengawas memverifikasi status Anda.",
                    fontSize = 11.sp,
                    color = OnSurfaceVariant,
                    lineHeight = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons
        Button(
            onClick = {
                Toast.makeText(context, "Tanda terima disimpan ke perangkat", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SecondaryContainer,
                contentColor = OnSecondaryContainer
            )
        ) {
            Icon(Icons.Default.DownloadForOffline, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Simpan Bukti Tanda Terima (PDF/Gambar)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onExitSession,
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(Icons.Default.LockReset, contentDescription = null, modifier = Modifier.size(18.dp), tint = Primary)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Keluar & Selesaikan Sesi", fontSize = 13.sp, color = Primary, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun ReceiptRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = OnSurfaceVariant)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Primary)
    }
}
