package com.denil.profe.data.curriculum

import com.denil.profe.domain.Cefr
import org.json.JSONArray
import org.json.JSONObject

data class Section(val heading: String, val body: String)
data class Example(val en: String, val es: String)
data class VocabItem(val en: String, val es: String, val example: String)

enum class ExerciseType { CHOICE, FILL, TRANSLATE, ORDER, LISTEN, SPEAK }

data class Exercise(
    val type: ExerciseType,
    /** Enunciado: pregunta (choice), oración con ___ (fill), texto en español (translate/order/listen/speak). */
    val prompt: String,
    val options: List<String> = emptyList(),
    val answerIndex: Int = -1,
    /** Respuestas aceptadas (fill/translate) u oración objetivo (order/listen/speak). */
    val answers: List<String> = emptyList(),
    val explain: String = "",
    val hint: String = ""
) {
    val target: String get() = answers.firstOrNull().orEmpty()
}

data class CourseUnit(
    val id: String,
    val level: Cefr,
    val number: Int,
    val title: String,
    val subtitle: String,
    val goal: String,
    val sections: List<Section>,
    val examples: List<Example>,
    val vocab: List<VocabItem>,
    val exercises: List<Exercise>
)

data class CourseLevel(
    val level: Cefr,
    val title: String,
    val description: String,
    val units: List<CourseUnit>
)

data class Curriculum(val levels: List<CourseLevel>) {
    val units: List<CourseUnit> by lazy { levels.flatMap { it.units } }
    fun unit(id: String): CourseUnit? = units.firstOrNull { it.id == id }
    fun level(cefr: Cefr): CourseLevel? = levels.firstOrNull { it.level == cefr }

    /** Unidad siguiente en el orden del curso (o null si es la última). */
    fun next(unitId: String): CourseUnit? {
        val i = units.indexOfFirst { it.id == unitId }
        return if (i >= 0) units.getOrNull(i + 1) else null
    }
}

data class PlacementQuestion(val level: Cefr, val prompt: String, val options: List<String>, val answer: Int)

/** Parser del JSON generado por tools/curriculum/build.py. */
object CurriculumParser {

    fun parseCurriculum(text: String): Curriculum {
        val root = JSONObject(text)
        val levelsArr = root.getJSONArray("levels")
        val levels = (0 until levelsArr.length()).map { li ->
            val l = levelsArr.getJSONObject(li)
            val cefr = Cefr.parse(l.getString("id")) ?: error("Nivel inválido: ${l.getString("id")}")
            val unitsArr = l.getJSONArray("units")
            CourseLevel(
                level = cefr,
                title = l.getString("title"),
                description = l.optString("description"),
                units = (0 until unitsArr.length()).map { ui -> parseUnit(unitsArr.getJSONObject(ui), cefr, ui + 1) }
            )
        }
        return Curriculum(levels)
    }

    private fun parseUnit(o: JSONObject, level: Cefr, number: Int): CourseUnit = CourseUnit(
        id = o.getString("id"),
        level = level,
        number = number,
        title = o.getString("title"),
        subtitle = o.optString("subtitle"),
        goal = o.optString("goal"),
        sections = o.getJSONArray("sections").objects().map { Section(it.getString("heading"), it.getString("body")) },
        examples = o.getJSONArray("examples").objects().map { Example(it.getString("en"), it.getString("es")) },
        vocab = o.getJSONArray("vocab").objects().map { VocabItem(it.getString("en"), it.getString("es"), it.optString("ex")) },
        exercises = o.getJSONArray("exercises").objects().mapNotNull { parseExercise(it) }
    )

    fun parseExercise(o: JSONObject): Exercise? {
        val type = when (o.getString("type")) {
            "choice" -> ExerciseType.CHOICE
            "fill" -> ExerciseType.FILL
            "translate" -> ExerciseType.TRANSLATE
            "order" -> ExerciseType.ORDER
            "listen" -> ExerciseType.LISTEN
            "speak" -> ExerciseType.SPEAK
            else -> return null
        }
        return Exercise(
            type = type,
            prompt = o.optString("prompt"),
            options = o.optJSONArray("options")?.strings().orEmpty(),
            answerIndex = o.optInt("answer", -1),
            answers = o.optJSONArray("answers")?.strings().orEmpty(),
            explain = o.optString("explain"),
            hint = o.optString("hint")
        )
    }

    fun parsePlacement(text: String): List<PlacementQuestion> {
        val root = JSONObject(text)
        val levels = root.getJSONArray("levels")
        val out = ArrayList<PlacementQuestion>()
        for (i in 0 until levels.length()) {
            val l = levels.getJSONObject(i)
            val cefr = Cefr.parse(l.getString("id")) ?: continue
            l.getJSONArray("questions").objects().forEach { q ->
                out += PlacementQuestion(cefr, q.getString("prompt"), q.getJSONArray("options").strings(), q.getInt("answer"))
            }
        }
        return out
    }

    private fun JSONArray.objects(): List<JSONObject> = (0 until length()).map { getJSONObject(it) }
    private fun JSONArray.strings(): List<String> = (0 until length()).map { getString(it) }
}
