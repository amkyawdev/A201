package com.example.data.model

data class SubtitleItem(
    val id: String,
    val startMs: Long,
    val endMs: Long,
    val textBurmese: String,
    val textEnglish: String,
    val speaker: String = "Narrator"
) {
    fun formattedStartTime(): String = formatTimestamp(startMs)
    fun formattedEndTime(): String = formatTimestamp(endMs)

    private fun formatTimestamp(ms: Long): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        val millis = ms % 1000
        return String.format("%02d:%02d.%03d", minutes, seconds, millis)
    }
}
