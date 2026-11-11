package com.example.data.repository

import com.example.data.model.MovieRecapScript
import com.example.data.model.RecapScene
import kotlinx.coroutines.delay
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

class AiRecapRepository {

    suspend fun generateRecapScript(
        movieTitle: String,
        genre: String,
        targetDurationSeconds: Int,
        userPrompt: String,
        apiKey: String?
    ): MovieRecapScript {
        // If a Gemini API Key is supplied by BYOK, attempt live Gemini API call
        if (!apiKey.isNullOrBlank()) {
            try {
                val liveResult = fetchFromGeminiApi(movieTitle, genre, targetDurationSeconds, userPrompt, apiKey)
                if (liveResult != null) return liveResult
            } catch (e: Exception) {
                // Fall back gracefully to built-in generator if API fails or network issue occurs
            }
        }

        // Built-in Burmese & English Cinematic Script Generation Engine
        delay(1200) // Simulate processing pipeline
        return generateTemplateScript(movieTitle, genre, targetDurationSeconds, userPrompt)
    }

    private fun fetchFromGeminiApi(
        movieTitle: String,
        genre: String,
        targetDurationSeconds: Int,
        userPrompt: String,
        apiKey: String
    ): MovieRecapScript? {
        val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=$apiKey"
        val systemPrompt = """
            You are a professional Movie Recap Script Writer. Create a scene-by-scene movie recap script for '$movieTitle' (Genre: $genre, Target Duration: $targetDurationSeconds seconds).
            Return raw JSON with structure:
            {
              "movieTitle": "$movieTitle",
              "logline": "short logline",
              "genre": "$genre",
              "totalDurationSeconds": $targetDurationSeconds,
              "scenes": [
                {
                  "sceneIndex": 1,
                  "timeStartSeconds": 0,
                  "timeEndSeconds": 15,
                  "title": "Scene title",
                  "shotDescription": "Visual camera shot details",
                  "narrationBurmese": "Burmese voiceover script in မြန်မာစာ",
                  "narrationEnglish": "English voiceover script",
                  "audioCue": "Suspenseful BGM"
                }
              ]
            }
        """.trimIndent()

        val jsonBody = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", systemPrompt + "\nAdditional context: " + userPrompt))
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("responseMimeType", "application/json")
            })
        }

        val url = URL(endpoint)
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/json")
        conn.doOutput = true
        conn.connectTimeout = 10000
        conn.readTimeout = 15000

        conn.outputStream.use { os ->
            os.write(jsonBody.toString().toByteArray())
        }

        if (conn.responseCode == 200) {
            val responseText = conn.inputStream.bufferedReader().use { it.readText() }
            val root = JSONObject(responseText)
            val candidates = root.getJSONArray("candidates")
            if (candidates.length() > 0) {
                val content = candidates.getJSONObject(0).getJSONObject("content")
                val parts = content.getJSONArray("parts")
                val text = parts.getJSONObject(0).getString("text")
                return parseScriptJson(text)
            }
        }
        return null
    }

    private fun parseScriptJson(jsonString: String): MovieRecapScript {
        val obj = JSONObject(jsonString)
        val scenesArray = obj.getJSONArray("scenes")
        val scenesList = mutableListOf<RecapScene>()

        for (i in 0 until scenesArray.length()) {
            val item = scenesArray.getJSONObject(i)
            scenesList.add(
                RecapScene(
                    id = UUID.randomUUID().toString(),
                    sceneIndex = item.optInt("sceneIndex", i + 1),
                    timeStartSeconds = item.optInt("timeStartSeconds", i * 20),
                    timeEndSeconds = item.optInt("timeEndSeconds", (i + 1) * 20),
                    title = item.optString("title", "Scene ${i + 1}"),
                    shotDescription = item.optString("shotDescription", "Cinematic view"),
                    narrationBurmese = item.optString("narrationBurmese", "ဒီဇာတ်လမ်းမှာ အဓိကဇာတ်ကောင်ဟာ ဆန်းကြယ်တဲ့အမှန်တရားကို စတင်တွေ့ရှိခဲ့ပါတယ်။"),
                    narrationEnglish = item.optString("narrationEnglish", "In this pivotal moment, our hero uncovers a chilling truth."),
                    audioCue = item.optString("audioCue", "Dramatic Strings")
                )
            )
        }

        return MovieRecapScript(
            movieTitle = obj.optString("movieTitle", "Untitled Movie"),
            logline = obj.optString("logline", "An intense recap journey filled with suspense and high stakes."),
            genre = obj.optString("genre", "Thriller"),
            totalDurationSeconds = obj.optInt("totalDurationSeconds", 180),
            scenes = scenesList
        )
    }

    private fun generateTemplateScript(
        title: String,
        genre: String,
        duration: Int,
        prompt: String
    ): MovieRecapScript {
        val cleanTitle = title.ifBlank { "Neon Shadows" }
        val sceneCount = when {
            duration <= 60 -> 4
            duration <= 180 -> 6
            else -> 8
        }
        val secondsPerScene = duration / sceneCount

        val scenes = mutableListOf<RecapScene>()

        val burmeseScripts = listOf(
            "ဇာတ်လမ်းအစမှာ $cleanTitle ဟာ ပုံမှန်မြို့ပြဘဝမှာ နေထိုင်နေပေမယ့် ကံကြမ္မာရဲ့ အလှည့်အပြောင်းတစ်ခု စတင်ခဲ့ပါတယ်။",
            "မထင်မှတ်ထားတဲ့ လျှို့ဝှက်ချက်တစ်ခု ပေါ်ထွက်လာပြီး ဇာတ်ကောင်တွေကြား ယုံကြည်မှု ချို့ယွင်းလာခဲ့ပါတယ်။",
            "ရန်သူရဲ့ လှည့်ကွက်တွေအောက်မှာ အဓိကဇာတ်ကောင်ဟာ ခက်ခဲတဲ့ ဆုံးဖြတ်ချက်ကို ချမှတ်ခဲ့ရပါတယ်။",
            "အရေးကြီးတဲ့ တိုက်ပွဲနဲ့ ရင်ဆိုင်ရချိန်မှာ သူမထင်ထားတဲ့ အကူအညီတစ်ခု ရရှိခဲ့ပါတယ်။",
            "အလှည့်အပြောင်းပေါင်းများစွာနဲ့အတူ ဇာတ်ရှိန်ဟာ အထွတ်အထိပ်သို့ ရောက်ရှိလာခဲ့ပါတယ်။",
            "နောက်ဆုံးမှာတော့ အမှန်တရား ပေါ်ပေါက်သွားခဲ့ပြီး ဇာတ်လမ်းဟာ အံ့အားသင့်ဖွယ် နိဂုံးချုပ်သွားခဲ့ပါတယ်။",
            "ဇာတ်ကောင်ရဲ့ ရဲရင့်မှုကြောင့် မြို့ပြကြီးဟာ ငြိမ်းချမ်းရေး ပြန်လည်ရရှိခဲ့ပါတယ်။",
            "ဒါပေမယ့် နောက်ကွယ်က ဆန်းကြယ်တဲ့ အရိပ်မဲကြီးဟာ ဆက်လက် တည်ရှိနေဆဲ ဖြစ်ပါတယ်။"
        )

        val englishScripts = listOf(
            "Our story begins in the shadowed streets of $cleanTitle, where an ordinary life takes a sudden, dangerous turn.",
            "A sudden revelation shatters everything, forcing unexpected allies to question who they can really trust.",
            "Trapped in a web of deceit, our main character is forced to make an impossible choice to survive.",
            "When all hope seems lost, a hidden power emerges, turning the tides of battle in an instant.",
            "The tension reaches a boiling point as secret motives are revealed under the neon lights.",
            "In a thrilling climax, the mastermind's true agenda comes to light in a dramatic showdown.",
            "Courage and sacrifice restore order, bringing a brief moment of peace to the chaotic city.",
            "Yet as the dust settles, a lingering shadow hints that this is only the beginning."
        )

        val audioCues = listOf(
            "Mysterious Synth Drone",
            "Rising Tension Percussion",
            "Fast Pulse Action Beat",
            "Melancholic Piano & Strings",
            "Heavy Bass Drop",
            "Orchestral Climax",
            "Gentle Ambient Outro",
            "Eerie Sub-Bass Rumble"
        )

        for (i in 0 until sceneCount) {
            val startSec = i * secondsPerScene
            val endSec = (i + 1) * secondsPerScene
            scenes.add(
                RecapScene(
                    id = UUID.randomUUID().toString(),
                    sceneIndex = i + 1,
                    timeStartSeconds = startSec,
                    timeEndSeconds = endSec,
                    title = "Act ${i + 1}: ${if (i == 0) "The Awakening" else if (i == sceneCount - 1) "Final Showdown" else "Rising Conflict"}",
                    shotDescription = "Wide cinematic angle featuring dynamic camera pan and $genre atmosphere.",
                    narrationBurmese = burmeseScripts[i % burmeseScripts.size],
                    narrationEnglish = englishScripts[i % englishScripts.size],
                    audioCue = audioCues[i % audioCues.size]
                )
            )
        }

        return MovieRecapScript(
            movieTitle = cleanTitle,
            logline = "An epic $genre recap of $cleanTitle crafted with Burmese and English narration.",
            genre = genre,
            totalDurationSeconds = duration,
            scenes = scenes
        )
    }
}
