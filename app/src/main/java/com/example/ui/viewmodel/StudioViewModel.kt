package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.SettingsDataStore
import com.example.data.model.MovieRecapScript
import com.example.data.model.ProjectEntity
import com.example.data.model.RecapScene
import com.example.data.model.SubtitleItem
import com.example.data.repository.AiRecapRepository
import com.example.data.repository.AudioTranscriptionRepository
import com.example.data.repository.ExportProgress
import com.example.data.repository.ProjectRepository
import com.example.data.repository.VideoExportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

sealed interface GeneratorUiState {
    data object Idle : GeneratorUiState
    data object Loading : GeneratorUiState
    data class Success(val script: MovieRecapScript) : GeneratorUiState
    data class Error(val message: String) : GeneratorUiState
}

class StudioViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val projectRepo = ProjectRepository(db.projectDao())
    private val aiRepo = AiRecapRepository()
    private val transcriptionRepo = AudioTranscriptionRepository()
    private val exportRepo = VideoExportRepository()
    private val settingsDataStore = SettingsDataStore(application)

    val allProjects: StateFlow<List<ProjectEntity>> = projectRepo.allProjects
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val geminiApiKey = settingsDataStore.geminiApiKey
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")
    val elevenLabsApiKey = settingsDataStore.elevenLabsApiKey
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")
    val assemblyAiApiKey = settingsDataStore.assemblyAiApiKey
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    private val _generatorState = MutableStateFlow<GeneratorUiState>(GeneratorUiState.Idle)
    val generatorState: StateFlow<GeneratorUiState> = _generatorState.asStateFlow()

    private val _activeProject = MutableStateFlow<ProjectEntity?>(null)
    val activeProject: StateFlow<ProjectEntity?> = _activeProject.asStateFlow()

    private val _subtitles = MutableStateFlow<List<SubtitleItem>>(emptyList())
    val subtitles: StateFlow<List<SubtitleItem>> = _subtitles.asStateFlow()

    private val _exportProgress = MutableStateFlow<ExportProgress?>(null)
    val exportProgress: StateFlow<ExportProgress?> = _exportProgress.asStateFlow()

    private val _isRecordingAudio = MutableStateFlow(false)
    val isRecordingAudio: StateFlow<Boolean> = _isRecordingAudio.asStateFlow()

    private val _recordingDurationSec = MutableStateFlow(0)
    val recordingDurationSec: StateFlow<Int> = _recordingDurationSec.asStateFlow()

    fun generateRecap(
        movieTitle: String,
        genre: String,
        targetDurationSeconds: Int,
        userPrompt: String
    ) {
        viewModelScope.launch {
            _generatorState.value = GeneratorUiState.Loading
            try {
                val apiKey = geminiApiKey.value
                val script = aiRepo.generateRecapScript(
                    movieTitle = movieTitle,
                    genre = genre,
                    targetDurationSeconds = targetDurationSeconds,
                    userPrompt = userPrompt,
                    apiKey = apiKey
                )
                _generatorState.value = GeneratorUiState.Success(script)

                // Auto-create initial project in Room
                val newProject = ProjectEntity(
                    id = UUID.randomUUID().toString(),
                    title = script.movieTitle,
                    genre = script.genre,
                    targetDurationSeconds = script.totalDurationSeconds,
                    aspectRatio = "9:16",
                    language = "dual",
                    videoUri = null,
                    videoDurationSeconds = script.totalDurationSeconds.toLong(),
                    scenesJson = serializeScenes(script.scenes),
                    subtitlesJson = "[]"
                )
                projectRepo.saveProject(newProject)
                _activeProject.value = newProject
            } catch (e: Exception) {
                _generatorState.value = GeneratorUiState.Error(e.message ?: "Failed to generate script")
            }
        }
    }

    fun selectProject(project: ProjectEntity) {
        _activeProject.value = project
        _subtitles.value = parseSubtitles(project.subtitlesJson)
    }

    fun saveApiSettings(gemini: String, elevenLabs: String, assemblyAi: String) {
        viewModelScope.launch {
            settingsDataStore.saveApiKeys(gemini, elevenLabs, assemblyAi)
        }
    }

    fun updateProjectSettings(
        aspectRatio: String,
        narrationVol: Float,
        bgmVol: Float,
        watermark: String
    ) {
        val current = _activeProject.value ?: return
        val updated = current.copy(
            aspectRatio = aspectRatio,
            narrationVolume = narrationVol,
            bgmVolume = bgmVol,
            watermarkText = watermark,
            updatedAt = System.currentTimeMillis()
        )
        _activeProject.value = updated
        viewModelScope.launch {
            projectRepo.updateProject(updated)
        }
    }

    fun startAudioRecording() {
        _isRecordingAudio.value = true
        _recordingDurationSec.value = 0
        viewModelScope.launch {
            while (_isRecordingAudio.value) {
                kotlinx.coroutines.delay(1000)
                _recordingDurationSec.value += 1
            }
        }
    }

    fun stopAudioRecordingAndTranscribe() {
        _isRecordingAudio.value = false
        viewModelScope.launch {
            val generatedSubtitles = transcriptionRepo.transcribeAudioTrack(null)
            _subtitles.value = generatedSubtitles
            _activeProject.value?.let { proj ->
                val updated = proj.copy(subtitlesJson = serializeSubtitles(generatedSubtitles))
                _activeProject.value = updated
                projectRepo.updateProject(updated)
            }
        }
    }

    fun addSubtitle(burmeseText: String, englishText: String, startMs: Long, endMs: Long) {
        val newItem = SubtitleItem(
            id = UUID.randomUUID().toString(),
            startMs = startMs,
            endMs = endMs,
            textBurmese = burmeseText,
            textEnglish = englishText
        )
        val updatedList = _subtitles.value + newItem
        _subtitles.value = updatedList
        _activeProject.value?.let { proj ->
            val updated = proj.copy(subtitlesJson = serializeSubtitles(updatedList))
            _activeProject.value = updated
            viewModelScope.launch { projectRepo.updateProject(updated) }
        }
    }

    fun deleteSubtitle(id: String) {
        val updatedList = _subtitles.value.filter { it.id != id }
        _subtitles.value = updatedList
        _activeProject.value?.let { proj ->
            val updated = proj.copy(subtitlesJson = serializeSubtitles(updatedList))
            _activeProject.value = updated
            viewModelScope.launch { projectRepo.updateProject(updated) }
        }
    }

    fun exportSrtFormat(languageMode: String): String {
        return transcriptionRepo.exportToSrt(_subtitles.value, languageMode)
    }

    fun exportVttFormat(languageMode: String): String {
        return transcriptionRepo.exportToVtt(_subtitles.value, languageMode)
    }

    fun startVideoExport(resolution: String, fps: Int, aspectRatio: String) {
        val proj = _activeProject.value ?: return
        viewModelScope.launch {
            exportRepo.exportMovieRecapVideo(proj, resolution, fps, aspectRatio).collect { progress ->
                _exportProgress.value = progress
                if (progress.isDone) {
                    val completed = proj.copy(status = "Completed")
                    _activeProject.value = completed
                    projectRepo.updateProject(completed)
                }
            }
        }
    }

    fun deleteProject(id: String) {
        viewModelScope.launch {
            projectRepo.deleteProject(id)
            if (_activeProject.value?.id == id) {
                _activeProject.value = null
            }
        }
    }

    private fun serializeScenes(scenes: List<RecapScene>): String {
        val array = JSONArray()
        scenes.forEach { s ->
            array.put(JSONObject().apply {
                put("id", s.id)
                put("sceneIndex", s.sceneIndex)
                put("timeStartSeconds", s.timeStartSeconds)
                put("timeEndSeconds", s.timeEndSeconds)
                put("title", s.title)
                put("shotDescription", s.shotDescription)
                put("narrationBurmese", s.narrationBurmese)
                put("narrationEnglish", s.narrationEnglish)
                put("audioCue", s.audioCue)
            })
        }
        return array.toString()
    }

    fun deserializeScenes(jsonStr: String): List<RecapScene> {
        if (jsonStr.isBlank() || jsonStr == "[]") return emptyList()
        val list = mutableListOf<RecapScene>()
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    RecapScene(
                        id = obj.optString("id", UUID.randomUUID().toString()),
                        sceneIndex = obj.optInt("sceneIndex", i + 1),
                        timeStartSeconds = obj.optInt("timeStartSeconds", i * 15),
                        timeEndSeconds = obj.optInt("timeEndSeconds", (i + 1) * 15),
                        title = obj.optString("title", "Scene ${i + 1}"),
                        shotDescription = obj.optString("shotDescription", ""),
                        narrationBurmese = obj.optString("narrationBurmese", ""),
                        narrationEnglish = obj.optString("narrationEnglish", ""),
                        audioCue = obj.optString("audioCue", "Cinematic Sound")
                    )
                )
            }
        } catch (e: Exception) {
            // Return empty list on parse error
        }
        return list
    }

    private fun serializeSubtitles(subtitles: List<SubtitleItem>): String {
        val array = JSONArray()
        subtitles.forEach { sub ->
            array.put(JSONObject().apply {
                put("id", sub.id)
                put("startMs", sub.startMs)
                put("endMs", sub.endMs)
                put("textBurmese", sub.textBurmese)
                put("textEnglish", sub.textEnglish)
                put("speaker", sub.speaker)
            })
        }
        return array.toString()
    }

    private fun parseSubtitles(jsonStr: String): List<SubtitleItem> {
        if (jsonStr.isBlank() || jsonStr == "[]") return emptyList()
        val list = mutableListOf<SubtitleItem>()
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    SubtitleItem(
                        id = obj.optString("id", UUID.randomUUID().toString()),
                        startMs = obj.optLong("startMs", 0L),
                        endMs = obj.optLong("endMs", 3000L),
                        textBurmese = obj.optString("textBurmese", ""),
                        textEnglish = obj.optString("textEnglish", ""),
                        speaker = obj.optString("speaker", "Narrator")
                    )
                )
            }
        } catch (e: Exception) {
            // Return empty on parse error
        }
        return list
    }
}
