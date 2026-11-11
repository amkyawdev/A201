package com.example.data.repository

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class TtsRepository(private val context: Context) {

    private val voiceMapping = mapOf(
        "my_aung_cinematic" to "pNInz6obpgnuMvscL7qy", // Adam (Multilingual)
        "my_nilar_story" to "21m00Tcm4TbcDqjt88lp",   // Rachel (Multilingual)
        "my_thiri_soft" to "AZnzlk1XYSVEbeis4S1P",    // Nicole (Multilingual)
        "my_kyaw_action" to "ErXw9S1q38Yv6ZvN2q5o",   // Antoni (Multilingual)
        "en_adam_cinematic" to "pNInz6obpgnuMvscL7qy",
        "en_rachel_warm" to "21m00Tcm4TbcDqjt88lp",
        "en_antoni_dramatic" to "ErXw9S1q38Yv6ZvN2q5o"
    )

    suspend fun generateSpeech(
        text: String,
        voiceId: String,
        apiKey: String?,
        provider: String = "elevenlabs"
    ): Uri? = withContext(Dispatchers.IO) {
        if (apiKey.isNullOrBlank()) return@withContext null
        
        val actualVoiceId = voiceMapping[voiceId] ?: voiceId

        return@withContext try {
            when (provider) {
                "elevenlabs" -> fetchFromElevenLabs(text, actualVoiceId, apiKey)
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun fetchFromElevenLabs(text: String, voiceId: String, apiKey: String): Uri? {
        val endpoint = "https://api.elevenlabs.io/v1/text-to-speech/$voiceId"
        val url = URL(endpoint)
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("xi-api-key", apiKey)
        conn.setRequestProperty("Content-Type", "application/json")
        conn.doOutput = true

        val jsonBody = JSONObject().apply {
            put("text", text)
            put("model_id", "eleven_multilingual_v2") // Supports Burmese
            put("voice_settings", JSONObject().apply {
                put("stability", 0.5)
                put("similarity_boost", 0.75)
            })
        }

        conn.outputStream.use { os ->
            os.write(jsonBody.toString().toByteArray())
        }

        if (conn.responseCode == 200) {
            val file = File(context.cacheDir, "tts_${System.currentTimeMillis()}.mp3")
            conn.inputStream.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            return Uri.fromFile(file)
        }
        return null
    }
}
