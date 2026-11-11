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

data class VoiceOption(
    val id: String,
    val name: String,
    val languageName: String,
    val gender: String,
    val description: String,
    val provider: String
)

object VoicePresets {
    val ALL_VOICES = listOf(
        VoiceOption("my_aung_cinematic", "Aung (အောင်)", "Burmese (မြန်မာ)", "Male", "Deep Cinematic Trailer Voice", "ElevenLabs / Gemini"),
        VoiceOption("my_nilar_story", "Nilar (နီလာ)", "Burmese (မြန်မာ)", "Female", "Warm Storyteller Voice", "ElevenLabs / Gemini"),
        VoiceOption("my_thiri_soft", "Thiri (သီရိ)", "Burmese (မြန်မာ)", "Female", "Soft Documentary Narration", "ElevenLabs / Gemini"),
        VoiceOption("my_kyaw_action", "Kyaw (ကျော်)", "Burmese (မြန်မာ)", "Male", "High Energy Action Trailer", "ElevenLabs / Gemini"),
        VoiceOption("en_adam_cinematic", "Adam", "English (US)", "Male", "Cinematic Movie Trailer Voice", "ElevenLabs"),
        VoiceOption("en_rachel_warm", "Rachel", "English (US)", "Female", "Engaging Storyteller & Recap Voice", "ElevenLabs"),
        VoiceOption("en_antoni_dramatic", "Antoni", "English (US)", "Male", "Dramatic Suspense Voice", "ElevenLabs")
    )
}
