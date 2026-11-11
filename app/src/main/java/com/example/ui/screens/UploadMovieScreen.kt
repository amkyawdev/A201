package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.HeaderBar
import com.example.ui.theme.CardCharcoal
import com.example.ui.theme.DarkCharcoal
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.ElectricVioletGlow
import com.example.ui.theme.MetallicGold
import com.example.ui.theme.MetallicGoldGlow
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SurfaceCharcoal
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.StudioViewModel

data class AutoDetectedScene(
    val title: String,
    val timestampRange: String,
    val type: String
)

@Composable
fun UploadMovieScreen(
    viewModel: StudioViewModel,
    onProceedToScript: () -> Unit
) {
    val context = LocalContext.current

    var selectedVideoUri by remember { mutableStateOf<Uri?>(null) }
    var movieName by remember { mutableStateOf("Cyber_Flicks_Movie_Full.mp4") }
    var detectedDuration by remember { mutableStateOf("01:48:32 (108 minutes)") }
    var detectedResolution by remember { mutableStateOf("1080p Full HD (1920x1080 @ 24fps)") }

    var importMode by remember { mutableStateOf("auto_highlights") } // "auto_highlights", "full_movie", "trailer_cut"
    var extractAudioTrack by remember { mutableStateOf(true) }

    val videoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedVideoUri = uri
            movieName = uri.lastPathSegment ?: "Uploaded_Movie_File.mp4"
            viewModel.updateVideoUri(uri)
            Toast.makeText(context, "Movie Video Selected Successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    val simulatedKeyframes = listOf(
        AutoDetectedScene("Intro: Neon City Entrance", "00:02:15 - 00:08:40", "Opening Act"),
        AutoDetectedScene("Inciting Incident: The Secret Code", "00:24:10 - 00:31:00", "Plot Twist"),
        AutoDetectedScene("Midpoint Battle: High Speed Chase", "00:52:00 - 00:58:30", "Action Climax"),
        AutoDetectedScene("Final Showdown: Rooftop Duel", "01:32:10 - 01:41:00", "Resolution")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkCharcoal)
    ) {
        HeaderBar(
            title = "Movie Upload & Import Studio",
            subtitle = "Video Analysis • Scene Detection • Keyframe Extraction"
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Step 1: Upload Video Drop Area
            item {
                GlassmorphicCard(
                    glowColor = if (selectedVideoUri != null) SuccessGreen.copy(alpha = 0.3f) else ElectricVioletGlow,
                    borderColor = if (selectedVideoUri != null) SuccessGreen else ElectricViolet,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(if (selectedVideoUri != null) SuccessGreen else ElectricViolet),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (selectedVideoUri != null) Icons.Default.CheckCircle else Icons.Default.CloudUpload,
                                contentDescription = "Upload Movie",
                                tint = TextPrimary,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (selectedVideoUri != null) "MOVIE FILE READY FOR RECAP" else "SELECT MOVIE / VIDEO FILE",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Text(
                            text = if (selectedVideoUri != null) movieName else "Supports MP4, MKV, MOV, AVI (up to 4K resolution)",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { videoPicker.launch("video/*") },
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .testTag("select_video_file_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedVideoUri != null) MetallicGold else ElectricViolet
                            )
                        ) {
                            Icon(Icons.Default.VideoFile, contentDescription = null, tint = DarkCharcoal)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (selectedVideoUri != null) "Change Video File" else "Browse & Upload Movie File",
                                color = DarkCharcoal,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Step 2: Video Metadata & Format Analysis
            item {
                GlassmorphicCard(
                    glowColor = MetallicGoldGlow,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Movie, contentDescription = null, tint = MetallicGold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "AUTOMATIC VIDEO ANALYSIS METADATA",
                                style = MaterialTheme.typography.bodySmall,
                                color = MetallicGold,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        MetadataRow("File Name:", movieName)
                        MetadataRow("Duration:", detectedDuration)
                        MetadataRow("Format / Codec:", "H.264 / AAC Audio")
                        MetadataRow("Resolution:", detectedResolution)
                        MetadataRow("Audio Stream:", "Stereo 48kHz (Active)")
                    }
                }
            }

            // Step 3: Import Processing Mode
            item {
                GlassmorphicCard(
                    glowColor = ElectricVioletGlow,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "IMPORT & RECAP MODE SELECTOR",
                            style = MaterialTheme.typography.bodySmall,
                            color = ElectricViolet,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = importMode == "auto_highlights",
                                onClick = { importMode = "auto_highlights" },
                                label = { Text("AI Auto Highlights", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ElectricViolet)
                            )
                            FilterChip(
                                selected = importMode == "full_movie",
                                onClick = { importMode = "full_movie" },
                                label = { Text("Full Movie Analysis", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ElectricViolet)
                            )
                            FilterChip(
                                selected = importMode == "trailer_cut",
                                onClick = { importMode = "trailer_cut" },
                                label = { Text("60s Fast Trailer Cut", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ElectricViolet)
                            )
                        }
                    }
                }
            }

            // Step 4: Auto-Detected Keyframe Scenes
            item {
                Text(
                    text = "Auto-Detected Scene Keyframes (${simulatedKeyframes.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            itemsIndexed(simulatedKeyframes) { index, scene ->
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(ElectricViolet),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "S${index + 1}",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = scene.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Timestamp: ${scene.timestampRange} • Type: ${scene.type}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            // Step 5: Proceed Action
            item {
                Button(
                    onClick = {
                        viewModel.generateRecap(
                            movieTitle = movieName.replace(".mp4", "").replace("_", " "),
                            genre = "Action Thriller",
                            targetDurationSeconds = 180,
                            userPrompt = "Imported movie file processing. Generate Burmese and English scene recap."
                        )
                        onProceedToScript()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("process_movie_recap_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Import & Generate AI Movie Recap Script", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun MetadataRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = TextMuted)
        Text(text = value, style = MaterialTheme.typography.bodySmall, color = TextPrimary, fontWeight = FontWeight.Bold)
    }
}
