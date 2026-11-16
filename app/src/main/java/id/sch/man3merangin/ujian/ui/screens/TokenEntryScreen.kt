package id.sch.man3merangin.ujian.ui.screens

import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.sch.man3merangin.ujian.ui.theme.GreenActive
import id.sch.man3merangin.ujian.ui.theme.GreenActiveContainer
import id.sch.man3merangin.ujian.ui.theme.OnPrimary
import id.sch.man3merangin.ujian.ui.theme.OnSecondaryContainer
import id.sch.man3merangin.ujian.ui.theme.OnSurface
import id.sch.man3merangin.ujian.ui.theme.OnSurfaceVariant
import id.sch.man3merangin.ujian.ui.theme.OutlineVariant
import id.sch.man3merangin.ujian.ui.theme.Primary
import id.sch.man3merangin.ujian.ui.theme.Secondary
import id.sch.man3merangin.ujian.ui.theme.SecondaryContainer
import id.sch.man3merangin.ujian.ui.theme.SurfaceContainer
import id.sch.man3merangin.ujian.ui.theme.SurfaceContainerHigh
import id.sch.man3merangin.ujian.ui.theme.SurfaceContainerLow
import id.sch.man3merangin.ujian.ui.theme.SurfaceContainerLowest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TokenEntryScreen(
    initialNisn: String = "212210045",
    initialToken: String = "MN3-9X4",
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onValidate: (nisn: String, session: String, token: String) -> Unit
) {
    var nisn by remember { mutableStateOf(initialNisn) }
    var token by remember { mutableStateOf(initialToken) }

    val sessions = listOf(
        "XII IPA 1 — Pagi (07.30 - 09.30 WIB)",
        "XII IPA 2 — Pagi (07.30 - 09.30 WIB)",
        "XII IPS 1 — Siang (10.00 - 12.00 WIB)",
        "XII Keagamaan — Siang (10.00 - 12.00 WIB)"
    )
    var selectedSession by remember { mutableStateOf(sessions[0]) }
    var sessionExpanded by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(id.sch.man3merangin.ujian.ui.theme.Surface)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar: Portal Asesmen Terkunci & Status Lokal Aktif
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = null,
                    tint = Secondary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "PORTAL ASESMEN TERKUNCI",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceVariant,
                    letterSpacing = 0.5.sp
                )
            }

            Surface(
                shape = CircleShape,
                color = SurfaceContainerHigh
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(GreenActive, CircleShape)
                    )
                    Text(
                        text = "Lokal Aktif",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GreenActive
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Hero School Identity
        Surface(
            shape = CircleShape,
            color = SurfaceContainerHigh,
            modifier = Modifier.size(72.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = "MAN 3 Merangin",
                    tint = Secondary,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            shape = CircleShape,
            color = SurfaceContainer
        ) {
            Text(
                text = "T.A. 2024/2025",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Secondary,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 3.dp)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Asesmen Digital Madrasah",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Primary,
            textAlign = TextAlign.Center
        )
        Text(
            text = "MAN 3 Merangin • Sistem Evaluasi Terpadu Berbasis Digital",
            fontSize = 12.sp,
            color = OnSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Card Form Input
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
                // NISN
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "NISN / Nomor Peserta",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OnSurface
                        )
                        Text(
                            text = "Wajib diisi",
                            fontSize = 11.sp,
                            color = OnSurfaceVariant
                        )
                    }

                    OutlinedTextField(
                        value = nisn,
                        onValueChange = { nisn = it },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(Icons.Default.Badge, contentDescription = null, tint = Secondary)
                        },
                        placeholder = { Text("Contoh: 212210045") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SurfaceContainerLowest,
                            unfocusedContainerColor = SurfaceContainerLow,
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = Secondary
                        )
                    )
                }

                // Sesi Ujian & Ruangan
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Sesi Ujian & Ruangan",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurface
                    )

                    ExposedDropdownMenuBox(
                        expanded = sessionExpanded,
                        onExpandedChange = { sessionExpanded = !sessionExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedSession,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            leadingIcon = {
                                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Secondary)
                            },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sessionExpanded) },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            textStyle = TextStyle(fontSize = 13.sp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = SurfaceContainerLowest,
                                unfocusedContainerColor = SurfaceContainerLow,
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderColor = Secondary
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = sessionExpanded,
                            onDismissRequest = { sessionExpanded = false }
                        ) {
                            sessions.forEach { s ->
                                DropdownMenuItem(
                                    text = { Text(s, fontSize = 13.sp) },
                                    onClick = {
                                        selectedSession = s
                                        sessionExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Token Input Area
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Token Ujian",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = OnSurface
                            )
                            Icon(Icons.Default.Key, contentDescription = null, tint = Secondary, modifier = Modifier.size(16.dp))
                        }

                        TextButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = clipboard.primaryClip?.getItemAt(0)?.text?.toString()
                                if (!clip.isNullOrBlank()) {
                                    token = clip.trim().uppercase()
                                }
                            }
                        ) {
                            Icon(Icons.Default.ContentPaste, contentDescription = null, tint = Secondary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Tempel", fontSize = 12.sp, color = Secondary, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    OutlinedTextField(
                        value = token,
                        onValueChange = { token = it.uppercase().take(10) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            letterSpacing = 3.sp,
                            textAlign = TextAlign.Center,
                            color = Primary
                        ),
                        placeholder = {
                            Text(
                                "MN3-___",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                color = OutlineVariant
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Characters
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SurfaceContainerLowest,
                            unfocusedContainerColor = SurfaceContainerLow,
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = Secondary
                        )
                    )

                    // Helper Info
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceContainerLow, RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = Secondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Token 6 karakter alfanumerik diberikan langsung oleh guru pengawas ruang melalui papan tulis kelas.",
                            fontSize = 11.sp,
                            color = OnSurfaceVariant,
                            lineHeight = 15.sp
                        )
                    }
                }

                // Integrity checklist box
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceContainer, RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Security,
                        contentDescription = null,
                        tint = Secondary,
                        modifier = Modifier.size(22.dp)
                    )
                    Column {
                        Text(
                            text = "Kamera & Layar Dipantau",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Primary
                        )
                        Text(
                            text = "Membuka aplikasi lain otomatis dilaporkan ke pengawas.",
                            fontSize = 11.sp,
                            color = OnSurfaceVariant
                        )
                    }
                }

                if (!errorMessage.isNullOrBlank()) {
                    Text(
                        text = errorMessage,
                        fontSize = 12.sp,
                        color = Color.Red,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                // Action Button: Validasi & Masuk Ujian
                Button(
                    onClick = { onValidate(nisn, selectedSession, token) },
                    enabled = !isLoading && nisn.isNotBlank() && token.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SecondaryContainer,
                        contentColor = OnSecondaryContainer
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = OnPrimary,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Memverifikasi Berkas...", fontSize = 14.sp)
                    } else {
                        Text("Validasi & Masuk Ujian", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Footer info
        Surface(
            shape = CircleShape,
            color = SurfaceContainer
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    Icons.Default.WifiTethering,
                    contentDescription = null,
                    tint = GreenActive,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Server Madrasah Terhubung (Lokal LAN)",
                    fontSize = 11.sp,
                    color = OnSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "v2.4.1 Client Android Steril • IP: 192.168.10.84",
            fontSize = 11.sp,
            color = OnSurfaceVariant.copy(alpha = 0.8f)
        )
    }
}
