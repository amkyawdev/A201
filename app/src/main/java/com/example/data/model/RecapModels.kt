package com.example.data.model

data class RecapScene(
    val id: String,
    val sceneIndex: Int,
    val timeStartSeconds: Int,
    val timeEndSeconds: Int,
    val title: String,
    val shotDescription: String,
    val narrationBurmese: String,
    val narrationEnglish: String,
    val audioCue: String,
    val isSelected: Boolean = true
)

data class MovieRecapScript(
    val movieTitle: String,
    val logline: String,
    val genre: String,
    val totalDurationSeconds: Int,
    val scenes: List<RecapScene>
)
