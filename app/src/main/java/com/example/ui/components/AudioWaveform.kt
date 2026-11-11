package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CardCharcoal
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.MetallicGold
import kotlin.random.Random

@Composable
fun AudioWaveformVisualizer(
    isRecording: Boolean,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "waveform")
    val animFactor by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "animFactor"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(CardCharcoal)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val barWidth = 6f
            val space = 4f
            val totalBars = (size.width / (barWidth + space)).toInt()

            for (i in 0 until totalBars) {
                val x = i * (barWidth + space)
                val baseHeight = if (isRecording) {
                    val rand = (i * 37 + (animFactor * 100).toInt()) % 100
                    (rand / 100f) * size.height * 0.85f
                } else {
                    size.height * 0.2f
                }
                val h = baseHeight.coerceAtLeast(8f)
                val y = (size.height - h) / 2f

                val color = if (i % 2 == 0) ElectricViolet else MetallicGold

                drawRoundRect(
                    color = if (isRecording) color else color.copy(alpha = 0.3f),
                    topLeft = Offset(x, y),
                    size = Size(barWidth, h),
                    cornerRadius = CornerRadius(3f, 3f)
                )
            }
        }
    }
}
