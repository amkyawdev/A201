package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.HeaderBar
import com.example.ui.theme.DarkCharcoal
import com.example.ui.theme.ElectricVioletGlow
import com.example.ui.theme.MetallicGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun DocsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkCharcoal)
    ) {
        HeaderBar(
            title = "Studio Documentation & Specs",
            subtitle = "Architecture • FFmpeg Pipeline • Burmese TTS • SRT Standards"
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                DocSection(
                    title = "1. AI RECAP SCRIPT GENERATOR",
                    content = "The Movie Recap Studio generates scene-by-scene scripts with precise timestamps, visual shot descriptions, emotional audio cues, and dual-language narration scripts (Burmese မြန်မာ + English). Users can connect their own Gemini API Key in API Settings for live LLM generation or rely on the built-in cinematic script engine."
                )
            }

            item {
                DocSection(
                    title = "2. MULTI-TRACK TIMELINE EDITOR",
                    content = "The multi-track timeline coordinates 4 distinct media layers:\n• Video Track (Clips, keyframe timestamps)\n• Narration Track (Burmese & English TTS speech)\n• BGM Track (Cinematic music with audio ducking)\n• Subtitle Track (Timed SRT & VTT captions)\nUsers can adjust master volume, narration mix, and aspect ratio conversion (16:9, 9:16 vertical for TikTok/Reels, and 1:1)."
                )
            }

            item {
                DocSection(
                    title = "3. SPEECH TRANSCRIPTION & SUBTITLE STUDIO",
                    content = "Record narration directly via microphone input. Real-time audio visualizers sample speech levels while the transcription engine generates timed subtitles in Burmese and English. Captions can be edited line-by-line and exported to standard .SRT and .VTT files."
                )
            }

            item {
                DocSection(
                    title = "4. FFMPEG RENDERING & EXPORT PIPELINE",
                    content = "Exports videos in 720p, 1080p, or 4K resolutions at 24, 30, or 60 FPS. Applies watermark overlays, aspect ratio crop filters, and audio stream mixing."
                )
            }
        }
    }
}

@Composable
private fun DocSection(title: String, content: String) {
    GlassmorphicCard(
        glowColor = ElectricVioletGlow,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MetallicGold,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                lineHeight = 20.sp
            )
        }
    }
}
