package com.denil.profe

import com.denil.profe.data.curriculum.CurriculumParser
import com.denil.profe.data.curriculum.ExerciseType
import com.denil.profe.domain.AnswerChecker
import com.denil.profe.domain.Cefr
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** Valida el contenido del curso generado (assets/curriculum) con el mismo parser que usa la app. */
class CurriculumContentTest {

    private fun asset(name: String): String =
        listOf("src/main/assets/curriculum/$name", "app/src/main/assets/curriculum/$name")
            .map { File(it) }.first { it.exists() }.readText()

    private val curriculum by lazy { CurriculumParser.parseCurriculum(asset("curriculum.json")) }

    @Test
    fun `hay seis niveles en orden`() {
        assertEquals(Cefr.entries.toList(), curriculum.levels.map { it.level })
        assertTrue(curriculum.units.size >= 50)
    }

    @Test
    fun `cada unidad tiene contenido completo`() {
        for (u in curriculum.units) {
            assertTrue(u.id, u.sections.isNotEmpty())
            assertTrue(u.id, u.examples.size >= 5)
            assertTrue(u.id, u.vocab.size >= 8)
            assertTrue(u.id, u.exercises.size >= 10)
        }
    }

    @Test
    fun `ejercicios bien formados`() {
        for (u in curriculum.units) for (e in u.exercises) {
            when (e.type) {
                ExerciseType.CHOICE -> {
                    assertTrue("${u.id}: ${e.prompt}", e.options.size >= 2)
                    assertTrue("${u.id}: ${e.prompt}", e.answerIndex in e.options.indices)
                }
                ExerciseType.FILL -> {
                    assertTrue("${u.id}: ${e.prompt}", e.prompt.contains("___"))
                    assertTrue("${u.id}: ${e.prompt}", e.answers.isNotEmpty())
                }
                else -> assertTrue("${u.id}: ${e.prompt}", e.answers.isNotEmpty())
            }
            // La respuesta modelo siempre debe aceptarse a sí misma.
            if (e.type == ExerciseType.FILL || e.type == ExerciseType.TRANSLATE) {
                e.answers.forEach { a -> assertTrue("${u.id}: $a", AnswerChecker.matches(a, e.answers)) }
            }
            if (e.type == ExerciseType.ORDER) {
                val tokens = AnswerChecker.orderTokens(e.target)
                assertTrue("${u.id}: ${e.target}", AnswerChecker.matches(tokens.joinToString(" "), e.answers))
            }
        }
    }

    @Test
    fun `ids unicos`() {
        val ids = curriculum.units.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun `banco de ubicacion completo`() {
        val q = CurriculumParser.parsePlacement(asset("placement.json"))
        for (level in Cefr.entries) {
            val n = q.count { it.level == level }
            assertTrue("$level tiene $n", n >= 5)
        }
        q.forEach { assertTrue(it.prompt, it.answer in it.options.indices) }
    }
}
