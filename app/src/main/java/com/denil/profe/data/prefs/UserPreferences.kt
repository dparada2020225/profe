package com.denil.profe.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.denil.profe.domain.Cefr
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "profe_prefs")

enum class ThemeMode { SYSTEM, LIGHT, DARK }

const val DEFAULT_GROQ_MODEL = "llama-3.3-70b-versatile"

data class UserSettings(
    val newPerDay: Int = 15,
    val reviewsPerDay: Int = 150,
    val reminderEnabled: Boolean = false,
    val reminderHour: Int = 20,
    val reminderMinute: Int = 0,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColors: Boolean = true,
    val sampleDeckSeeded: Boolean = false,
    val courseSeeded: Boolean = false,
    /** Nivel en el que estudia (resultado del examen o elegido a mano). */
    val level: Cefr = Cefr.A1,
    val placementDone: Boolean = false,
    /** Nivel demostrado en el último examen ("" si ninguno). */
    val placementResult: String = "",
    val placementAt: Long = 0L,
    val name: String = "",
    val dailyGoalXp: Int = 50,
    val groqApiKey: String = "",
    val groqModel: String = DEFAULT_GROQ_MODEL,
    /** Velocidad de la voz (0.5–1.5). */
    val speechRate: Float = 0.95f,
    /** Leer en voz alta las respuestas del tutor. */
    val tutorSpeaks: Boolean = true
) {
    val hasGroqKey: Boolean get() = groqApiKey.isNotBlank()
}

@Singleton
class UserPreferences @Inject constructor(@ApplicationContext private val context: Context) {

    private object Keys {
        val NEW_PER_DAY = intPreferencesKey("new_per_day")
        val REVIEWS_PER_DAY = intPreferencesKey("reviews_per_day")
        val REMINDER_ENABLED = booleanPreferencesKey("reminder_enabled")
        val REMINDER_HOUR = intPreferencesKey("reminder_hour")
        val REMINDER_MINUTE = intPreferencesKey("reminder_minute")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DYNAMIC_COLORS = booleanPreferencesKey("dynamic_colors")
        val SAMPLE_SEEDED = booleanPreferencesKey("sample_seeded")
        val COURSE_SEEDED = booleanPreferencesKey("course_gic2_seeded")
        val LEVEL = stringPreferencesKey("level")
        val PLACEMENT_DONE = booleanPreferencesKey("placement_done")
        val PLACEMENT_RESULT = stringPreferencesKey("placement_result")
        val PLACEMENT_AT = androidx.datastore.preferences.core.longPreferencesKey("placement_at")
        val NAME = stringPreferencesKey("name")
        val DAILY_GOAL = intPreferencesKey("daily_goal_xp")
        val GROQ_KEY = stringPreferencesKey("groq_api_key")
        val GROQ_MODEL = stringPreferencesKey("groq_model")
        val SPEECH_RATE = floatPreferencesKey("speech_rate")
        val TUTOR_SPEAKS = booleanPreferencesKey("tutor_speaks")
    }

    val settings: Flow<UserSettings> = context.dataStore.data.map { p ->
        UserSettings(
            newPerDay = p[Keys.NEW_PER_DAY] ?: 15,
            reviewsPerDay = p[Keys.REVIEWS_PER_DAY] ?: 150,
            reminderEnabled = p[Keys.REMINDER_ENABLED] ?: false,
            reminderHour = p[Keys.REMINDER_HOUR] ?: 20,
            reminderMinute = p[Keys.REMINDER_MINUTE] ?: 0,
            themeMode = p[Keys.THEME_MODE]?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() } ?: ThemeMode.SYSTEM,
            dynamicColors = p[Keys.DYNAMIC_COLORS] ?: true,
            sampleDeckSeeded = p[Keys.SAMPLE_SEEDED] ?: false,
            courseSeeded = p[Keys.COURSE_SEEDED] ?: false,
            level = Cefr.parse(p[Keys.LEVEL]) ?: Cefr.A1,
            placementDone = p[Keys.PLACEMENT_DONE] ?: false,
            placementResult = p[Keys.PLACEMENT_RESULT] ?: "",
            placementAt = p[Keys.PLACEMENT_AT] ?: 0L,
            name = p[Keys.NAME] ?: "",
            dailyGoalXp = p[Keys.DAILY_GOAL] ?: 50,
            groqApiKey = p[Keys.GROQ_KEY] ?: "",
            groqModel = p[Keys.GROQ_MODEL]?.takeIf { it.isNotBlank() } ?: DEFAULT_GROQ_MODEL,
            speechRate = p[Keys.SPEECH_RATE] ?: 0.95f,
            tutorSpeaks = p[Keys.TUTOR_SPEAKS] ?: true
        )
    }

    suspend fun current(): UserSettings = settings.first()

    suspend fun setNewPerDay(value: Int) = context.dataStore.edit { it[Keys.NEW_PER_DAY] = value.coerceIn(0, 500) }
    suspend fun setReviewsPerDay(value: Int) = context.dataStore.edit { it[Keys.REVIEWS_PER_DAY] = value.coerceIn(0, 2000) }
    suspend fun setReminderEnabled(value: Boolean) = context.dataStore.edit { it[Keys.REMINDER_ENABLED] = value }
    suspend fun setReminderTime(hour: Int, minute: Int) = context.dataStore.edit {
        it[Keys.REMINDER_HOUR] = hour.coerceIn(0, 23)
        it[Keys.REMINDER_MINUTE] = minute.coerceIn(0, 59)
    }
    suspend fun setThemeMode(mode: ThemeMode) = context.dataStore.edit { it[Keys.THEME_MODE] = mode.name }
    suspend fun setDynamicColors(value: Boolean) = context.dataStore.edit { it[Keys.DYNAMIC_COLORS] = value }
    suspend fun setSampleSeeded(value: Boolean) = context.dataStore.edit { it[Keys.SAMPLE_SEEDED] = value }
    suspend fun setCourseSeeded(value: Boolean) = context.dataStore.edit { it[Keys.COURSE_SEEDED] = value }
    suspend fun setLevel(level: Cefr) = context.dataStore.edit { it[Keys.LEVEL] = level.name }
    suspend fun setPlacement(result: Cefr?, recommended: Cefr) = context.dataStore.edit {
        it[Keys.PLACEMENT_DONE] = true
        it[Keys.PLACEMENT_RESULT] = result?.name ?: ""
        it[Keys.PLACEMENT_AT] = System.currentTimeMillis()
        it[Keys.LEVEL] = recommended.name
    }
    suspend fun skipPlacement(level: Cefr) = context.dataStore.edit {
        it[Keys.PLACEMENT_DONE] = true
        it[Keys.LEVEL] = level.name
    }
    suspend fun setName(value: String) = context.dataStore.edit { it[Keys.NAME] = value.trim().take(40) }
    suspend fun setDailyGoal(value: Int) = context.dataStore.edit { it[Keys.DAILY_GOAL] = value.coerceIn(10, 500) }
    suspend fun setGroqApiKey(value: String) = context.dataStore.edit { it[Keys.GROQ_KEY] = value.trim() }
    suspend fun setGroqModel(value: String) = context.dataStore.edit { it[Keys.GROQ_MODEL] = value.trim() }
    suspend fun setSpeechRate(value: Float) = context.dataStore.edit { it[Keys.SPEECH_RATE] = value.coerceIn(0.5f, 1.5f) }
    suspend fun setTutorSpeaks(value: Boolean) = context.dataStore.edit { it[Keys.TUTOR_SPEAKS] = value }
}
