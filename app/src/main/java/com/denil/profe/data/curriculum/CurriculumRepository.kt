package com.denil.profe.data.curriculum

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/** Carga (una sola vez) el curso y el banco del examen de ubicación desde assets/curriculum. */
@Singleton
class CurriculumRepository @Inject constructor(@ApplicationContext private val context: Context) {

    private val mutex = Mutex()
    private var cached: Curriculum? = null
    private var placement: List<PlacementQuestion>? = null

    suspend fun curriculum(): Curriculum = mutex.withLock {
        cached ?: withContext(Dispatchers.IO) {
            CurriculumParser.parseCurriculum(read("curriculum/curriculum.json"))
        }.also { cached = it }
    }

    suspend fun placementQuestions(): List<PlacementQuestion> = mutex.withLock {
        placement ?: withContext(Dispatchers.IO) {
            CurriculumParser.parsePlacement(read("curriculum/placement.json"))
        }.also { placement = it }
    }

    private fun read(path: String): String =
        context.assets.open(path).bufferedReader(Charsets.UTF_8).use { it.readText() }
}
