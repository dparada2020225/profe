package com.denil.profe.domain

import kotlin.math.max
import kotlin.math.roundToInt

/** Calificación que el usuario da a una tarjeta tras verla. */
enum class Rating(val quality: Int) {
    AGAIN(1),
    HARD(3),
    GOOD(4),
    EASY(5)
}

/** Estado de una tarjeta según su progreso. */
enum class CardStatus { NEW, LEARNING, MATURE }

/** Parámetros SM-2 que se guardan por tarjeta. */
data class Sm2State(
    val easeFactor: Double = Sm2.DEFAULT_EASE,
    val intervalDays: Int = 0,
    val repetitions: Int = 0,
    val lapses: Int = 0
)

/** Resultado de aplicar una calificación. */
data class Sm2Result(
    val state: Sm2State,
    /** Minutos hasta el próximo repaso (10 min si se falló, N días si no). */
    val nextIntervalMinutes: Long
) {
    val nextIntervalDays: Int get() = (nextIntervalMinutes / MINUTES_PER_DAY).toInt()

    companion object {
        const val MINUTES_PER_DAY = 24L * 60L
    }
}

/**
 * Implementación del algoritmo SM-2 (SuperMemo 2) con cuatro botones
 * al estilo Anki: Otra vez / Difícil / Bien / Fácil.
 */
object Sm2 {
    const val DEFAULT_EASE = 2.5
    const val MIN_EASE = 1.3
    const val MATURE_THRESHOLD_DAYS = 21
    const val RELEARN_MINUTES = 10L
    const val MAX_INTERVAL_DAYS = 365 * 5

    fun review(state: Sm2State, rating: Rating): Sm2Result {
        val q = rating.quality
        // Fórmula clásica de SM-2 para el factor de facilidad.
        val newEase = max(
            MIN_EASE,
            state.easeFactor + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02))
        )

        if (rating == Rating.AGAIN) {
            val newState = state.copy(
                easeFactor = newEase,
                intervalDays = 0,
                repetitions = 0,
                lapses = state.lapses + 1
            )
            return Sm2Result(newState, RELEARN_MINUTES)
        }

        val reps = state.repetitions
        val prev = state.intervalDays
        val interval: Int = when (rating) {
            Rating.HARD -> when (reps) {
                0 -> 1
                else -> max(prev + 1, (prev * 1.2).roundToInt())
            }
            Rating.GOOD -> when (reps) {
                0 -> 1
                1 -> 6
                else -> max(prev + 1, (prev * newEase).roundToInt())
            }
            Rating.EASY -> when (reps) {
                0 -> 4
                1 -> 8
                else -> max(prev + 2, (prev * newEase * 1.3).roundToInt())
            }
            Rating.AGAIN -> 0 // manejado arriba
        }.coerceIn(1, MAX_INTERVAL_DAYS)

        val newState = state.copy(
            easeFactor = newEase,
            intervalDays = interval,
            repetitions = reps + 1
        )
        return Sm2Result(newState, interval * Sm2Result.MINUTES_PER_DAY)
    }

    /** Vista previa de los cuatro resultados posibles, para mostrar en los botones. */
    fun preview(state: Sm2State): Map<Rating, Sm2Result> =
        Rating.entries.associateWith { review(state, it) }

    fun statusFor(state: Sm2State, everReviewed: Boolean): CardStatus = when {
        !everReviewed -> CardStatus.NEW
        state.intervalDays >= MATURE_THRESHOLD_DAYS -> CardStatus.MATURE
        else -> CardStatus.LEARNING
    }

    /** Texto corto en español para mostrar un intervalo: "10 min", "1 d", "3 sem", "2 m", "1 a". */
    fun formatInterval(minutes: Long): String {
        if (minutes < Sm2Result.MINUTES_PER_DAY) return "$minutes min"
        val days = minutes / Sm2Result.MINUTES_PER_DAY
        return when {
            days < 30 -> "$days d"
            days < 365 -> {
                val months = days / 30.0
                val rounded = (months * 10).roundToInt() / 10.0
                if (rounded == rounded.toLong().toDouble()) "${rounded.toLong()} m" else "$rounded m"
            }
            else -> {
                val years = days / 365.0
                val rounded = (years * 10).roundToInt() / 10.0
                if (rounded == rounded.toLong().toDouble()) "${rounded.toLong()} a" else "$rounded a"
            }
        }
    }
}
