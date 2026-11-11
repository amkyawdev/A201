package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.HeaderBar
import com.example.ui.theme.CardCharcoal
import com.example.ui.theme.DarkCharcoal
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.ElectricVioletGlow
import com.example.ui.theme.MetallicGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun SubtitlesScreen(viewModel: StudioViewModel) {
    val subtitles by viewModel.subtitles.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var newBurmese by remember { mutableStateOf("") }
    var newEnglish by remember { mutableStateOf("") }
    var startMsText by remember { mutableStateOf("0") }
    var endMsText by remember { mutableStateOf("4000") }
    var languageMode by remember { mutableStateOf("dual") } // "dual", "my", "en"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkCharcoal)
    ) {
        HeaderBar(
            title = "SRT & VTT Subtitle Editor",
            subtitle = "Burmese (မြန်မာ) + English Caption Timing & Export"
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Language Display Filter
            item {
                GlassmorphicCard(
                    glowColor = ElectricVioletGlow,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "CAPTION DISPLAY MODE & EXPORT",
                            style = MaterialTheme.typography.bodySmall,
                            color = MetallicGold,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = languageMode == "dual",
                                onClick = { languageMode = "dual" },
                                label = { Text("Dual (မြန်မာ + EN)", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ElectricViolet)
                            )
                            FilterChip(
                                selected = languageMode == "my",
                                onClick = { languageMode = "my" },
                                label = { Text("Burmese Only (မြန်မာ)", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ElectricViolet)
                            )
                            FilterChip(
                                selected = languageMode == "en",
                                onClick = { languageMode = "en" },
                                label = { Text("English Only", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ElectricViolet)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val srtContent = viewModel.exportSrtFormat(languageMode)
                                    clipboardManager.setText(AnnotatedString(srtContent))
                                    Toast.makeText(context, "SRT copied to Clipboard!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("copy_srt_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = MetallicGold)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, tint = DarkCharcoal, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Copy .SRT", color = DarkCharcoal, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    val vttContent = viewModel.exportVttFormat(languageMode)
                                    clipboardManager.setText(AnnotatedString(vttContent))
                                    Toast.makeText(context, "VTT copied to Clipboard!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
                            ) {
                                Icon(Icons.Default.Subtitles, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Copy .VTT", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Add New Subtitle Form
            item {
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(
                            text = "ADD NEW SUBTITLE CUE",
                            style = MaterialTheme.typography.bodySmall,
                            color = ElectricViolet,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = newBurmese,
                            onValueChange = { newBurmese = it },
                            label = { Text("Burmese Caption Text (မြန်မာ)", color = TextSecondary) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricViolet,
                                unfocusedBorderColor = CardCharcoal,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = newEnglish,
                            onValueChange = { newEnglish = it },
                            label = { Text("English Translation Text", color = TextSecondary) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricViolet,
                                unfocusedBorderColor = CardCharcoal,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = startMsText,
                                onValueChange = { startMsText = it },
                                label = { Text("Start (ms)", color = TextSecondary) },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )

                            OutlinedTextField(
                                value = endMsText,
                                onValueChange = { endMsText = it },
                                label = { Text("End (ms)", color = TextSecondary) },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                val start = startMsText.toLongOrNull() ?: 0L
                                val end = endMsText.toLongOrNull() ?: 4000L
                                if (newBurmese.isNotBlank() || newEnglish.isNotBlank()) {
                                    viewModel.addSubtitle(newBurmese, newEnglish, start, end)
                                    newBurmese = ""
                                    newEnglish = ""
                                    startMsText = "$end"
                                    endMsText = "${end + 4000}"
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("add_subtitle_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add Subtitle Cue")
                        }
                    }
                }
            }

            // Subtitle List
            items(subtitles) { sub ->
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "[${sub.formattedStartTime()} ➔ ${sub.formattedEndTime()}]",
                                style = MaterialTheme.typography.bodySmall,
                                color = MetallicGold,
                                fontWeight = FontWeight.Bold
                            )
                            if (sub.textBurmese.isNotBlank()) {
                                Text(
                                    text = sub.textBurmese,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            if (sub.textEnglish.isNotBlank()) {
                                Text(
                                    text = sub.textEnglish,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }

                        IconButton(onClick = { viewModel.deleteSubtitle(sub.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextMuted)
                        }
                    }
                }
            }
        }
    }
}
