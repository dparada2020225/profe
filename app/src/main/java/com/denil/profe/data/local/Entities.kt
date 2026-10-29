package com.denil.profe.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.denil.profe.domain.CardStatus
import com.denil.profe.domain.SchedulableCard
import com.denil.profe.domain.Sm2
import com.denil.profe.domain.Sm2State

@Entity(tableName = "decks")
data class DeckEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    /** Nombre del curso al que pertenece el mazo ("" = mazo suelto). */
    @ColumnInfo(defaultValue = "") val course: String = "",
    /** Orden dentro del curso. */
    @ColumnInfo(defaultValue = "0") val position: Int = 0
)

/** Tipos de tarjeta. */
enum class CardType { BASIC, CLOZE }

@Entity(
    tableName = "cards",
    foreignKeys = [
        ForeignKey(
            entity = DeckEntity::class,
            parentColumns = ["id"],
            childColumns = ["deckId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("deckId"), Index("dueAt"), Index("status")]
)
data class CardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deckId: Long,
    val front: String,
    val back: String,
    val example: String = "",
    /** Etiquetas separadas por coma. */
    val tags: String = "",
    val easeFactor: Double = Sm2.DEFAULT_EASE,
    val intervalDays: Int = 0,
    val repetitions: Int = 0,
    val lapses: Int = 0,
    val status: String = CardStatus.NEW.name,
    val dueAt: Long = 0L,
    val lastReviewedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    /** BASIC (frente/reverso) o CLOZE (oración con huecos {{respuesta}}). */
    @ColumnInfo(defaultValue = "BASIC") val type: String = CardType.BASIC.name,
    /** Pista opcional (se muestra antes de revelar). */
    @ColumnInfo(defaultValue = "") val hint: String = "",
    /** Texto en inglés que se pronuncia con TTS ("" = se deduce del frente). */
    @ColumnInfo(defaultValue = "") val audio: String = "",
    /** Sección del libro/curso (p. ej. "3.4"). */
    @ColumnInfo(defaultValue = "") val section: String = ""
) {
    val cardType: CardType get() = runCatching { CardType.valueOf(type) }.getOrDefault(CardType.BASIC)
    val isCloze: Boolean get() = cardType == CardType.CLOZE
    val sm2State: Sm2State get() = Sm2State(easeFactor, intervalDays, repetitions, lapses)
    val cardStatus: CardStatus get() = runCatching { CardStatus.valueOf(status) }.getOrDefault(CardStatus.NEW)
    val tagList: List<String> get() = tags.split(',').map { it.trim() }.filter { it.isNotEmpty() }

    fun toSchedulable() = SchedulableCard(id, cardStatus, dueAt, createdAt)
}

@Entity(
    tableName = "review_logs",
    foreignKeys = [
        ForeignKey(
            entity = CardEntity::class,
            parentColumns = ["id"],
            childColumns = ["cardId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("cardId"), Index("reviewedAt"), Index("deckId")]
)
data class ReviewLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cardId: Long,
    val deckId: Long,
    val reviewedAt: Long,
    val rating: Int,
    val intervalAfterDays: Int,
    val easeAfter: Double,
    /** true si la tarjeta era nueva cuando se repasó (cuenta para el límite de nuevas por día). */
    @ColumnInfo(defaultValue = "0") val wasNew: Boolean
)

/** Resumen por mazo para la lista principal. */
data class DeckSummary(
    val id: Long,
    val name: String,
    val description: String,
    val createdAt: Long,
    val course: String,
    val position: Int,
    val totalCards: Int,
    val newCards: Int,
    val learningCards: Int,
    val matureCards: Int,
    val dueCards: Int
)

data class StatusCount(val status: String, val count: Int)
