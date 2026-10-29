package com.denil.profe.di

import android.content.Context
import androidx.room.Room
import com.denil.profe.data.local.ActivityDao
import com.denil.profe.data.local.AppDatabase
import com.denil.profe.data.local.CardDao
import com.denil.profe.data.local.ChatDao
import com.denil.profe.data.local.DeckDao
import com.denil.profe.data.local.ProgressDao
import com.denil.profe.data.local.ReviewLogDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "profe.db").build()

    @Provides fun provideDeckDao(db: AppDatabase): DeckDao = db.deckDao()
    @Provides fun provideCardDao(db: AppDatabase): CardDao = db.cardDao()
    @Provides fun provideReviewLogDao(db: AppDatabase): ReviewLogDao = db.reviewLogDao()
    @Provides fun provideProgressDao(db: AppDatabase): ProgressDao = db.progressDao()
    @Provides fun provideActivityDao(db: AppDatabase): ActivityDao = db.activityDao()
    @Provides fun provideChatDao(db: AppDatabase): ChatDao = db.chatDao()
}
