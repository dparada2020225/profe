package com.denil.profe.domain

import java.text.Normalizer

/**
 * Tarjetas de completar: el frente contiene huecos escritos como {{respuesta}}.
 * Se admiten varias respuestas válidas separadas por |: {{isn't|is not}}.
 */
object Cloze {
    private val BLANK = Regex("\\{\\{(.+?)\\}\\}")

    /** Un trozo del texto: literal o hueco. */
    data class Segment(val text: String, val isBlank: Boolean, val alternatives: List<String> = emptyList()) {
        /** Respuesta que se muestra al revelar (la primera alternativa). */
        val display: String get() = alternatives.firstOrNull() ?: text
    }

    fun hasBlanks(text: String): Boolean = BLANK.containsMatchIn(text)

    fun parse(text: String): List<Segment> {
        val out = ArrayList<Segment>()
        var last = 0
        for (m in BLANK.findAll(text)) {
            if (m.range.first > last) out.add(Segment(text.substring(last, m.range.first), false))
            val alts = m.groupValues[1].split('|').map { it.trim() }.filter { it.isNotEmpty() }
            out.add(Segment(m.value, true, alts))
            last = m.range.last + 1
        }
        if (last < text.length) out.add(Segment(text.substring(last), false))
        return out
    }

    /** Lista de huecos en orden. */
    fun blanks(text: String): List<Segment> = parse(text).filter { it.isBlank }

    /** Texto con los huecos sustituidos por rayas (para el frente). */
    fun masked(text: String, placeholder: String = "______"): String =
        parse(text).joinToString("") { if (it.isBlank) placeholder else it.text }

    /** Texto completo con la primera alternativa de cada hueco (para el reverso y para pronunciar). */
    fun filled(text: String): String =
        parse(text).joinToString("") { if (it.isBlank) it.display else it.text }

    /**
     * Comprueba una respuesta escrita. Si hay varios huecos, las respuestas se separan por coma.
     * La comparación ignora mayúsculas, acentos, espacios sobrantes y apóstrofos tipográficos.
     */
    fun check(text: String, userAnswer: String): CheckResult {
        val blanks = blanks(text)
        if (blanks.isEmpty()) return CheckResult(emptyList(), true)
        val given = splitAnswers(userAnswer, blanks.size)
        val results = blanks.mapIndexed { i, blank ->
            val g = given.getOrNull(i).orEmpty()
            val ok = blank.alternatives.any { normalize(it) == normalize(g) }
            BlankResult(expected = blank.display, given = g, correct = ok)
        }
        return CheckResult(results, results.all { it.correct })
    }

    private fun splitAnswers(answer: String, count: Int): List<String> {
        if (count <= 1) return listOf(answer.trim())
        val parts = answer.split(',', ';', '/').map { it.trim() }
        return if (parts.size >= count) parts else parts + List(count - parts.size) { "" }
    }

    fun normalize(s: String): String {
        val decomposed = Normalizer.normalize(s.trim().lowercase(), Normalizer.Form.NFD)
        return decomposed
            .replace(Regex("\\p{M}+"), "")
            .replace('’', '\'')
            .replace('‘', '\'')
            .replace(Regex("\\s+"), " ")
            .trimEnd('.', '!', '?')
    }

    data class BlankResult(val expected: String, val given: String, val correct: Boolean)
    data class CheckResult(val blanks: List<BlankResult>, val allCorrect: Boolean)
}
