package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.ui.theme.CardCharcoal
import com.example.ui.theme.DarkCharcoal
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.ElectricVioletGlow
import com.example.ui.theme.MetallicGold
import com.example.ui.theme.MetallicGoldGlow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.GeneratorUiState
import com.example.ui.viewmodel.StudioViewModel

@Composable
fun RecapGeneratorScreen(
    viewModel: StudioViewModel,
    onScriptGenerated: () -> Unit
) {
    var movieTitle by remember { mutableStateOf("Neon Shadows: Cyber City") }
    var selectedGenre by remember { mutableStateOf("Action Thriller") }
    var targetDuration by remember { mutableStateOf(180) } // Seconds
    var customPrompt by remember { mutableStateOf("Focus on the climax battle scene and the mysterious betrayal under the neon rain.") }

    val generatorState by viewModel.generatorState.collectAsStateWithLifecycle()

    val genres = listOf("Action Thriller", "Sci-Fi Mystery", "Horror", "Comedy", "Romantic Drama", "Anime / Reels")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkCharcoal)
    ) {
        HeaderBar(
            title = "AI Recap Generator",
            subtitle = "Scene-by-Scene Script • Burmese (မြန်မာ) + English Voiceover"
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                GlassmorphicCard(
                    glowColor = ElectricVioletGlow,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "MOVIE RECAP PARAMETERS",
                            style = MaterialTheme.typography.bodySmall,
                            color = MetallicGold,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = movieTitle,
                            onValueChange = { movieTitle = it },
                            label = { Text("Movie Title / Subject", color = TextSecondary) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("movie_title_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricViolet,
                                unfocusedBorderColor = CardCharcoal,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Select Genre / Tone:",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            genres.take(3).forEach { g ->
                                FilterChip(
                                    selected = selectedGenre == g,
                                    onClick = { selectedGenre = g },
                                    label = { Text(g, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = ElectricViolet,
                                        selectedLabelColor = TextPrimary
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Target Recap Duration: ${targetDuration}s (${targetDuration / 60}m ${targetDuration % 60}s)",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )

                        Slider(
                            value = targetDuration.toFloat(),
                            onValueChange = { targetDuration = it.toInt() },
                            valueRange = 60f..300f,
                            steps = 3,
                            colors = SliderDefaults.colors(
                                thumbColor = MetallicGold,
                                activeTrackColor = ElectricViolet
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = customPrompt,
                            onValueChange = { customPrompt = it },
                            label = { Text("Custom Plot Focus / Instructions", color = TextSecondary) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("custom_prompt_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricViolet,
                                unfocusedBorderColor = CardCharcoal,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            maxLines = 3
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                viewModel.generateRecap(
                                    movieTitle = movieTitle,
                                    genre = selectedGenre,
                                    targetDurationSeconds = targetDuration,
                                    userPrompt = customPrompt
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("generate_script_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                            enabled = generatorState !is GeneratorUiState.Loading
                        ) {
                            if (generatorState is GeneratorUiState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = TextPrimary,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Generating AI Script...")
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Generate Scene Breakdown & Narration", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // State Results
            when (val state = generatorState) {
                is GeneratorUiState.Success -> {
                    item {
                        GlassmorphicCard(
                            glowColor = MetallicGoldGlow,
                            borderColor = MetallicGold,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Movie,
                                        contentDescription = null,
                                        tint = MetallicGold
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = state.script.movieTitle,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Button(
                                        onClick = onScriptGenerated,
                                        colors = ButtonDefaults.buttonColors(containerColor = MetallicGold)
                                    ) {
                                        Text("Open in Timeline", color = DarkCharcoal, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = state.script.logline,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    items(state.script.scenes) { scene ->
                        GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(ElectricViolet)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "${scene.timeStartSeconds}s - ${scene.timeEndSeconds}s",
                                            fontSize = 11.sp,
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = scene.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Shot: ${scene.shotDescription}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "မြန်မာနောက်ခံစကားပြော:",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MetallicGold,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = scene.narrationBurmese,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimary
                                )

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "English Narration:",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ElectricViolet,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = scene.narrationEnglish,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }

                is GeneratorUiState.Error -> {
                    item {
                        GlassmorphicCard(
                            glowColor = Color.Red.copy(alpha = 0.2f),
                            borderColor = Color.Red,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Error: ${state.message}",
                                color = Color.Red,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                else -> {}
            }
        }
    }
}
