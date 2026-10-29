package com.denil.profe.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        DeckEntity::class, CardEntity::class, ReviewLogEntity::class,
        UnitProgressEntity::class, ActivityEntity::class, ChatMessageEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deckDao(): DeckDao
    abstract fun cardDao(): CardDao
    abstract fun reviewLogDao(): ReviewLogDao
    abstract fun progressDao(): ProgressDao
    abstract fun activityDao(): ActivityDao
    abstract fun chatDao(): ChatDao
}
