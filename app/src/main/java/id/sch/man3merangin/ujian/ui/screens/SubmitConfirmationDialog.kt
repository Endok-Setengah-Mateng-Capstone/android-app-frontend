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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
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
import id.sch.man3merangin.ujian.ui.theme.SurfaceContainerHigh
import id.sch.man3merangin.ujian.ui.theme.SurfaceContainerLow
import id.sch.man3merangin.ujian.ui.theme.SurfaceContainerLowest
import id.sch.man3merangin.ujian.ui.theme.WarningAmber
import id.sch.man3merangin.ujian.ui.theme.WarningAmberContainer

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clickable(enabled = false) {}
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
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header Icon
                Surface(
                    shape = CircleShape,
                    color = SurfaceContainerHigh,
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Secondary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Text(
                    text = "Konfirmasi Pengumpulan Jawaban",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Pastikan semua nomor soal telah dijawab dengan teliti sebelum mengirimkan lembar ujian.",
                    fontSize = 12.sp,
                    color = OnSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )

                // Status 3 metrics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricBox(
                        title = "Terjawab",
                        value = "$answeredCount",
                        color = GreenActive,
                        bg = GreenActiveContainer,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        title = "Ragu-ragu",
                        value = "$doubtfulCount",
                        color = WarningAmber,
                        bg = WarningAmberContainer,
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        title = "Kosong",
                        value = "$unansweredCount",
                        color = OnSurfaceVariant,
                        bg = SurfaceContainerHigh,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Agreement checkbox
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceContainerLow, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Checkbox(
                        checked = isConfirmed,
                        onCheckedChange = { isConfirmed = it },
                        colors = CheckboxDefaults.colors(checkedColor = Secondary)
                    )
                    Text(
                        text = "Saya telah memeriksa dan yakin untuk mengakhiri sesi ujian ini.",
                        fontSize = 11.sp,
                        color = OnSurface,
                        lineHeight = 15.sp
                    )
                }

                // Action Buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onConfirmSubmit,
                        enabled = isConfirmed,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SecondaryContainer,
                            contentColor = OnSecondaryContainer
                        )
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Ya, Selesaikan & Kumpulkan Ujian", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Kembali Periksa Jawaban", fontSize = 12.sp, color = OnSurface)
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricBox(
    title: String,
    value: String,
    color: Color,
    bg: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bg,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
            Text(title, fontSize = 10.sp, color = color)
        }
    }
}
