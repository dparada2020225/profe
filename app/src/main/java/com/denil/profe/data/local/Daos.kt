package com.denil.profe.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DeckDao {
    @Query(
        """
        SELECT d.id AS id, d.name AS name, d.description AS description, d.createdAt AS createdAt,
            d.course AS course, d.position AS position,
            (SELECT COUNT(*) FROM cards c WHERE c.deckId = d.id) AS totalCards,
            (SELECT COUNT(*) FROM cards c WHERE c.deckId = d.id AND c.status = 'NEW') AS newCards,
            (SELECT COUNT(*) FROM cards c WHERE c.deckId = d.id AND c.status = 'LEARNING') AS learningCards,
            (SELECT COUNT(*) FROM cards c WHERE c.deckId = d.id AND c.status = 'MATURE') AS matureCards,
            (SELECT COUNT(*) FROM cards c WHERE c.deckId = d.id AND c.status != 'NEW' AND c.dueAt <= :endOfToday) AS dueCards
        FROM decks d ORDER BY d.course COLLATE NOCASE, d.position, d.name COLLATE NOCASE
        """
    )
    fun observeSummaries(endOfToday: Long): Flow<List<DeckSummary>>

    @Query("SELECT * FROM decks ORDER BY name COLLATE NOCASE")
    suspend fun getAll(): List<DeckEntity>

    @Query("SELECT * FROM decks WHERE id = :id")
    fun observe(id: Long): Flow<DeckEntity?>

    @Query("SELECT * FROM decks WHERE id = :id")
    suspend fun get(id: Long): DeckEntity?

    @Query("SELECT * FROM decks WHERE course = :course ORDER BY position, name COLLATE NOCASE")
    suspend fun getByCourse(course: String): List<DeckEntity>

    @Query("SELECT COUNT(*) FROM decks WHERE course = :course")
    suspend fun countByCourse(course: String): Int

    @Query("SELECT COALESCE(MAX(position), 0) FROM decks")
    suspend fun maxPosition(): Int

    @Query("SELECT COUNT(*) FROM decks")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(deck: DeckEntity): Long

    @Update
    suspend fun update(deck: DeckEntity)

    @Delete
    suspend fun delete(deck: DeckEntity)

    @Query("DELETE FROM decks")
    suspend fun deleteAll()
}

@Dao
interface CardDao {
    @Query("SELECT * FROM cards WHERE deckId = :deckId ORDER BY createdAt DESC, id DESC")
    fun observeByDeck(deckId: Long): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE deckId = :deckId ORDER BY createdAt ASC, id ASC")
    suspend fun getByDeck(deckId: Long): List<CardEntity>

    @Query("SELECT * FROM cards WHERE deckId IN (:deckIds) ORDER BY createdAt ASC, id ASC")
    suspend fun getByDecks(deckIds: List<Long>): List<CardEntity>

    @Query("SELECT * FROM cards ORDER BY id")
    suspend fun getAll(): List<CardEntity>

    @Query("SELECT * FROM cards")
    fun observeAll(): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE id = :id")
    suspend fun get(id: Long): CardEntity?

    @Query("SELECT * FROM cards WHERE id IN (:ids)")
    suspend fun getByIds(ids: List<Long>): List<CardEntity>

    @Query("SELECT status AS status, COUNT(*) AS count FROM cards GROUP BY status")
    fun observeStatusCounts(): Flow<List<StatusCount>>

    @Query("SELECT status AS status, COUNT(*) AS count FROM cards WHERE deckId = :deckId GROUP BY status")
    fun observeStatusCounts(deckId: Long): Flow<List<StatusCount>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(card: CardEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(cards: List<CardEntity>): List<Long>

    @Update
    suspend fun update(card: CardEntity)

    @Delete
    suspend fun delete(card: CardEntity)

    @Query("DELETE FROM cards")
    suspend fun deleteAll()
}

@Dao
interface ReviewLogDao {
    @Insert
    suspend fun insert(log: ReviewLogEntity): Long

    @Insert
    suspend fun insertAll(logs: List<ReviewLogEntity>)

    @Query("SELECT * FROM review_logs ORDER BY id")
    suspend fun getAll(): List<ReviewLogEntity>

    @Query("SELECT reviewedAt FROM review_logs")
    fun observeAllTimestamps(): Flow<List<Long>>

    @Query("SELECT reviewedAt FROM review_logs WHERE reviewedAt >= :since")
    fun observeTimestampsSince(since: Long): Flow<List<Long>>

    @Query("SELECT COUNT(DISTINCT cardId) FROM review_logs WHERE deckId IN (:deckIds) AND wasNew = 1 AND reviewedAt BETWEEN :start AND :end")
    suspend fun countNewIntroduced(deckIds: List<Long>, start: Long, end: Long): Int

    @Query("SELECT COUNT(DISTINCT cardId) FROM review_logs WHERE deckId IN (:deckIds) AND wasNew = 0 AND reviewedAt BETWEEN :start AND :end")
    suspend fun countReviewsDone(deckIds: List<Long>, start: Long, end: Long): Int

    @Query("SELECT COUNT(*) FROM review_logs WHERE reviewedAt BETWEEN :start AND :end")
    fun observeCountBetween(start: Long, end: Long): Flow<Int>

    @Query("DELETE FROM review_logs")
    suspend fun deleteAll()
}
