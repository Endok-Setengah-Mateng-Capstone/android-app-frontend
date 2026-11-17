package id.sch.man3merangin.ujian.ui.screens

import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import id.sch.man3merangin.ujian.ui.theme.MonoStyle
import id.sch.man3merangin.ujian.ui.theme.Space
import id.sch.man3merangin.ujian.ui.theme.TapTarget

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
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Space.l, vertical = Space.xl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconBadge(Icons.Default.School, size = 72.dp)
        Spacer(Modifier.size(Space.m))
        Text(
            "Ujian MAN 3 Merangin",
            style = MaterialTheme.typography.titleLarge,
            color = colors.onBackground,
            textAlign = TextAlign.Center
        )
        Text(
            "Masukkan NISN dan token dari pengawas.",
            style = MaterialTheme.typography.bodyMedium,
            color = colors.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.size(Space.xl))

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
                OutlinedTextField(
                    value = nisn,
                    onValueChange = { nisn = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("NISN") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    placeholder = { Text("Contoh: 212210045") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = MaterialTheme.shapes.small
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
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        label = { Text("Sesi ujian") },
                        leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sessionExpanded) },
                        shape = MaterialTheme.shapes.small
                    )
                    ExposedDropdownMenu(
                        expanded = sessionExpanded,
                        onDismissRequest = { sessionExpanded = false }
                    ) {
                        sessions.forEach { s ->
                            DropdownMenuItem(
                                text = { Text(s, style = MaterialTheme.typography.bodyMedium) },
                                modifier = Modifier.heightIn(min = TapTarget),
                                onClick = {
                                    selectedSession = s
                                    sessionExpanded = false
                                }
                            )
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(Space.xs)) {
                    OutlinedTextField(
                        value = token,
                        onValueChange = { token = it.uppercase().take(10) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Token ujian") },
                        leadingIcon = { Icon(Icons.Default.Key, contentDescription = null) },
                        textStyle = MonoStyle.copy(fontSize = MaterialTheme.typography.titleMedium.fontSize),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                        singleLine = true,
                        shape = MaterialTheme.shapes.small,
                        supportingText = { Text("Token ada pada pengawas ruang.") }
                    )
                    TextButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = clipboard.primaryClip?.getItemAt(0)?.text?.toString()
                            if (!clip.isNullOrBlank()) {
                                token = clip.trim().uppercase()
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.End)
                            .heightIn(min = TapTarget)
                    ) {
                        Icon(Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(Space.xs))
                        Text("Tempel token")
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.surfaceContainer, MaterialTheme.shapes.small)
                        .padding(Space.m),
                    horizontalArrangement = Arrangement.spacedBy(Space.m),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = colors.primary)
                    Text(
                        "Selama ujian, tangkapan layar dinonaktifkan. Keluar dari aplikasi akan dicatat dan dilaporkan ke guru.",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onSurfaceVariant
                    )
                }

                if (!errorMessage.isNullOrBlank()) {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.error,
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics { liveRegion = LiveRegionMode.Polite },
                        textAlign = TextAlign.Center
                    )
                }

                Button(
                    onClick = { onValidate(nisn, selectedSession, token) },
                    enabled = !isLoading && nisn.isNotBlank() && token.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = TapTarget),
                    shape = MaterialTheme.shapes.small
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = colors.onPrimary,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(Space.s))
                        Text("Memeriksa token…")
                    } else {
                        Text("Masuk ujian")
                        Spacer(Modifier.width(Space.s))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
