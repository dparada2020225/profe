package com.denil.profe.data.local

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/** Progreso de una unidad del curso. */
@Entity(tableName = "unit_progress")
data class UnitProgressEntity(
    @PrimaryKey val unitId: String,
    val level: String,
    val bestScore: Int = 0,
    val lastScore: Int = 0,
    val attempts: Int = 0,
    /** Momento en que se aprobó por primera vez (≥ 80 %), o null. */
    val completedAt: Long? = null,
    /** true cuando su vocabulario ya se agregó al mazo de repaso. */
    val vocabAdded: Boolean = false,
    val openedAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val completed: Boolean get() = completedAt != null
}

/** Tipos de actividad que suman XP y cuentan para la racha. */
enum class ActivityKind(val label: String) {
    LESSON("Lección"), DICTATION("Dictado"), PRONUNCIATION("Pronunciación"), WRITING("Escritura"),
    TUTOR("Tutor"), PLACEMENT("Examen de nivel"), REVIEW("Repaso"), DICTIONARY("Diccionario")
}

@Entity(tableName = "activities", indices = [Index("at")])
data class ActivityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val at: Long = System.currentTimeMillis(),
    val kind: String,
    val xp: Int,
    val detail: String = ""
)

@Entity(tableName = "chat_messages", indices = [Index("conversation")])
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val conversation: String,
    /** "user" | "assistant" */
    val role: String,
    val content: String,
    val at: Long = System.currentTimeMillis()
)

@Dao
interface ProgressDao {
    @Query("SELECT * FROM unit_progress")
    fun observeAll(): Flow<List<UnitProgressEntity>>

    @Query("SELECT * FROM unit_progress")
    suspend fun getAll(): List<UnitProgressEntity>

    @Query("SELECT * FROM unit_progress WHERE unitId = :unitId")
    suspend fun get(unitId: String): UnitProgressEntity?

    @Upsert
    suspend fun upsert(progress: UnitProgressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<UnitProgressEntity>)

    @Query("DELETE FROM unit_progress")
    suspend fun deleteAll()
}

@Dao
interface ActivityDao {
    @Insert
    suspend fun insert(activity: ActivityEntity): Long

    @Insert
    suspend fun insertAll(items: List<ActivityEntity>)

    @Query("SELECT * FROM activities ORDER BY at")
    suspend fun getAll(): List<ActivityEntity>

    @Query("SELECT at FROM activities")
    fun observeTimestamps(): Flow<List<Long>>

    @Query("SELECT COALESCE(SUM(xp), 0) FROM activities WHERE at BETWEEN :start AND :end")
    fun observeXpBetween(start: Long, end: Long): Flow<Int>

    @Query("SELECT COALESCE(SUM(xp), 0) FROM activities")
    fun observeTotalXp(): Flow<Int>

    @Query("SELECT * FROM activities WHERE at >= :since ORDER BY at DESC")
    fun observeSince(since: Long): Flow<List<ActivityEntity>>

    @Query("DELETE FROM activities")
    suspend fun deleteAll()
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages WHERE conversation = :conversation ORDER BY at, id")
    fun observe(conversation: String): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages WHERE conversation = :conversation ORDER BY at, id")
    suspend fun get(conversation: String): List<ChatMessageEntity>

    @Insert
    suspend fun insert(message: ChatMessageEntity): Long

    @Query("DELETE FROM chat_messages WHERE conversation = :conversation")
    suspend fun clear(conversation: String)
}
