package com.denil.profe

import com.denil.profe.domain.CardStatus
import com.denil.profe.domain.SchedulableCard
import com.denil.profe.domain.SessionPlanner
import com.denil.profe.domain.StudyStats
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class SessionPlannerTest {

    private val zone: ZoneId = ZoneId.of("America/Guatemala")
    private val today: LocalDate = LocalDate.of(2026, 9, 16)
    private val startOfToday = StudyStats.startOfDay(today, zone)
    private val endOfToday = StudyStats.endOfDay(today, zone)
    private val now = startOfToday + 12 * 60 * 60 * 1000L // mediodía
    private val day = 24 * 60 * 60 * 1000L

    private fun newCard(id: Long, createdAt: Long = id) = SchedulableCard(id, CardStatus.NEW, 0L, createdAt)
    private fun learning(id: Long, dueAt: Long) = SchedulableCard(id, CardStatus.LEARNING, dueAt, id)
    private fun mature(id: Long, dueAt: Long) = SchedulableCard(id, CardStatus.MATURE, dueAt, id)

    private fun plan(
        cards: List<SchedulableCard>,
        newPerDay: Int = 10,
        reviewsPerDay: Int = 100,
        newToday: Int = 0,
        reviewsToday: Int = 0
    ) = SessionPlanner.plan(cards, now, endOfToday, newPerDay, reviewsPerDay, newToday, reviewsToday)

    @Test
    fun `respeta el limite de tarjetas nuevas por dia`() {
        val cards = (1L..30L).map { newCard(it) }
        val p = plan(cards, newPerDay = 10)
        assertEquals(10, p.newIds.size)
        assertEquals((1L..10L).toList(), p.newIds) // las más antiguas primero
        assertTrue(p.reviewIds.isEmpty())
    }

    @Test
    fun `descuenta las nuevas ya introducidas hoy`() {
        val cards = (1L..30L).map { newCard(it) }
        val p = plan(cards, newPerDay = 10, newToday = 7)
        assertEquals(3, p.newIds.size)
    }

    @Test
    fun `no propone nuevas si ya se alcanzo el limite`() {
        val cards = (1L..5L).map { newCard(it) }
        val p = plan(cards, newPerDay = 5, newToday = 5)
        assertEquals(0, p.total)
    }

    @Test
    fun `incluye solo repasos vencidos hoy o antes`() {
        val cards = listOf(
            learning(1, now - 3 * day),       // atrasada
            learning(2, now - 60_000L),       // vencida hace un minuto
            mature(3, endOfToday - 1_000L),   // vence hoy más tarde
            mature(4, endOfToday + 1_000L),   // mañana
            mature(5, now + 5 * day)          // dentro de 5 días
        )
        val p = plan(cards)
        assertEquals(listOf(1L, 2L, 3L), p.reviewIds)
    }

    @Test
    fun `las atrasadas van antes que las que vencen mas tarde hoy`() {
        val cards = listOf(
            mature(1, endOfToday - 1_000L),
            learning(2, now - day),
            learning(3, now - 2 * day)
        )
        val p = plan(cards)
        assertEquals(listOf(3L, 2L, 1L), p.reviewIds)
    }

    @Test
    fun `respeta el limite de repasos por dia y descuenta los hechos`() {
        val cards = (1L..50L).map { learning(it, now - it * 1000L) }
        val p = plan(cards, reviewsPerDay = 20, reviewsToday = 15)
        assertEquals(5, p.reviewIds.size)
    }

    @Test
    fun `la cola pone los repasos antes que las nuevas`() {
        val cards = listOf(newCard(10), learning(1, now - day), newCard(11), mature(2, now - day))
        val p = plan(cards, newPerDay = 5)
        assertEquals(listOf(1L, 2L, 10L, 11L), p.queue)
        assertEquals(4, p.total)
    }

    @Test
    fun `las tarjetas nuevas no cuentan como repasos`() {
        val cards = (1L..5L).map { newCard(it) }
        val p = plan(cards, reviewsPerDay = 0)
        assertEquals(5, p.newIds.size)
        assertEquals(0, p.reviewIds.size)
    }

    @Test
    fun `racha cuenta dias consecutivos hasta hoy o ayer`() {
        val stamps = listOf(
            startOfToday + 1000,
            startOfToday - day + 1000,
            startOfToday - 2 * day + 1000,
            startOfToday - 5 * day + 1000 // hueco: no cuenta
        )
        assertEquals(3, StudyStats.streak(stamps, today, zone))
        // Sin estudiar hoy, la racha sigue viva desde ayer.
        assertEquals(2, StudyStats.streak(stamps.drop(1), today, zone))
        // Si el último estudio fue anteayer, la racha se rompe.
        assertEquals(0, StudyStats.streak(stamps.drop(2), today, zone))
    }

    @Test
    fun `pronostico de 7 dias acumula atrasadas en hoy`() {
        val cards = listOf(
            learning(1, now - 3 * day), // atrasada -> hoy
            learning(2, now),           // hoy
            mature(3, now + day),       // mañana
            mature(4, now + 6 * day),   // día 7
            mature(5, now + 7 * day),   // fuera del rango
            newCard(6)                  // nueva: no cuenta
        )
        val f = StudyStats.forecast(cards, today, 7, zone)
        assertEquals(7, f.size)
        assertEquals(2, f[0].second)
        assertEquals(1, f[1].second)
        assertEquals(1, f[6].second)
        assertEquals(4, f.sumOf { it.second })
    }

    @Test
    fun `repasos por dia devuelve una entrada por dia en orden`() {
        val stamps = listOf(startOfToday + 1, startOfToday + 2, startOfToday - day + 1)
        val r = StudyStats.reviewsPerDay(stamps, today, 3, zone)
        assertEquals(3, r.size)
        assertEquals(today.minusDays(2), r[0].first)
        assertEquals(0, r[0].second)
        assertEquals(1, r[1].second)
        assertEquals(2, r[2].second)
    }

    @Test
    fun `los limites diarios se aplican al conjunto de varios mazos`() {
        // Tarjetas de dos "mazos" distintos: el planificador no distingue mazos, solo aplica límites al conjunto.
        val deckA = (1L..8L).map { newCard(it) }
        val deckB = (9L..16L).map { newCard(it) }
        val p = plan(deckA + deckB, newPerDay = 10)
        assertEquals(10, p.newIds.size)
        assertEquals((1L..10L).toList(), p.newIds)
    }
}
