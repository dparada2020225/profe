package com.denil.profe.domain

import java.text.Normalizer

/**
 * Comparación tolerante de respuestas escritas o habladas en inglés.
 * Ignora mayúsculas, acentos, puntuación, espacios extra y la diferencia entre
 * contracciones y formas completas ("I'm" = "I am", "don't" = "do not").
 */
object AnswerChecker {

    private val contractions: List<Pair<Regex, String>> = listOf(
        "won't" to "will not",
        "can't" to "can not",
        "cannot" to "can not",
        "shan't" to "shall not",
        "ain't" to "is not",
        "let's" to "let us",
        "i'm" to "i am",
        "n't" to " not",
        "'re" to " are",
        "'ve" to " have",
        "'ll" to " will",
        "'d" to " would",
        "it's" to "it is",
        "that's" to "that is",
        "there's" to "there is",
        "here's" to "here is",
        "what's" to "what is",
        "where's" to "where is",
        "who's" to "who is",
        "how's" to "how is",
        "he's" to "he is",
        "she's" to "she is"
    ).map { (from, to) -> Regex(Regex.escape(from)) to to }

    /** Normaliza para comparar: minúsculas, sin acentos, sin puntuación, contracciones expandidas. */
    fun normalize(text: String): String {
        var s = text.trim().lowercase()
            .replace('’', '\'')
            .replace('‘', '\'')
            .replace('´', '\'')
            .replace('`', '\'')
        s = Normalizer.normalize(s, Normalizer.Form.NFD).replace(Regex("\\p{Mn}+"), "")
        for ((regex, replacement) in contractions) s = regex.replace(s, replacement)
        // El 's posesivo se conserva como "s" para no confundir (my dad's → my dads)
        s = s.replace("'", "")
        s = s.replace(Regex("[^a-z0-9 ]"), " ")
        return s.replace(Regex("\\s+"), " ").trim()
    }

    fun matches(input: String, accepted: Collection<String>): Boolean {
        val n = normalize(input)
        if (n.isEmpty()) return false
        return accepted.any { normalize(it) == n }
    }

    /** Palabras normalizadas (para dictado y pronunciación). */
    fun tokens(text: String): List<String> = normalize(text).split(' ').filter { it.isNotEmpty() }

    data class WordResult(val word: String, val ok: Boolean)

    data class Comparison(
        /** Palabras de la frase objetivo marcando si se reconocieron/escribieron. */
        val target: List<WordResult>,
        /** Palabras de más que dijo/escribió el usuario. */
        val extra: List<String>,
        /** 0..100 */
        val score: Int
    ) {
        val perfect: Boolean get() = score == 100 && extra.isEmpty()
    }

    /**
     * Compara una frase objetivo con lo que el usuario escribió o dijo usando la
     * subsecuencia común más larga (LCS) de palabras.
     */
    fun compare(target: String, attempt: String): Comparison {
        val t = tokens(target)
        val a = tokens(attempt)
        if (t.isEmpty()) return Comparison(emptyList(), a, if (a.isEmpty()) 100 else 0)
        val dp = Array(t.size + 1) { IntArray(a.size + 1) }
        for (i in t.indices.reversed()) {
            for (j in a.indices.reversed()) {
                dp[i][j] = if (t[i] == a[j]) dp[i + 1][j + 1] + 1 else maxOf(dp[i + 1][j], dp[i][j + 1])
            }
        }
        val okTarget = BooleanArray(t.size)
        val usedAttempt = BooleanArray(a.size)
        var i = 0
        var j = 0
        while (i < t.size && j < a.size) {
            when {
                t[i] == a[j] -> { okTarget[i] = true; usedAttempt[j] = true; i++; j++ }
                dp[i + 1][j] >= dp[i][j + 1] -> i++
                else -> j++
            }
        }
        val matched = okTarget.count { it }
        val extra = a.filterIndexed { idx, _ -> !usedAttempt[idx] }
        // Penaliza palabras de más, pero sin bajar de 0.
        val raw = (matched * 100.0 / t.size) - extra.size * (50.0 / t.size)
        val score = raw.coerceIn(0.0, 100.0).toInt()
        return Comparison(t.mapIndexed { idx, w -> WordResult(w, okTarget[idx]) }, extra, score)
    }

    /** Separa una oración en fichas para el ejercicio de ordenar (conserva mayúsculas y puntuación). */
    fun orderTokens(sentence: String): List<String> =
        sentence.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
}
