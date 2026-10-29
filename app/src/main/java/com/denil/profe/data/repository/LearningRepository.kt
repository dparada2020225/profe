package com.denil.profe.data.repository

import androidx.room.withTransaction
import com.denil.profe.data.curriculum.CourseUnit
import com.denil.profe.data.curriculum.CurriculumRepository
import com.denil.profe.data.curriculum.Example
import com.denil.profe.data.local.ActivityDao
import com.denil.profe.data.local.ActivityEntity
import com.denil.profe.data.local.ActivityKind
import com.denil.profe.data.local.AppDatabase
import com.denil.profe.data.local.CardDao
import com.denil.profe.data.local.CardEntity
import com.denil.profe.data.local.DeckDao
import com.denil.profe.data.local.DeckEntity
import com.denil.profe.data.local.ProgressDao
import com.denil.profe.data.local.ReviewLogDao
import com.denil.profe.data.local.UnitProgressEntity
import com.denil.profe.domain.Cefr
import com.denil.profe.domain.StudyStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/** Progreso del curso, XP, racha y vocabulario de las unidades. */
@Singleton
class LearningRepository @Inject constructor(
    private val db: AppDatabase,
    private val progressDao: ProgressDao,
    private val activityDao: ActivityDao,
    private val deckDao: DeckDao,
    private val cardDao: CardDao,
    private val logDao: ReviewLogDao,
    private val curriculumRepo: CurriculumRepository
) {
    companion object {
        const val VOCAB_COURSE = "Profe · Vocabulario del curso"
        const val MY_WORDS_DECK = "Mis palabras"
        const val PASS_SCORE = 80
    }

    // ---------- Progreso ----------

    fun observeProgress(): Flow<Map<String, UnitProgressEntity>> =
        progressDao.observeAll().map { list -> list.associateBy { it.unitId } }

    suspend fun progress(unitId: String): UnitProgressEntity? = progressDao.get(unitId)

    /** Al abrir una unidad se registra y su vocabulario entra al mazo de repaso (una sola vez). */
    suspend fun openUnit(unit: CourseUnit) {
        val existing = progressDao.get(unit.id)
        if (existing?.vocabAdded == true) return
        db.withTransaction {
            addVocabToDeck(unit)
            progressDao.upsert(
                (existing ?: UnitProgressEntity(unitId = unit.id, level = unit.level.name))
                    .copy(vocabAdded = true, updatedAt = System.currentTimeMillis())
            )
        }
    }

    private suspend fun addVocabToDeck(unit: CourseUnit) {
        val deckName = "Vocabulario ${unit.level.name}"
        val deck = deckDao.getByCourse(VOCAB_COURSE).firstOrNull { it.name == deckName }
        val deckId = deck?.id ?: deckDao.insert(
            DeckEntity(
                name = deckName,
                description = "Palabras de las lecciones de ${unit.level.name} (${unit.level.label}).",
                course = VOCAB_COURSE,
                position = unit.level.ordinal + 1
            )
        )
        val existingFronts = cardDao.getByDeck(deckId).map { it.front.lowercase() }.toHashSet()
        val base = System.currentTimeMillis()
        val cards = unit.vocab.filter { it.en.lowercase() !in existingFronts }.mapIndexed { i, v ->
            CardEntity(
                deckId = deckId, front = v.en, back = v.es, example = v.example,
                tags = unit.id, section = unit.id, createdAt = base + i
            )
        }
        if (cards.isNotEmpty()) cardDao.insertAll(cards)
    }

    /** Guarda el resultado de un intento de ejercicios. Devuelve el XP ganado. */
    suspend fun recordLessonResult(unit: CourseUnit, score: Int): Int {
        val now = System.currentTimeMillis()
        val existing = progressDao.get(unit.id) ?: UnitProgressEntity(unitId = unit.id, level = unit.level.name)
        val passed = score >= PASS_SCORE
        progressDao.upsert(
            existing.copy(
                bestScore = maxOf(existing.bestScore, score),
                lastScore = score,
                attempts = existing.attempts + 1,
                completedAt = existing.completedAt ?: if (passed) now else null,
                updatedAt = now
            )
        )
        val xp = 5 + score / 5 + if (passed && existing.completedAt == null) 10 else 0
        addActivity(ActivityKind.LESSON, xp, unit.id)
        return xp
    }

    /** Marca como completadas todas las unidades de los niveles inferiores (tras el examen). */
    suspend fun markLevelsBelowAsKnown(level: Cefr) {
        val curriculum = curriculumRepo.curriculum()
        val now = System.currentTimeMillis()
        val items = curriculum.units.filter { it.level < level }.map { u ->
            val existing = progressDao.get(u.id)
            (existing ?: UnitProgressEntity(unitId = u.id, level = u.level.name)).copy(
                completedAt = existing?.completedAt ?: now,
                bestScore = maxOf(existing?.bestScore ?: 0, PASS_SCORE),
                updatedAt = now
            )
        }
        if (items.isNotEmpty()) progressDao.insertAll(items)
    }

    /** Siguiente unidad recomendada: la primera no completada desde el nivel actual. */
    suspend fun nextUnit(level: Cefr, progress: Map<String, UnitProgressEntity>): CourseUnit? {
        val units = curriculumRepo.curriculum().units
        return units.firstOrNull { it.level >= level && progress[it.id]?.completed != true }
            ?: units.firstOrNull { progress[it.id]?.completed != true }
    }

    // ---------- Actividad, XP y racha ----------

    suspend fun addActivity(kind: ActivityKind, xp: Int, detail: String = "") {
        activityDao.insert(ActivityEntity(kind = kind.name, xp = xp, detail = detail))
    }

    /** XP de hoy = actividades + 1 por cada tarjeta repasada. */
    fun observeXpToday(): Flow<Int> {
        val today = LocalDate.now()
        val start = StudyStats.startOfDay(today)
        val end = StudyStats.endOfDay(today)
        return combine(activityDao.observeXpBetween(start, end), logDao.observeCountBetween(start, end)) { a, r -> a + r }
    }

    fun observeTotalXp(): Flow<Int> =
        combine(activityDao.observeTotalXp(), logDao.observeAllTimestamps()) { a, r -> a + r.size }

    /** Racha: días seguidos con cualquier actividad (lecciones, práctica o repasos). */
    fun observeStreak(): Flow<Int> =
        combine(activityDao.observeTimestamps(), logDao.observeAllTimestamps()) { a, r ->
            StudyStats.streak(a + r, LocalDate.now())
        }

    fun observeAllActivityTimestamps(): Flow<List<Long>> =
        combine(activityDao.observeTimestamps(), logDao.observeAllTimestamps()) { a, r -> a + r }

    fun observeRecentActivities(days: Int = 7): Flow<List<ActivityEntity>> =
        activityDao.observeSince(StudyStats.startOfDay(LocalDate.now().minusDays(days.toLong())))

    // ---------- Frases para práctica ----------

    /** Ejemplos de las unidades del nivel indicado (y el anterior, para repasar). */
    suspend fun practiceSentences(level: Cefr): List<Example> {
        val curriculum = curriculumRepo.curriculum()
        val levels = setOfNotNull(level, Cefr.entries.getOrNull(level.ordinal - 1))
        return curriculum.units.filter { it.level in levels }.flatMap { it.examples }
    }

    // ---------- Mis palabras (desde el diccionario) ----------

    suspend fun addToMyWords(front: String, back: String, example: String): Long = db.withTransaction {
        val deck = deckDao.getAll().firstOrNull { it.name == MY_WORDS_DECK && it.course.isEmpty() }
        val deckId = deck?.id ?: deckDao.insert(
            DeckEntity(name = MY_WORDS_DECK, description = "Palabras que agregaste desde el diccionario.")
        )
        cardDao.insert(CardEntity(deckId = deckId, front = front.trim(), back = back.trim(), example = example.trim(), tags = "diccionario"))
    }

    // ---------- Respaldo ----------

    suspend fun allProgress(): List<UnitProgressEntity> = progressDao.getAll()
    suspend fun allActivities(): List<ActivityEntity> = activityDao.getAll()

    suspend fun replaceProgress(progress: List<UnitProgressEntity>, activities: List<ActivityEntity>) = db.withTransaction {
        progressDao.deleteAll()
        activityDao.deleteAll()
        if (progress.isNotEmpty()) progressDao.insertAll(progress)
        if (activities.isNotEmpty()) activityDao.insertAll(activities)
    }
}
