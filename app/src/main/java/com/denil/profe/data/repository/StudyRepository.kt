package com.denil.profe.data.repository

import androidx.room.withTransaction
import com.denil.profe.data.io.BackupCodec
import com.denil.profe.data.io.CsvCodec
import com.denil.profe.data.local.AppDatabase
import com.denil.profe.data.local.CardDao
import com.denil.profe.data.local.CardEntity
import com.denil.profe.data.local.DeckDao
import com.denil.profe.data.local.DeckEntity
import com.denil.profe.data.local.DeckSummary
import com.denil.profe.data.local.ReviewLogDao
import com.denil.profe.data.local.ReviewLogEntity
import com.denil.profe.data.local.StatusCount
import com.denil.profe.data.prefs.UserPreferences
import com.denil.profe.data.seed.CourseContent
import com.denil.profe.data.seed.CourseInfo
import com.denil.profe.data.seed.CourseLoader
import com.denil.profe.data.seed.SampleDeck
import com.denil.profe.domain.Cloze
import com.denil.profe.data.local.CardType
import com.denil.profe.domain.CardStatus
import com.denil.profe.domain.Rating
import com.denil.profe.domain.SessionPlan
import com.denil.profe.domain.SessionPlanner
import com.denil.profe.domain.Sm2
import com.denil.profe.domain.Sm2Result
import com.denil.profe.domain.StudyStats
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudyRepository @Inject constructor(
    private val db: AppDatabase,
    private val deckDao: DeckDao,
    private val cardDao: CardDao,
    private val logDao: ReviewLogDao,
    private val prefs: UserPreferences,
    private val courseLoader: CourseLoader,
    private val progressDao: com.denil.profe.data.local.ProgressDao,
    private val activityDao: com.denil.profe.data.local.ActivityDao
) {
    // ---------- Mazos ----------

    fun observeDeckSummaries(endOfToday: Long): Flow<List<DeckSummary>> = deckDao.observeSummaries(endOfToday)
    fun observeDeck(id: Long): Flow<DeckEntity?> = deckDao.observe(id)
    suspend fun getDeck(id: Long): DeckEntity? = deckDao.get(id)

    suspend fun createDeck(name: String, description: String): Long =
        deckDao.insert(DeckEntity(name = name.trim(), description = description.trim()))

    suspend fun updateDeck(deck: DeckEntity) = deckDao.update(deck)
    suspend fun deleteDeck(deck: DeckEntity) = deckDao.delete(deck)

    // ---------- Tarjetas ----------

    fun observeCards(deckId: Long): Flow<List<CardEntity>> = cardDao.observeByDeck(deckId)
    fun observeAllCards(): Flow<List<CardEntity>> = cardDao.observeAll()
    suspend fun getCard(id: Long): CardEntity? = cardDao.get(id)

    suspend fun addCard(
        deckId: Long, front: String, back: String, example: String, tags: String,
        type: CardType = CardType.BASIC, hint: String = ""
    ): Long =
        cardDao.insert(
            CardEntity(
                deckId = deckId,
                front = front.trim(),
                back = back.trim(),
                example = example.trim(),
                tags = normalizeTags(tags),
                type = type.name,
                hint = hint.trim()
            )
        )

    suspend fun updateCardContent(
        card: CardEntity, front: String, back: String, example: String, tags: String,
        type: CardType = card.cardType, hint: String = card.hint
    ) =
        cardDao.update(
            card.copy(
                front = front.trim(), back = back.trim(), example = example.trim(), tags = normalizeTags(tags),
                type = type.name, hint = hint.trim()
            )
        )

    suspend fun deleteCard(card: CardEntity) = cardDao.delete(card)

    /** Reinicia el progreso de una tarjeta (vuelve a ser nueva). */
    suspend fun resetCard(card: CardEntity) = cardDao.update(
        card.copy(
            easeFactor = Sm2.DEFAULT_EASE, intervalDays = 0, repetitions = 0, lapses = 0,
            status = CardStatus.NEW.name, dueAt = 0L, lastReviewedAt = null
        )
    )

    private fun normalizeTags(tags: String): String =
        tags.split(',').map { it.trim().lowercase() }.filter { it.isNotEmpty() }.distinct().joinToString(",")

    // ---------- Sesión de repaso ----------

    suspend fun planSession(deckId: Long, now: Long = System.currentTimeMillis()): SessionPlan =
        planSession(listOf(deckId), now)

    /** Planifica una sesión sobre uno o varios mazos (p. ej. todos los de un curso). Los límites diarios se aplican al conjunto. */
    suspend fun planSession(deckIds: List<Long>, now: Long = System.currentTimeMillis()): SessionPlan {
        if (deckIds.isEmpty()) return SessionPlan(emptyList(), emptyList())
        val settings = prefs.current()
        val today = StudyStats.toLocalDate(now)
        val start = StudyStats.startOfDay(today)
        val end = StudyStats.endOfDay(today)
        val cards = cardDao.getByDecks(deckIds).map { it.toSchedulable() }
        return SessionPlanner.plan(
            cards = cards,
            now = now,
            endOfToday = end,
            newPerDay = settings.newPerDay,
            reviewsPerDay = settings.reviewsPerDay,
            newIntroducedToday = logDao.countNewIntroduced(deckIds, start, end),
            reviewsDoneToday = logDao.countReviewsDone(deckIds, start, end)
        )
    }

    suspend fun getDecksOfCourse(course: String): List<DeckEntity> = deckDao.getByCourse(course)

    suspend fun allDeckIds(): List<Long> = deckDao.getAll().map { it.id }

    suspend fun getCardsByIds(ids: List<Long>): Map<Long, CardEntity> =
        if (ids.isEmpty()) emptyMap() else cardDao.getByIds(ids).associateBy { it.id }

    /** Aplica una calificación SM-2, guarda la tarjeta y registra el repaso. Devuelve la tarjeta actualizada. */
    suspend fun reviewCard(card: CardEntity, rating: Rating, now: Long = System.currentTimeMillis()): CardEntity {
        val wasNew = card.cardStatus == CardStatus.NEW
        val result: Sm2Result = Sm2.review(card.sm2State, rating)
        val updated = card.copy(
            easeFactor = result.state.easeFactor,
            intervalDays = result.state.intervalDays,
            repetitions = result.state.repetitions,
            lapses = result.state.lapses,
            status = Sm2.statusFor(result.state, everReviewed = true).name,
            dueAt = now + result.nextIntervalMinutes * 60_000L,
            lastReviewedAt = now
        )
        db.withTransaction {
            cardDao.update(updated)
            logDao.insert(
                ReviewLogEntity(
                    cardId = card.id,
                    deckId = card.deckId,
                    reviewedAt = now,
                    rating = rating.quality,
                    intervalAfterDays = result.state.intervalDays,
                    easeAfter = result.state.easeFactor,
                    wasNew = wasNew
                )
            )
        }
        return updated
    }

    // ---------- Estadísticas ----------

    fun observeStatusCounts(): Flow<List<StatusCount>> = cardDao.observeStatusCounts()
    fun observeStatusCounts(deckId: Long): Flow<List<StatusCount>> = cardDao.observeStatusCounts(deckId)
    fun observeAllReviewTimestamps(): Flow<List<Long>> = logDao.observeAllTimestamps()
    fun observeReviewsToday(): Flow<Int> {
        val today = LocalDate.now()
        return logDao.observeCountBetween(StudyStats.startOfDay(today), StudyStats.endOfDay(today))
    }

    // ---------- CSV ----------

    suspend fun importCsv(deckId: Long, text: String): CsvCodec.ParseResult {
        val parsed = CsvCodec.parse(text)
        if (parsed.rows.isNotEmpty()) {
            val base = System.currentTimeMillis()
            cardDao.insertAll(
                parsed.rows.mapIndexed { i, r ->
                    CardEntity(
                        deckId = deckId, front = r.front, back = r.back, example = r.example, createdAt = base + i,
                        type = if (Cloze.hasBlanks(r.front)) CardType.CLOZE.name else CardType.BASIC.name
                    )
                }
            )
        }
        return parsed
    }

    suspend fun exportCsv(deckId: Long): String =
        CsvCodec.encode(cardDao.getByDeck(deckId).map { CsvCodec.Row(it.front, it.back, it.example) })

    // ---------- Respaldo JSON ----------

    suspend fun exportBackup(): String =
        BackupCodec.encode(
            BackupCodec.Backup(
                deckDao.getAll(), cardDao.getAll(), logDao.getAll(),
                progressDao.getAll(), activityDao.getAll()
            )
        )

    /**
     * Respaldo de Profe: reemplaza todos los datos.
     * Respaldo de Repaso: agrega sus mazos, tarjetas e historial sin borrar nada.
     */
    suspend fun restoreBackup(text: String): BackupCodec.Backup {
        val backup = BackupCodec.decode(text)
        if (backup.fromRepaso) {
            db.withTransaction {
                val deckIds = HashMap<Long, Long>()
                backup.decks.forEach { d -> deckIds[d.id] = deckDao.insert(d.copy(id = 0)) }
                val cardIds = HashMap<Long, Long>()
                backup.cards.forEach { c ->
                    val newDeck = deckIds[c.deckId] ?: return@forEach
                    cardIds[c.id] = cardDao.insert(c.copy(id = 0, deckId = newDeck))
                }
                val logs = backup.logs.mapNotNull { l ->
                    val card = cardIds[l.cardId] ?: return@mapNotNull null
                    val deck = deckIds[l.deckId] ?: return@mapNotNull null
                    l.copy(id = 0, cardId = card, deckId = deck)
                }
                if (logs.isNotEmpty()) logDao.insertAll(logs)
            }
            return backup
        }
        db.withTransaction {
            logDao.deleteAll()
            cardDao.deleteAll()
            deckDao.deleteAll()
            backup.decks.forEach { deckDao.insert(it) }
            if (backup.cards.isNotEmpty()) cardDao.insertAll(backup.cards)
            if (backup.logs.isNotEmpty()) logDao.insertAll(backup.logs)
            progressDao.deleteAll()
            activityDao.deleteAll()
            if (backup.progress.isNotEmpty()) progressDao.insertAll(backup.progress)
            if (backup.activities.isNotEmpty()) activityDao.insertAll(backup.activities)
        }
        return backup
    }

    // ---------- Cursos ----------

    fun listBuiltInCourses(): List<CourseInfo> = courseLoader.listCourses()

    suspend fun isCourseInstalled(courseName: String): Boolean = deckDao.countByCourse(courseName) > 0

    suspend fun installBuiltInCourse(id: String): CourseInfo {
        val content = courseLoader.load(id)
        installCourse(content)
        return content.info
    }

    /** Inserta todos los mazos y tarjetas del curso. Si ya existe, se añaden de nuevo (el usuario decide). */
    suspend fun installCourse(content: CourseContent): Unit = db.withTransaction {
        val base = System.currentTimeMillis()
        var offset = 0L
        for ((deck, cards) in content.decks) {
            val deckId = deckDao.insert(deck)
            if (cards.isNotEmpty()) {
                cardDao.insertAll(cards.map { it.copy(deckId = deckId, createdAt = base + offset + it.createdAt) })
                offset += cards.size + 1
            }
        }
    }

    suspend fun deleteCourse(courseName: String): Unit = db.withTransaction {
        deckDao.getByCourse(courseName).forEach { deckDao.delete(it) }
    }

    suspend fun insertSampleDeck(): Long = db.withTransaction {
        val deckId = deckDao.insert(DeckEntity(name = SampleDeck.NAME, description = SampleDeck.DESCRIPTION))
        val base = System.currentTimeMillis()
        cardDao.insertAll(
            SampleDeck.words.mapIndexed { i, w ->
                CardEntity(deckId = deckId, front = w.front, back = w.back, example = w.example, tags = w.tags, createdAt = base + i)
            }
        )
        deckId
    }
}
