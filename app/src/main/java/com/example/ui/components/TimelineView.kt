package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderGlass
import com.example.ui.theme.CardCharcoal
import com.example.ui.theme.DarkCharcoal
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.ElectricVioletDark
import com.example.ui.theme.MetallicGold
import com.example.ui.theme.MetallicGoldDark
import com.example.ui.theme.SurfaceCharcoal
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@Composable
fun MultiTrackTimelineView(
    totalDurationSeconds: Int,
    currentPositionSeconds: Int,
    onSeek: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val duration = totalDurationSeconds.coerceAtLeast(30)
    val progress = (currentPositionSeconds.toFloat() / duration).coerceIn(0f, 1f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceCharcoal)
            .border(1.dp, BorderGlass, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        // Time ruler header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TIMELINE TRACKS",
                style = MaterialTheme.typography.bodySmall,
                color = MetallicGold,
                fontSize = 10.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "${formatTime(currentPositionSeconds)} / ${formatTime(duration)}",
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val newProgress = (offset.x / size.width).coerceIn(0f, 1f)
                        onSeek((newProgress * duration).toInt())
                    }
                }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                // Track 1: Video
                TimelineTrack(
                    trackName = "Video",
                    icon = Icons.Default.Videocam,
                    trackColor = ElectricViolet,
                    clipCount = 5,
                    label = "Scenes (16:9 / 9:16)"
                )

                // Track 2: Voice Narration (Burmese & English)
                TimelineTrack(
                    trackName = "Narration",
                    icon = Icons.Default.VolumeUp,
                    trackColor = MetallicGold,
                    clipCount = 4,
                    label = "Burmese (မြန်မာ) + English Dual Voice"
                )

                // Track 3: Background Music
                TimelineTrack(
                    trackName = "Music BGM",
                    icon = Icons.Default.Audiotrack,
                    trackColor = Color(0xFF3B82F6),
                    clipCount = 2,
                    label = "Cinematic Synth BGM (Volume: 30%)"
                )

                // Track 4: SRT Captions
                TimelineTrack(
                    trackName = "Subtitles",
                    icon = Icons.Default.Subtitles,
                    trackColor = Color(0xFF22C55E),
                    clipCount = 6,
                    label = "Timed Captions (SRT / VTT)"
                )
            }

            // Playhead red line
            Canvas(
                modifier = Modifier
                    .matchParentSize()
            ) {
                val xPos = size.width * progress
                drawLine(
                    color = Color.Red,
                    start = Offset(xPos, 0f),
                    end = Offset(xPos, size.height),
                    strokeWidth = 4f
                )
                drawCircle(
                    color = Color.Red,
                    radius = 10f,
                    center = Offset(xPos, 0f)
                )
            }
        }
    }
}

@Composable
private fun TimelineTrack(
    trackName: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    trackColor: Color,
    clipCount: Int,
    label: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(CardCharcoal)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = trackName,
            tint = trackColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = trackName,
            style = MaterialTheme.typography.bodySmall,
            color = TextPrimary,
            fontSize = 11.sp,
            modifier = Modifier.width(70.dp)
        )

        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            for (i in 0 until clipCount) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(4.dp))
                        .background(trackColor.copy(alpha = 0.35f))
                        .border(1.dp, trackColor, RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (i == 0) {
                        Text(
                            text = label,
                            color = TextPrimary,
                            fontSize = 8.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

private fun formatTime(totalSeconds: Int): String {
    val m = totalSeconds / 60
    val s = totalSeconds % 60
    return String.format("%02d:%02d", m, s)
}
