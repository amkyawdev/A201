package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: String,
    val title: String,
    val genre: String,
    val targetDurationSeconds: Int,
    val aspectRatio: String, // "16:9", "9:16", "1:1"
    val language: String, // "dual", "my", "en"
    val videoUri: String?,
    val videoDurationSeconds: Long,
    val scenesJson: String,
    val subtitlesJson: String,
    val narrationVolume: Float = 1.0f,
    val bgmVolume: Float = 0.3f,
    val masterVolume: Float = 1.0f,
    val watermarkText: String = "Movie Recap Studio",
    val selectedVoiceId: String = "my_aung_cinematic",
    val speechRate: Float = 1.0f,
    val speechPitch: Float = 1.0f,
    val customVoiceUri: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val status: String = "Draft" // "Draft", "Processing", "Completed"
)
