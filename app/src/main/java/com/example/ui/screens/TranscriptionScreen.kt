package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AudioWaveformVisualizer
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.HeaderBar
import com.example.ui.theme.DarkCharcoal
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.ElectricVioletGlow
import com.example.ui.theme.MetallicGold
import com.example.ui.theme.MetallicGoldGlow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun TranscriptionScreen(
    viewModel: StudioViewModel,
    onSubtitlesGenerated: () -> Unit
) {
    val isRecording by viewModel.isRecordingAudio.collectAsStateWithLifecycle()
    val recordingDuration by viewModel.recordingDurationSec.collectAsStateWithLifecycle()
    val subtitles by viewModel.subtitles.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkCharcoal)
    ) {
        HeaderBar(
            title = "Audio & Speech Transcription",
            subtitle = "Microphone Voice Recording • Speech-to-Text • Burmese Subtitles"
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                GlassmorphicCard(
                    glowColor = if (isRecording) MetallicGoldGlow else ElectricVioletGlow,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isRecording) "RECORDING VOICE NARRATION..." else "STUDIO MICROPHONE INPUT",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isRecording) MetallicGold else ElectricViolet,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        AudioWaveformVisualizer(isRecording = isRecording)

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Duration: ${recordingDuration}s",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        IconButton(
                            onClick = {
                                if (isRecording) {
                                    viewModel.stopAudioRecordingAndTranscribe()
                                } else {
                                    viewModel.startAudioRecording()
                                }
                            },
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(if (isRecording) MetallicGold else ElectricViolet)
                                .testTag("record_mic_button")
                        ) {
                            Icon(
                                imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                                contentDescription = if (isRecording) "Stop Recording" else "Start Recording",
                                tint = DarkCharcoal,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (isRecording) "Tap to finish recording & transcribe to Burmese subtitles" else "Tap microphone to record voiceover comment",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Generated Subtitles List Preview
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Transcribed Timed Captions (${subtitles.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (subtitles.isNotEmpty()) {
                        Button(
                            onClick = onSubtitlesGenerated,
                            colors = ButtonDefaults.buttonColors(containerColor = MetallicGold)
                        ) {
                            Icon(Icons.Default.Subtitles, contentDescription = null, tint = DarkCharcoal)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Open Subtitle Studio", color = DarkCharcoal, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (subtitles.isEmpty()) {
                item {
                    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "No transcribed audio items yet. Record narration above or import speech track.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }
                }
            } else {
                items(subtitles) { sub ->
                    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            Text(
                                text = "${sub.formattedStartTime()} ➔ ${sub.formattedEndTime()}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MetallicGold,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "မြန်မာ: ${sub.textBurmese}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "EN: ${sub.textEnglish}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}
