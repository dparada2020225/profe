package com.denil.profe.data.remote

import com.denil.profe.data.curriculum.CourseUnit
import com.denil.profe.data.curriculum.CurriculumParser
import com.denil.profe.data.curriculum.Exercise
import com.denil.profe.data.curriculum.ExerciseType
import com.denil.profe.domain.Cefr
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

data class WritingMistake(val original: String, val fix: String, val why: String)

data class WritingFeedback(
    val corrected: String,
    val better: String,
    val score: Int,
    val level: String,
    val feedback: String,
    val mistakes: List<WritingMistake>
)

data class TranslationVerdict(val correct: Boolean, val feedback: String, val better: String)

/** Funciones del "profe" que usan Groq. */
@Singleton
class AiTeacher @Inject constructor(private val groq: GroqClient) {

    suspend fun isAvailable(): Boolean = groq.isConfigured()

    suspend fun askAboutLesson(level: Cefr, unit: CourseUnit, question: String): String =
        groq.chat(
            TutorPrompts.lessonQuestionSystem(level, unit.title, unit.subtitle),
            listOf(ChatTurn("user", question)),
            temperature = 0.4
        )

    suspend fun checkTranslation(spanish: String, expected: List<String>, attempt: String): TranslationVerdict {
        val prompt = "Spanish: $spanish\nModel answers: ${expected.joinToString(" | ")}\nLearner: $attempt"
        val text = groq.chat(TutorPrompts.translationCheckSystem(), listOf(ChatTurn("user", prompt)), temperature = 0.1, maxTokens = 250, jsonMode = true)
        val o = JSONObject(extractJson(text))
        return TranslationVerdict(o.optBoolean("correct"), o.optString("feedback"), o.optString("better"))
    }

    suspend fun extraExercises(level: Cefr, unit: CourseUnit): List<Exercise> {
        val examples = unit.exercises.take(4).joinToString("\n") { it.prompt }
        val text = groq.chat(
            TutorPrompts.extraExercisesSystem(level, unit.title, unit.subtitle),
            listOf(ChatTurn("user", "Existing examples (do not repeat):\n$examples")),
            temperature = 0.8, maxTokens = 1800, jsonMode = true
        )
        val arr = JSONObject(extractJson(text)).optJSONArray("exercises") ?: return emptyList()
        return (0 until arr.length()).mapNotNull { i ->
            runCatching { CurriculumParser.parseExercise(arr.getJSONObject(i)) }.getOrNull()
        }.filter { e ->
            when (e.type) {
                ExerciseType.CHOICE -> e.options.size >= 2 && e.answerIndex in e.options.indices
                ExerciseType.FILL -> e.prompt.contains("___") && e.answers.isNotEmpty()
                ExerciseType.TRANSLATE -> e.answers.isNotEmpty()
                else -> false
            }
        }
    }

    suspend fun reviewWriting(level: Cefr, task: String, text: String): WritingFeedback {
        val raw = groq.chat(
            TutorPrompts.writingSystem(level),
            listOf(ChatTurn("user", "Task: $task\n\nText:\n$text")),
            temperature = 0.3, maxTokens = 1500, jsonMode = true
        )
        val o = JSONObject(extractJson(raw))
        val arr = o.optJSONArray("mistakes")
        val mistakes = if (arr == null) emptyList() else (0 until arr.length()).map {
            val m = arr.getJSONObject(it)
            WritingMistake(m.optString("original"), m.optString("fix"), m.optString("why"))
        }
        return WritingFeedback(
            corrected = o.optString("corrected"),
            better = o.optString("better"),
            score = o.optInt("score", 0).coerceIn(0, 100),
            level = o.optString("level"),
            feedback = o.optString("feedback"),
            mistakes = mistakes
        )
    }

    /** Algunos modelos envuelven el JSON en ```; nos quedamos con el objeto. */
    private fun extractJson(text: String): String {
        val start = text.indexOf('{')
        val end = text.lastIndexOf('}')
        if (start < 0 || end <= start) throw NetworkException("La IA no devolvió una respuesta válida. Intenta de nuevo.")
        return text.substring(start, end + 1)
    }
}
