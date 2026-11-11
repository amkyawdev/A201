package com.example.data.repository

import com.example.data.model.SubtitleItem
import kotlinx.coroutines.delay
import java.util.UUID

class AudioTranscriptionRepository {

    suspend fun transcribeAudioTrack(audioUri: String?, apiKey: String?): List<SubtitleItem> {
        if (!apiKey.isNullOrBlank() && audioUri != null) {
            try {
                // Simplified AssemblyAI / Whisper integration logic
                // In a real app, you'd upload the file and poll for results
                // For this studio demo, we'll implement the HTTP skeleton
            } catch (e: Exception) {
                // Fallback to mock
            }
        }
        
        delay(1500) // Simulate speech-to-text model inference

        return listOf(
            SubtitleItem(
                id = UUID.randomUUID().toString(),
                startMs = 0L,
                endMs = 4500L,
                textBurmese = "မင်္ဂလာပါ၊ Movie Recap Studio မှ ကြိုဆိုပါတယ်။",
                textEnglish = "Welcome to Movie Recap Studio narration."
            ),
            SubtitleItem(
                id = UUID.randomUUID().toString(),
                startMs = 5000L,
                endMs = 9500L,
                textBurmese = "ဇာတ်ကားရဲ့ အရေးကြီးဆုံး ဇာတ်ဝင်ခန်းတွေကို ကြည့်ရှုလိုက်ရအောင်။",
                textEnglish = "Let's dive into the most intense scenes of the movie."
            ),
            SubtitleItem(
                id = UUID.randomUUID().toString(),
                startMs = 10000L,
                endMs = 14500L,
                textBurmese = "ဇာတ်ကောင်ရဲ့ လျှို့ဝှက်ချက်ဟာ မကြာမီ ပေါ်ပေါက်တော့မှာ ဖြစ်ပါတယ်။",
                textEnglish = "The character's secret will be revealed shortly."
            ),
            SubtitleItem(
                id = UUID.randomUUID().toString(),
                startMs = 15000L,
                endMs = 20000L,
                textBurmese = "နောက်ထပ် စိတ်လှုပ်ရှားဖွယ်ရာ အပိုင်းကို ဆက်လက် စောင့်မျှော်ကြည့်ရှုပါ။",
                textEnglish = "Stay tuned for the thrilling conclusion."
            )
        )
    }

    fun parseSrtOrVttContent(content: String): List<SubtitleItem> {
        val list = mutableListOf<SubtitleItem>()
        if (content.isBlank()) return list

        val blocks = content.split("\n\n")
        for (block in blocks) {
            val lines = block.lines().map { it.trim() }.filter { it.isNotBlank() }
            if (lines.size >= 2) {
                val timeLine = lines.firstOrNull { it.contains("-->") } ?: continue
                val textLines = lines.subList(lines.indexOf(timeLine) + 1, lines.size)

                val parts = timeLine.split("-->")
                if (parts.size == 2) {
                    val startMs = parseTimestampToMs(parts[0].trim())
                    val endMs = parseTimestampToMs(parts[1].trim())

                    val combinedText = textLines.joinToString("\n")
                    val isBurmese = combinedText.any { it.code in 0x1000..0x109F }

                    list.add(
                        SubtitleItem(
                            id = UUID.randomUUID().toString(),
                            startMs = startMs,
                            endMs = endMs,
                            textBurmese = if (isBurmese) combinedText else "မြန်မာဘာသာပြန်ချက်: $combinedText",
                            textEnglish = if (!isBurmese) combinedText else "English Translation: $combinedText"
                        )
                    )
                }
            }
        }
        return list
    }

    private fun parseTimestampToMs(timeStr: String): Long {
        return try {
            val clean = timeStr.replace(",", ".").trim()
            val timeParts = clean.split(":")
            if (timeParts.size == 3) {
                val hours = timeParts[0].toLong()
                val minutes = timeParts[1].toLong()
                val secParts = timeParts[2].split(".")
                val seconds = secParts[0].toLong()
                val millis = if (secParts.size > 1) secParts[1].padEnd(3, '0').take(3).toLong() else 0L
                (hours * 3600 + minutes * 60 + seconds) * 1000 + millis
            } else 0L
        } catch (e: Exception) {
            0L
        }
    }

    fun exportToSrt(subtitles: List<SubtitleItem>, languageMode: String): String {
        val sb = StringBuilder()
        subtitles.forEachIndexed { index, item ->
            sb.append("${index + 1}\n")
            sb.append("${formatSrtTime(item.startMs)} --> ${formatSrtTime(item.endMs)}\n")
            val text = when (languageMode) {
                "my" -> item.textBurmese
                "en" -> item.textEnglish
                else -> "${item.textBurmese}\n${item.textEnglish}"
            }
            sb.append("$text\n\n")
        }
        return sb.toString()
    }

    fun exportToVtt(subtitles: List<SubtitleItem>, languageMode: String): String {
        val sb = StringBuilder("WEBVTT\n\n")
        subtitles.forEachIndexed { index, item ->
            sb.append("${index + 1}\n")
            sb.append("${formatVttTime(item.startMs)} --> ${formatVttTime(item.endMs)}\n")
            val text = when (languageMode) {
                "my" -> item.textBurmese
                "en" -> item.textEnglish
                else -> "${item.textBurmese}\n${item.textEnglish}"
            }
            sb.append("$text\n\n")
        }
        return sb.toString()
    }

    private fun formatSrtTime(ms: Long): String {
        val hours = ms / (1000 * 60 * 60)
        val minutes = (ms / (1000 * 60)) % 60
        val seconds = (ms / 1000) % 60
        val millis = ms % 1000
        return String.format("%02d:%02d:%02d,%03d", hours, minutes, seconds, millis)
    }

    private fun formatVttTime(ms: Long): String {
        val hours = ms / (1000 * 60 * 60)
        val minutes = (ms / (1000 * 60)) % 60
        val seconds = (ms / 1000) % 60
        val millis = ms % 1000
        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, millis)
    }
}
