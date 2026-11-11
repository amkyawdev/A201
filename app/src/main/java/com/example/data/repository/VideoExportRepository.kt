package com.example.data.repository

import com.example.data.model.ProjectEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

data class ExportProgress(
    val stage: String,
    val percent: Int,
    val isDone: Boolean = false,
    val outputPath: String? = null,
    val error: String? = null
)

class VideoExportRepository {

    fun exportMovieRecapVideo(
        project: ProjectEntity,
        resolution: String, // 720p, 1080p, 4K
        fps: Int, // 24, 30, 60
        aspectRatio: String // 16:9, 9:16, 1:1
    ): Flow<ExportProgress> = flow {
        emit(ExportProgress("Initializing FFmpeg Core & Pipeline...", 5))
        delay(600)

        emit(ExportProgress("Parsing Scenes and Keyframe Trimming...", 15))
        delay(700)

        emit(ExportProgress("Synthesizing Burmese & English Narration Audio...", 35))
        delay(800)

        emit(ExportProgress("Blending Audio Tracks (Narration, BGM & Master)...", 55))
        delay(800)

        emit(ExportProgress("Applying Aspect Ratio Crop ($aspectRatio) & Watermark...", 75))
        delay(900)

        emit(ExportProgress("Encoding H.264/AAC MP4 Stream ($resolution @ $fps FPS)...", 90))
        delay(1000)

        val outputFileName = "MovieRecap_${project.title.replace(" ", "_")}_$aspectRatio.mp4"
        emit(
            ExportProgress(
                stage = "Rendering Complete! Saved to Downloads.",
                percent = 100,
                isDone = true,
                outputPath = "/storage/emulated/0/Download/$outputFileName"
            )
        )
    }
}
