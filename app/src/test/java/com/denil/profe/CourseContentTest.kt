package com.denil.profe

import com.denil.profe.domain.Cloze
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Valida el contenido del curso incluido (assets/courses/gic2.json) sin depender de Android:
 * se lee el archivo directamente y se comprueba con un parser JSON mínimo basado en expresiones regulares.
 */
class CourseContentTest {

    private val file: File by lazy {
        listOf("src/main/assets/courses/gic2.json", "app/src/main/assets/courses/gic2.json")
            .map { File(it) }
            .first { it.exists() }
    }

    @Test
    fun `el curso tiene 14 unidades y apendices`() {
        val text = file.readText()
        val deckNames = Regex("\"name\": \"(U\\d+ · [^\"]+|Apéndice [^\"]+)\"").findAll(text).map { it.groupValues[1] }.toList()
        assertEquals(17, deckNames.size)
        for (i in 1..14) assertTrue("Falta la unidad $i", deckNames.any { it.startsWith("U$i ·") })
    }

    @Test
    fun `todas las tarjetas de completar tienen huecos validos`() {
        val text = file.readText()
        // Cada tarjeta cloze: "type": "CLOZE" seguido de su "front"
        val cloze = Regex("\"type\": \"CLOZE\",\\s*\"section\": \"[^\"]*\",\\s*\"front\": \"((?:[^\"\\\\]|\\\\.)*)\"")
            .findAll(text).map { it.groupValues[1] }.toList()
        assertTrue(cloze.size > 600)
        for (front in cloze) {
            assertTrue("Sin huecos: $front", Cloze.hasBlanks(front))
            val blanks = Cloze.blanks(front)
            assertTrue(blanks.all { it.alternatives.isNotEmpty() })
            // La respuesta principal debe ser válida para sí misma
            val answer = blanks.joinToString(", ") { it.display }
            assertTrue("No se valida: $front", Cloze.check(front, answer).allCorrect)
        }
    }

    @Test
    fun `cubre todas las secciones del libro`() {
        val text = file.readText()
        val sections = Regex("\"section\": \"(\\d+\\.\\d+)\"").findAll(text).map { it.groupValues[1] }.toSet()
        val expected = mapOf(1 to 16, 2 to 9, 3 to 7, 4 to 11, 5 to 12, 6 to 7, 7 to 8, 8 to 11, 9 to 11, 10 to 11, 11 to 6, 12 to 8, 13 to 5, 14 to 9)
        for ((unit, count) in expected) {
            for (n in 1..count) assertTrue("Falta la sección $unit.$n", "$unit.$n" in sections)
        }
    }
}
