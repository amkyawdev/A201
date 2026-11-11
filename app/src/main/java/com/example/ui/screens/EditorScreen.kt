package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.VolumeUp
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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.HeaderBar
import com.example.ui.components.MultiTrackTimelineView
import com.example.ui.theme.CardCharcoal
import com.example.ui.theme.DarkCharcoal
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.ElectricVioletGlow
import com.example.ui.theme.MetallicGold
import com.example.ui.theme.MetallicGoldGlow
import com.example.ui.theme.SurfaceCharcoal
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun EditorScreen(
    viewModel: StudioViewModel,
    onNavigateToExport: () -> Unit,
    onNavigateToSubtitles: () -> Unit
) {
    val project by viewModel.activeProject.collectAsStateWithLifecycle()

    var isPlaying by remember { mutableStateOf(false) }
    var currentPositionSec by remember { mutableIntStateOf(0) }

    var selectedAspectRatio by remember { mutableStateOf(project?.aspectRatio ?: "9:16") }
    var narrationVolume by remember { mutableFloatStateOf(project?.narrationVolume ?: 1.0f) }
    var bgmVolume by remember { mutableFloatStateOf(project?.bgmVolume ?: 0.3f) }
    var watermarkText by remember { mutableStateOf(project?.watermarkText ?: "Movie Recap Studio") }

    val aspectRatios = listOf("9:16", "16:9", "1:1")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkCharcoal)
    ) {
        HeaderBar(
            title = project?.title ?: "Multi-Track Editor",
            subtitle = "Video Trimming • Narration Mixing • Aspect Conversion"
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Video Preview Canvas Box
            item {
                GlassmorphicCard(
                    glowColor = ElectricVioletGlow,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Dynamic Aspect Ratio Preview Window
                        val aspectMultiplier = when (selectedAspectRatio) {
                            "16:9" -> 16f / 9f
                            "1:1" -> 1f
                            else -> 9f / 16f // 9:16 Vertical
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth(if (selectedAspectRatio == "9:16") 0.65f else 1f)
                                .aspectRatio(aspectMultiplier)
                                .clip(RoundedCornerShape(12.dp))
                                .background(CardCharcoal)
                                .border(1.dp, MetallicGold, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Movie,
                                    contentDescription = null,
                                    tint = ElectricViolet,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = project?.title ?: "No Active Video",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Previewing $selectedAspectRatio Mode",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted
                                )
                            }

                            // Watermark Overlay Preview
                            if (watermarkText.isNotBlank()) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(8.dp)
                                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = watermarkText,
                                        color = MetallicGold,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Playback Control Bar
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            IconButton(
                                onClick = { isPlaying = !isPlaying },
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(ElectricViolet)
                                    .testTag("play_pause_button")
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play/Pause",
                                    tint = TextPrimary
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Timecode: ${currentPositionSec}s / ${project?.targetDurationSeconds ?: 180}s",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                                Slider(
                                    value = currentPositionSec.toFloat(),
                                    onValueChange = { currentPositionSec = it.toInt() },
                                    valueRange = 0f..(project?.targetDurationSeconds?.toFloat() ?: 180f),
                                    colors = SliderDefaults.colors(
                                        thumbColor = MetallicGold,
                                        activeTrackColor = ElectricViolet
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Visual Multi-Track Timeline
            item {
                MultiTrackTimelineView(
                    totalDurationSeconds = project?.targetDurationSeconds ?: 180,
                    currentPositionSeconds = currentPositionSec,
                    onSeek = { currentPositionSec = it }
                )
            }

            // Studio Controls & Mixing Panel
            item {
                GlassmorphicCard(
                    glowColor = MetallicGoldGlow,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "AUDIO & ASPECT RATIO MIXER",
                            style = MaterialTheme.typography.bodySmall,
                            color = MetallicGold,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Output Aspect Ratio:",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            aspectRatios.forEach { ar ->
                                FilterChip(
                                    selected = selectedAspectRatio == ar,
                                    onClick = {
                                        selectedAspectRatio = ar
                                        viewModel.updateProjectSettings(ar, narrationVolume, bgmVolume, watermarkText)
                                    },
                                    label = { Text(ar, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = ElectricViolet,
                                        selectedLabelColor = TextPrimary
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Burmese/English Narration Volume: ${(narrationVolume * 100).toInt()}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        Slider(
                            value = narrationVolume,
                            onValueChange = {
                                narrationVolume = it
                                viewModel.updateProjectSettings(selectedAspectRatio, it, bgmVolume, watermarkText)
                            },
                            valueRange = 0f..1.5f,
                            colors = SliderDefaults.colors(
                                thumbColor = ElectricViolet,
                                activeTrackColor = ElectricViolet
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Background Music (BGM) Volume: ${(bgmVolume * 100).toInt()}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        Slider(
                            value = bgmVolume,
                            onValueChange = {
                                bgmVolume = it
                                viewModel.updateProjectSettings(selectedAspectRatio, narrationVolume, it, watermarkText)
                            },
                            valueRange = 0f..1.0f,
                            colors = SliderDefaults.colors(
                                thumbColor = MetallicGold,
                                activeTrackColor = MetallicGold
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = watermarkText,
                            onValueChange = {
                                watermarkText = it
                                viewModel.updateProjectSettings(selectedAspectRatio, narrationVolume, bgmVolume, it)
                            },
                            label = { Text("Studio Watermark Overlay", color = TextSecondary) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricViolet,
                                unfocusedBorderColor = CardCharcoal,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = onNavigateToSubtitles,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MetallicGold)
                            ) {
                                Icon(Icons.Default.Subtitles, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("SRT Captions")
                            }

                            Button(
                                onClick = onNavigateToExport,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("export_video_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet)
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Export Video", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
