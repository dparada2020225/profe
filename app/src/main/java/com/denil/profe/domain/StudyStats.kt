package com.denil.profe.domain

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/** Utilidades puras de fechas y estadísticas (sin dependencias de Android). */
object StudyStats {

    fun toLocalDate(epochMillis: Long, zone: ZoneId = ZoneId.systemDefault()): LocalDate =
        Instant.ofEpochMilli(epochMillis).atZone(zone).toLocalDate()

    fun startOfDay(date: LocalDate, zone: ZoneId = ZoneId.systemDefault()): Long =
        date.atStartOfDay(zone).toInstant().toEpochMilli()

    fun endOfDay(date: LocalDate, zone: ZoneId = ZoneId.systemDefault()): Long =
        startOfDay(date.plusDays(1), zone) - 1

    /**
     * Racha de días consecutivos con repasos, contando hacia atrás desde hoy
     * (o desde ayer si hoy todavía no se ha estudiado).
     */
    fun streak(reviewTimestamps: Collection<Long>, today: LocalDate, zone: ZoneId = ZoneId.systemDefault()): Int {
        if (reviewTimestamps.isEmpty()) return 0
        val days = reviewTimestamps.map { toLocalDate(it, zone) }.toHashSet()
        var cursor = if (today in days) today else today.minusDays(1)
        var count = 0
        while (cursor in days) {
            count++
            cursor = cursor.minusDays(1)
        }
        return count
    }

    /** Repasos por día para los últimos `days` días (incluyendo hoy), en orden cronológico. */
    fun reviewsPerDay(
        reviewTimestamps: Collection<Long>,
        today: LocalDate,
        days: Int,
        zone: ZoneId = ZoneId.systemDefault()
    ): List<Pair<LocalDate, Int>> {
        val counts = HashMap<LocalDate, Int>()
        for (ts in reviewTimestamps) {
            val d = toLocalDate(ts, zone)
            counts[d] = (counts[d] ?: 0) + 1
        }
        return (days - 1 downTo 0).map { offset ->
            val d = today.minusDays(offset.toLong())
            d to (counts[d] ?: 0)
        }
    }

    /**
     * Pronóstico: cuántas tarjetas vencen cada uno de los próximos `days` días.
     * Las tarjetas ya vencidas se cuentan en el día de hoy.
     */
    fun forecast(
        cards: List<SchedulableCard>,
        today: LocalDate,
        days: Int,
        zone: ZoneId = ZoneId.systemDefault()
    ): List<Pair<LocalDate, Int>> {
        val counts = IntArray(days)
        for (card in cards) {
            if (card.status == CardStatus.NEW) continue
            val due = toLocalDate(card.dueAt, zone)
            val offset = java.time.temporal.ChronoUnit.DAYS.between(today, due).toInt()
            val index = offset.coerceAtLeast(0)
            if (index < days) counts[index]++
        }
        return (0 until days).map { today.plusDays(it.toLong()) to counts[it] }
    }
}
