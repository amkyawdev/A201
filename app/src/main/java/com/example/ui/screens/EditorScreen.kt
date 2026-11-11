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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Tab
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.VoicePresets
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.HeaderBar
import com.example.ui.components.MultiTrackTimelineView
import com.example.ui.theme.CardCharcoal
import com.example.ui.theme.DarkCharcoal
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.ElectricVioletGlow
import com.example.ui.theme.MetallicGold
import com.example.ui.theme.MetallicGoldGlow
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.StudioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    viewModel: StudioViewModel,
    onNavigateToExport: () -> Unit,
    onNavigateToSubtitles: () -> Unit
) {
    val projectState by viewModel.activeProject.collectAsStateWithLifecycle()
    val project = projectState ?: remember { viewModel.ensureActiveProject() }
    val subtitles by viewModel.subtitles.collectAsStateWithLifecycle()
    val exportProgress by viewModel.exportProgress.collectAsStateWithLifecycle()

    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }

    var isPlaying by remember { mutableStateOf(false) }
    var currentPositionSec by remember { mutableIntStateOf(0) }

    var selectedAspectRatio by remember { mutableStateOf(project.aspectRatio) }
    var narrationVolume by remember { mutableFloatStateOf(project.narrationVolume) }
    var bgmVolume by remember { mutableFloatStateOf(project.bgmVolume) }
    var watermarkText by remember { mutableStateOf(project.watermarkText) }

    var selectedVoiceId by remember { mutableStateOf(project.selectedVoiceId) }
    var speechRate by remember { mutableFloatStateOf(project.speechRate) }
    var speechPitch by remember { mutableFloatStateOf(project.speechPitch) }
    var customVoiceUri by remember { mutableStateOf(project.customVoiceUri) }

    var isMuted by remember { mutableStateOf(false) }
    var showControls by remember { mutableStateOf(true) }
    
    var voiceExpanded by remember { mutableStateOf(false) }

    var newBurmese by remember { mutableStateOf("") }
    var newEnglish by remember { mutableStateOf("") }
    var startMsText by remember { mutableStateOf("0") }
    var endMsText by remember { mutableStateOf("4000") }

    var selectedResolution by remember { mutableStateOf("1080p") }
    var selectedFps by remember { mutableIntStateOf(30) }

    val videoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.updateVideoUri(uri)
            val updatedName = uri.lastPathSegment ?: "Uploaded_Movie.mp4"
            Toast.makeText(context, "Movie Video Uploaded: $updatedName", Toast.LENGTH_SHORT).show()
        }
    }

    val audioPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            customVoiceUri = uri.toString()
            viewModel.updateProjectSettings(
                selectedAspectRatio, narrationVolume, bgmVolume, watermarkText,
                selectedVoiceId, speechRate, speechPitch, uri.toString()
            )
            Toast.makeText(context, "Custom Voiceover Loaded!", Toast.LENGTH_SHORT).show()
        }
    }

    val srtPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val content = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                if (!content.isNullOrBlank()) {
                    viewModel.importSubtitlesFromSrtContent(content)
                    Toast.makeText(context, "Subtitles Imported!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to read subtitle file", Toast.LENGTH_SHORT).show()
            }
        }
    }

    androidx.compose.runtime.LaunchedEffect(viewModel.exoPlayer) {
        while (true) {
            currentPositionSec = (viewModel.exoPlayer.currentPosition / 1000).toInt()
            isPlaying = viewModel.exoPlayer.isPlaying
            kotlinx.coroutines.delay(500)
        }
    }

    val tabTitles = listOf("🎬 Timeline & Video", "🎙️ Voice & Audio", "💬 Captions & SRT", "🚀 Render & Export")
    val aspectRatios = listOf("9:16", "16:9", "1:1")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkCharcoal)
    ) {
        HeaderBar(
            title = project.title,
            subtitle = "Genre: ${project.genre} • Aspect: ${selectedAspectRatio} • Duration: ${project.targetDurationSeconds}s"
        )

        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = CardCharcoal,
            contentColor = MetallicGold,
            edgePadding = 12.dp
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) MetallicGold else TextMuted
                        )
                    }
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    item {
                        GlassmorphicCard(
                            glowColor = ElectricVioletGlow,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                val aspectMultiplier = when (selectedAspectRatio) {
                                    "16:9" -> 16f / 9f
                                    "1:1" -> 1f
                                    else -> 9f / 16f
                                }

                                val videoUri by viewModel.videoUri.collectAsStateWithLifecycle()
                                
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(if (selectedAspectRatio == "9:16") 0.65f else 1f)
                                        .aspectRatio(aspectMultiplier)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(CardCharcoal)
                                        .border(1.dp, MetallicGold, RoundedCornerShape(12.dp))
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) { showControls = !showControls },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (videoUri != null) {
                                        AndroidView(
                                            factory = { ctx ->
                                                androidx.media3.ui.PlayerView(ctx).apply {
                                                    player = viewModel.exoPlayer
                                                    useController = false
                                                    layoutParams = android.view.ViewGroup.LayoutParams(
                                                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                                                        android.view.ViewGroup.LayoutParams.MATCH_PARENT
                                                    )
                                                }
                                            },
                                            modifier = Modifier.fillMaxSize()
                                        )

                                        // Custom Overlay Controls
                                        androidx.compose.animation.AnimatedVisibility(
                                            visible = showControls,
                                            enter = androidx.compose.animation.fadeIn(),
                                            exit = androidx.compose.animation.fadeOut()
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(Color.Black.copy(alpha = 0.4f))
                                            ) {
                                                // Center Play/Pause
                                                IconButton(
                                                    onClick = { 
                                                        isPlaying = !isPlaying 
                                                        if (isPlaying) viewModel.exoPlayer.play() else viewModel.exoPlayer.pause()
                                                    },
                                                    modifier = Modifier
                                                        .size(64.dp)
                                                        .align(Alignment.Center)
                                                        .background(ElectricViolet.copy(alpha = 0.8f), CircleShape)
                                                ) {
                                                    Icon(
                                                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(40.dp)
                                                    )
                                                }

                                                // Bottom Controls (Seek & Volume)
                                                Column(
                                                    modifier = Modifier
                                                        .align(Alignment.BottomCenter)
                                                        .fillMaxWidth()
                                                        .padding(12.dp)
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Text(
                                                            text = "${currentPositionSec}s",
                                                            color = Color.White,
                                                            fontSize = 12.sp,
                                                            modifier = Modifier.width(40.dp)
                                                        )
                                                        Slider(
                                                            value = currentPositionSec.toFloat(),
                                                            onValueChange = { 
                                                                currentPositionSec = it.toInt()
                                                                viewModel.exoPlayer.seekTo(it.toLong() * 1000)
                                                            },
                                                            valueRange = 0f..project.targetDurationSeconds.toFloat().coerceAtLeast(1f),
                                                            modifier = Modifier.weight(1f),
                                                            colors = SliderDefaults.colors(
                                                                thumbColor = MetallicGold,
                                                                activeTrackColor = ElectricViolet
                                                            )
                                                        )
                                                        Text(
                                                            text = "${project.targetDurationSeconds}s",
                                                            color = Color.White,
                                                            fontSize = 12.sp,
                                                            modifier = Modifier.width(40.dp)
                                                        )
                                                        
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        
                                                        IconButton(
                                                            onClick = { 
                                                                isMuted = !isMuted
                                                                viewModel.exoPlayer.volume = if (isMuted) 0f else 1f
                                                            },
                                                            modifier = Modifier.size(32.dp)
                                                        ) {
                                                            Icon(
                                                                imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                                                                contentDescription = null,
                                                                tint = Color.White
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                imageVector = Icons.Default.Movie,
                                                contentDescription = null,
                                                tint = ElectricViolet,
                                                modifier = Modifier.size(48.dp)
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "No Video Loaded",
                                                style = MaterialTheme.typography.titleMedium,
                                                color = TextPrimary,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "Upload a movie to start editing",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = TextMuted
                                            )
                                        }
                                    }

                                    if (watermarkText.isNotBlank()) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
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

                                Button(
                                    onClick = { videoPicker.launch("video/*") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = MetallicGold)
                                ) {
                                    Icon(Icons.Default.CloudUpload, contentDescription = null, tint = DarkCharcoal)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Upload / Replace Movie Video File (.mp4 / .mkv)", color = DarkCharcoal, fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    IconButton(
                                        onClick = { 
                                            isPlaying = !isPlaying 
                                            if (isPlaying) viewModel.exoPlayer.play() else viewModel.exoPlayer.pause()
                                        },
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
                                            text = "Timecode: ${currentPositionSec}s / ${project.targetDurationSeconds}s",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary
                                        )
                                        Slider(
                                            value = currentPositionSec.toFloat(),
                                            onValueChange = { 
                                                currentPositionSec = it.toInt()
                                                viewModel.exoPlayer.seekTo(it.toLong() * 1000)
                                            },
                                            valueRange = 0f..project.targetDurationSeconds.toFloat(),
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

                    item {
                        MultiTrackTimelineView(
                            totalDurationSeconds = project.targetDurationSeconds,
                            currentPositionSeconds = currentPositionSec,
                            onSeek = { currentPositionSec = it }
                        )
                    }

                    item {
                        GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                            Column {
                                Text(text = "Aspect Ratio Format:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    aspectRatios.forEach { ar ->
                                        FilterChip(
                                            selected = selectedAspectRatio == ar,
                                            onClick = {
                                                selectedAspectRatio = ar
                                                viewModel.updateProjectSettings(
                                                    ar, narrationVolume, bgmVolume, watermarkText,
                                                    selectedVoiceId, speechRate, speechPitch, customVoiceUri
                                                )
                                            },
                                            label = { Text(ar, fontSize = 12.sp) },
                                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ElectricViolet)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    item {
                        GlassmorphicCard(
                            glowColor = MetallicGoldGlow,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = MetallicGold)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "BURMESE & ENGLISH TTS VOICE SELECTOR",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MetallicGold,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                ExposedDropdownMenuBox(
                                    expanded = voiceExpanded,
                                    onExpandedChange = { voiceExpanded = !voiceExpanded }
                                ) {
                                    val selectedVoice = VoicePresets.ALL_VOICES.find { it.id == selectedVoiceId }
                                        ?: VoicePresets.ALL_VOICES.first()

                                    OutlinedTextField(
                                        value = "${selectedVoice.name} (${selectedVoice.languageName})",
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = voiceExpanded) },
                                        modifier = Modifier
                                            .menuAnchor()
                                            .fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = ElectricViolet,
                                            unfocusedBorderColor = CardCharcoal,
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary
                                        )
                                    )

                                    ExposedDropdownMenu(
                                        expanded = voiceExpanded,
                                        onDismissRequest = { voiceExpanded = false },
                                        modifier = Modifier.background(CardCharcoal)
                                    ) {
                                        VoicePresets.ALL_VOICES.forEach { voice ->
                                            DropdownMenuItem(
                                                text = {
                                                    Column {
                                                        Text(text = "${voice.name} • ${voice.gender}", color = TextPrimary, fontWeight = FontWeight.Bold)
                                                        Text(text = "${voice.languageName} • ${voice.description}", color = TextSecondary, fontSize = 11.sp)
                                                    }
                                                },
                                                onClick = {
                                                    selectedVoiceId = voice.id
                                                    voiceExpanded = false
                                                    viewModel.updateProjectSettings(
                                                        selectedAspectRatio, narrationVolume, bgmVolume, watermarkText,
                                                        voice.id, speechRate, speechPitch, customVoiceUri
                                                    )
                                                }
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Speed: ${String.format("%.1f", speechRate)}x",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary
                                        )
                                        Slider(
                                            value = speechRate,
                                            onValueChange = {
                                                speechRate = it
                                                viewModel.updateProjectSettings(
                                                    selectedAspectRatio, narrationVolume, bgmVolume, watermarkText,
                                                    selectedVoiceId, it, speechPitch, customVoiceUri
                                                )
                                            },
                                            valueRange = 0.5f..2.0f,
                                            colors = SliderDefaults.colors(thumbColor = MetallicGold)
                                        )
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Pitch: ${String.format("%.1f", speechPitch)}x",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary
                                        )
                                        Slider(
                                            value = speechPitch,
                                            onValueChange = {
                                                speechPitch = it
                                                viewModel.updateProjectSettings(
                                                    selectedAspectRatio, narrationVolume, bgmVolume, watermarkText,
                                                    selectedVoiceId, speechRate, it, customVoiceUri
                                                )
                                            },
                                            valueRange = 0.5f..1.5f,
                                            colors = SliderDefaults.colors(thumbColor = ElectricViolet)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedButton(
                                    onClick = { audioPicker.launch("audio/*") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MetallicGold)
                                ) {
                                    Icon(Icons.Default.Upload, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (customVoiceUri != null) "Change External Voice File" else "Upload Custom Voice File (.mp3 / .wav)")
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Button(
                                    onClick = { 
                                        val scenes = viewModel.deserializeScenes(project.scenesJson)
                                        val fullText = scenes.joinToString(". ") { it.narrationBurmese }
                                        viewModel.generateVoiceover(fullText)
                                        Toast.makeText(context, "Generating AI Voiceover with ElevenLabs...", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                                    enabled = !viewModel.elevenLabsApiKey.value.isNullOrBlank()
                                ) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Generate AI Voiceover (ElevenLabs)")
                                }
                                
                                if (viewModel.elevenLabsApiKey.value.isNullOrBlank()) {
                                    Text(
                                        text = "⚠️ ElevenLabs API Key missing in Settings",
                                        color = Color.Red,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        GlassmorphicCard(
                            glowColor = ElectricVioletGlow,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(text = "AUDIO VOLUME MIXER", style = MaterialTheme.typography.bodySmall, color = ElectricViolet, fontWeight = FontWeight.Bold)

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(text = "Narration Volume: ${(narrationVolume * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                Slider(
                                    value = narrationVolume,
                                    onValueChange = {
                                        narrationVolume = it
                                        viewModel.updateProjectSettings(
                                            selectedAspectRatio, it, bgmVolume, watermarkText,
                                            selectedVoiceId, speechRate, speechPitch, customVoiceUri
                                        )
                                    },
                                    valueRange = 0f..1.5f,
                                    colors = SliderDefaults.colors(thumbColor = ElectricViolet)
                                )

                                Text(text = "BGM Volume: ${(bgmVolume * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                Slider(
                                    value = bgmVolume,
                                    onValueChange = {
                                        bgmVolume = it
                                        viewModel.updateProjectSettings(
                                            selectedAspectRatio, narrationVolume, it, watermarkText,
                                            selectedVoiceId, speechRate, speechPitch, customVoiceUri
                                        )
                                    },
                                    valueRange = 0f..1.0f,
                                    colors = SliderDefaults.colors(thumbColor = MetallicGold)
                                )
                            }
                        }
                    }
                }

                2 -> {
                    item {
                        GlassmorphicCard(glowColor = MetallicGoldGlow, modifier = Modifier.fillMaxWidth()) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Button(
                                        onClick = { srtPicker.launch("*/*") },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = MetallicGold)
                                    ) {
                                        Icon(Icons.Default.FileUpload, contentDescription = null, tint = DarkCharcoal, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Upload .SRT File", color = DarkCharcoal, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(text = "Global Timestamp Shift:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    OutlinedButton(onClick = { viewModel.shiftSubtitleTimestamps(-500L) }, modifier = Modifier.weight(1f)) {
                                        Text("-500ms", fontSize = 11.sp)
                                    }
                                    OutlinedButton(onClick = { viewModel.shiftSubtitleTimestamps(500L) }, modifier = Modifier.weight(1f)) {
                                        Text("+500ms", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                            Column {
                                Text(text = "ADD SUBTITLE CUE", style = MaterialTheme.typography.bodySmall, color = ElectricViolet, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = newBurmese,
                                    onValueChange = { newBurmese = it },
                                    label = { Text("Burmese Text (မြန်မာ)", color = TextSecondary) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                OutlinedTextField(
                                    value = newEnglish,
                                    onValueChange = { newEnglish = it },
                                    label = { Text("English Translation", color = TextSecondary) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )

                                Spacer(modifier = Modifier.height(8.dp))

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
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Add Caption Cue")
                                }
                            }
                        }
                    }

                    items(subtitles) { sub ->
                        GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "[${sub.formattedStartTime()} ➔ ${sub.formattedEndTime()}]", color = MetallicGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    if (sub.textBurmese.isNotBlank()) Text(text = sub.textBurmese, color = TextPrimary, fontWeight = FontWeight.Bold)
                                    if (sub.textEnglish.isNotBlank()) Text(text = sub.textEnglish, color = TextSecondary, fontSize = 12.sp)
                                }
                                IconButton(onClick = { viewModel.deleteSubtitle(sub.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextMuted)
                                }
                            }
                        }
                    }
                }

                3 -> {
                    item {
                        GlassmorphicCard(glowColor = ElectricVioletGlow, modifier = Modifier.fillMaxWidth()) {
                            Column {
                                Text(text = "EXPORT QUALITY CONFIGURATION", style = MaterialTheme.typography.bodySmall, color = ElectricViolet, fontWeight = FontWeight.Bold)

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf("720p", "1080p", "4K").forEach { res ->
                                        FilterChip(
                                            selected = selectedResolution == res,
                                            onClick = { selectedResolution = res },
                                            label = { Text(res, fontSize = 12.sp) },
                                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ElectricViolet)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = watermarkText,
                                    onValueChange = {
                                        watermarkText = it
                                        viewModel.updateProjectSettings(
                                            selectedAspectRatio, narrationVolume, bgmVolume, it,
                                            selectedVoiceId, speechRate, speechPitch, customVoiceUri
                                        )
                                    },
                                    label = { Text("Watermark Text", color = TextSecondary) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = {
                                        viewModel.startVideoExport(selectedResolution, selectedFps, selectedAspectRatio)
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet)
                                ) {
                                    Icon(Icons.Default.Download, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Start FFmpeg MP4 Render Pipeline", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    if (exportProgress != null) {
                        item {
                            val progress = exportProgress!!
                            GlassmorphicCard(
                                glowColor = if (progress.isDone) SuccessGreen.copy(alpha = 0.3f) else MetallicGoldGlow,
                                borderColor = if (progress.isDone) SuccessGreen else MetallicGold,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column {
                                    Text(text = if (progress.isDone) "RENDER COMPLETE!" else "RENDERING...", color = TextPrimary, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    LinearProgressIndicator(
                                        progress = { progress.percent / 100f },
                                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                                        color = if (progress.isDone) SuccessGreen else ElectricViolet
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(text = progress.stage, color = TextSecondary, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
