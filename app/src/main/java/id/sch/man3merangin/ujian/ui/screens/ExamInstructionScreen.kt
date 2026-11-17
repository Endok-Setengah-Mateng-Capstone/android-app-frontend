package id.sch.man3merangin.ujian.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import id.sch.man3merangin.ujian.ui.theme.Space
import id.sch.man3merangin.ujian.ui.theme.TapTarget

@Composable
fun ExamInstructionScreen(
    studentName: String = "Ahmad Fauzan Al-Ghifari",
    nisn: String = "212210045",
    examTitle: String = "Biologi & Fikih Lingkungan",
    durationMinutes: Int = 90,
    totalQuestions: Int = 40,
    onStartExam: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
            .padding(Space.l),
        verticalArrangement = Arrangement.spacedBy(Space.l)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Space.xs)) {
            Text(
                "Periksa data kamu",
                style = MaterialTheme.typography.titleLarge,
                color = colors.onBackground
            )
            Text(
                "Pastikan nama dan mata pelajaran sudah benar sebelum mulai.",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = colors.surfaceContainerLowest),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier.padding(Space.l),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Space.l)
            ) {
                IconBadge(Icons.Default.Person, size = 56.dp)
                Column {
                    Text(
                        studentName,
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.onSurface
                    )
                    Text(
                        "NISN $nisn",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onSurfaceVariant
                    )
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = colors.surfaceContainerLow)
        ) {
            Column(
                modifier = Modifier.padding(Space.l),
                verticalArrangement = Arrangement.spacedBy(Space.m)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Space.s)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Assignment, contentDescription = null, tint = colors.primary)
                    Text(
                        examTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.onSurface
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(Space.m)) {
                    Fact(Icons.Default.Schedule, "Durasi", "$durationMinutes menit", Modifier.weight(1f))
                    Fact(Icons.AutoMirrored.Filled.Assignment, "Jumlah soal", "$totalQuestions soal", Modifier.weight(1f))
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = colors.surfaceContainerLowest),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(Space.l),
                verticalArrangement = Arrangement.spacedBy(Space.l)
            ) {
                Text(
                    "Aturan selama ujian",
                    style = MaterialTheme.typography.titleSmall,
                    color = colors.onSurface
                )
                Rule(
                    Icons.Default.Lock,
                    "Tetap di aplikasi ini",
                    "Jangan pindah ke aplikasi lain, membuka layar terbagi, atau jendela melayang."
                )
                Rule(
                    Icons.Default.CloudSync,
                    "Jawaban tersimpan otomatis",
                    "Setiap pilihanmu disimpan di HP, lalu dikirim ke server saat ada jaringan."
                )
                Rule(
                    Icons.Default.Shield,
                    "Pelanggaran dicatat",
                    "Keluar dari aplikasi membunyikan alarm dan masuk ke catatan guru. Guru yang memutuskan tindak lanjutnya."
                )
                Rule(
                    Icons.Default.Psychology,
                    "Kerjakan dengan jujur",
                    "Mulailah dengan basmalah dan kerjakan sendiri."
                )
            }
        }

        Button(
            onClick = onStartExam,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = TapTarget),
            shape = MaterialTheme.shapes.small
        ) {
            Text("Mulai ujian")
            Spacer(Modifier.width(Space.s))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun Fact(icon: ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(Space.m),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Space.s)
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            Column {
                Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(value, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

@Composable
private fun Rule(icon: ImageVector, title: String, desc: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(Space.m)) {
        IconBadge(icon, size = 36.dp)
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
            Text(desc, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
