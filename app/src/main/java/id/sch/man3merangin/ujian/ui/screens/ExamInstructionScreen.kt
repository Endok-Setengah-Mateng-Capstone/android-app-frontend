package id.sch.man3merangin.ujian.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun ExamInstructionScreen(
    studentName: String = "Ahmad Fauzan Al-Ghifari",
    nisn: String = "212210045",
    examTitle: String = "Biologi & Fikih Lingkungan",
    durationMinutes: Int = 90,
    totalQuestions: Int = 40,
    onStartExam: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "Konfirmasi Peserta & Tata Tertib",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Primary,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Pastikan data diri dan mata pelajaran telah sesuai",
            fontSize = 12.sp,
            color = OnSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Student Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = SurfaceContainerHigh,
                    modifier = Modifier.size(54.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = Secondary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = studentName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                    Text(
                        text = "NISN: $nisn • XII IPA 1",
                        fontSize = 12.sp,
                        color = OnSurfaceVariant
                    )
                    Text(
                        text = "Ruang: Lab Komputer 2 (Meja 14)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Secondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Exam Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Assignment, contentDescription = null, tint = Secondary)
                    Text(
                        text = examTitle,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SurfaceContainerLowest,
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = Secondary, modifier = Modifier.size(18.dp))
                            Column {
                                Text("Durasi", fontSize = 10.sp, color = OnSurfaceVariant)
                                Text("$durationMinutes Menit", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Primary)
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SurfaceContainerLowest,
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Assignment, contentDescription = null, tint = Secondary, modifier = Modifier.size(18.dp))
                            Column {
                                Text("Jumlah Soal", fontSize = 10.sp, color = OnSurfaceVariant)
                                Text("$totalQuestions Butir", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Primary)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Instructions list
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Instruksi & Integritas Ujian",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )

                InstructionItem(
                    icon = Icons.Default.Lock,
                    title = "Aplikasi Terkunci Otomatis",
                    desc = "Perangkat wajib terkunci di aplikasi CBT. Jangan berpindah aplikasi atau mengaktifkan mode split screen."
                )

                InstructionItem(
                    icon = Icons.Default.CloudSync,
                    title = "Penyimpanan Jawaban Mandiri",
                    desc = "Jawaban disimpan otomatis setiap saat di memori perangkat dan tersinkron berkala ke server madrasah."
                )

                InstructionItem(
                    icon = Icons.Default.Shield,
                    title = "Pencatatan Anomali & Alarm",
                    desc = "Tindakan keluar aplikasi atau mematikan layar akan dicatat pada log pengawas sebagai anomali integritas."
                )

                InstructionItem(
                    icon = Icons.Default.Psychology,
                    title = "Kejujuran Islami",
                    desc = "Mulailah dengan basmalah, kerjakan dengan mandiri, cermat, dan junjung tinggi kejujuran Islami."
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onStartExam,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SecondaryContainer,
                contentColor = OnSecondaryContainer
            )
        ) {
            Text("Mulai Mengerjakan Ujian", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun InstructionItem(
    icon: ImageVector,
    title: String,
    desc: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = CircleShape,
            color = SurfaceContainerHigh,
            modifier = Modifier.size(32.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = Secondary, modifier = Modifier.size(16.dp))
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Primary)
            Text(desc, fontSize = 12.sp, color = OnSurfaceVariant, lineHeight = 16.sp)
        }
    }
}
