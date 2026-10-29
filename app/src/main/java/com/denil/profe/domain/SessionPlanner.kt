package com.denil.profe.domain

/** Tarjeta mínima que necesita el planificador (independiente de Room). */
data class SchedulableCard(
    val id: Long,
    val status: CardStatus,
    val dueAt: Long,
    val createdAt: Long
)

/** Lo que se repasará hoy: primero los repasos vencidos, luego las tarjetas nuevas. */
data class SessionPlan(
    val reviewIds: List<Long>,
    val newIds: List<Long>
) {
    val queue: List<Long> get() = reviewIds + newIds
    val total: Int get() = reviewIds.size + newIds.size
}

/**
 * Construye la sesión diaria respetando los límites configurados:
 * - `newPerDay` tarjetas nuevas al día (menos las ya introducidas hoy).
 * - `reviewsPerDay` repasos al día (menos los ya hechos hoy).
 */
object SessionPlanner {

    fun plan(
        cards: List<SchedulableCard>,
        now: Long,
        endOfToday: Long,
        newPerDay: Int,
        reviewsPerDay: Int,
        newIntroducedToday: Int,
        reviewsDoneToday: Int
    ): SessionPlan {
        val remainingNew = (newPerDay - newIntroducedToday).coerceAtLeast(0)
        val remainingReviews = (reviewsPerDay - reviewsDoneToday).coerceAtLeast(0)

        val due = cards
            .filter { it.status != CardStatus.NEW && it.dueAt <= endOfToday }
            .sortedBy { it.dueAt }

        // Las tarjetas ya vencidas (dueAt <= now) tienen prioridad sobre las que vencen más tarde hoy.
        val (overdue, laterToday) = due.partition { it.dueAt <= now }
        val reviews = (overdue + laterToday).take(remainingReviews).map { it.id }

        val fresh = cards
            .filter { it.status == CardStatus.NEW }
            .sortedWith(compareBy<SchedulableCard> { it.createdAt }.thenBy { it.id })
            .take(remainingNew)
            .map { it.id }

        return SessionPlan(reviewIds = reviews, newIds = fresh)
    }
}
