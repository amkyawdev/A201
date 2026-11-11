package com.example.data.repository

import com.example.data.model.SubtitleItem
import kotlinx.coroutines.delay
import java.util.UUID

class AudioTranscriptionRepository {

    suspend fun transcribeAudioTrack(audioUri: String?): List<SubtitleItem> {
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
