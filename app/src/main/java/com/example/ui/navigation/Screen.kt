package com.example.ui.navigation

sealed class Screen(val route: String, val title: String) {
    data object Dashboard : Screen("dashboard", "Dashboard")
    data object Upload : Screen("upload", "Upload Movie")
    data object Generator : Screen("generator", "AI Script")
    data object Editor : Screen("editor", "Timeline Editor")
    data object Transcription : Screen("transcription", "Audio & Speech")
    data object Subtitles : Screen("subtitles", "SRT Subtitles")
    data object Export : Screen("export", "Export Video")
    data object Settings : Screen("settings", "API Settings")
    data object Docs : Screen("docs", "Documentation")
}
