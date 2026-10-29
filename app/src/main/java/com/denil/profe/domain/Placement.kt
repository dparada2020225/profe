package com.denil.profe.domain

/** Niveles del Marco Común Europeo. */
enum class Cefr(val label: String) {
    A1("Principiante"), A2("Básico"), B1("Intermedio"), B2("Intermedio alto"), C1("Avanzado"), C2("Maestría");

    companion object {
        fun parse(id: String?): Cefr? = entries.firstOrNull { it.name.equals(id, ignoreCase = true) }
    }
}

/**
 * Examen de ubicación adaptativo (lógica pura).
 *
 * Empieza en A2 con bloques de [perBlock] preguntas. Si aciertas al menos [passMark] del bloque,
 * sube de nivel; si no, termina. Si fallas el primer bloque (A2) se prueba A1.
 * El resultado es el nivel más alto aprobado; si no apruebas ninguno, A1.
 */
class PlacementEngine(
    private val perBlock: Int = 5,
    private val passMark: Int = 4,
    private val start: Cefr = Cefr.A2
) {
    data class BlockResult(val level: Cefr, val correct: Int, val total: Int) {
        val passed: Boolean get() = total > 0 && correct * 5 >= total * 4 // ≥ 80 %
    }

    private val results = mutableListOf<BlockResult>()
    var current: Cefr? = start
        private set
    private var answeredInBlock = 0
    private var correctInBlock = 0

    val blocks: List<BlockResult> get() = results.toList()
    val finished: Boolean get() = current == null
    val questionsPerBlock: Int get() = perBlock
    val answeredInCurrentBlock: Int get() = answeredInBlock

    /** Registra una respuesta del bloque actual. Devuelve true si el bloque terminó. */
    fun answer(correct: Boolean): Boolean {
        val level = current ?: return true
        answeredInBlock++
        if (correct) correctInBlock++
        // Corte anticipado: ya no puede aprobar o ya aprobó con certeza.
        val remaining = perBlock - answeredInBlock
        val cannotPass = correctInBlock + remaining < passMark
        val surePass = correctInBlock >= passMark
        if (answeredInBlock < perBlock && !cannotPass && !surePass) return false
        val block = BlockResult(level, correctInBlock, answeredInBlock)
        results += block
        answeredInBlock = 0
        correctInBlock = 0
        current = next(block)
        return true
    }

    private fun next(block: BlockResult): Cefr? {
        val passedNow = block.correct >= passMark
        val tested = results.map { it.level }.toSet()
        return if (passedNow) {
            val up = Cefr.entries.getOrNull(block.level.ordinal + 1)
            if (up == null || up in tested) null else up
        } else {
            // Si falló el primer bloque y hay un nivel más bajo sin probar, baja una vez.
            val down = Cefr.entries.getOrNull(block.level.ordinal - 1)
            if (results.size == 1 && down != null && down !in tested) down else null
        }
    }

    /** Nivel recomendado: el siguiente al más alto aprobado (lo que te toca estudiar). */
    fun recommendedLevel(): Cefr {
        val highestPassed = results.filter { it.correct >= passMark }.maxByOrNull { it.level.ordinal }?.level
            ?: return Cefr.A1
        return Cefr.entries.getOrNull(highestPassed.ordinal + 1) ?: Cefr.C2
    }

    /** Nivel demostrado (el más alto aprobado), o null si ninguno. */
    fun demonstratedLevel(): Cefr? =
        results.filter { it.correct >= passMark }.maxByOrNull { it.level.ordinal }?.level
}
