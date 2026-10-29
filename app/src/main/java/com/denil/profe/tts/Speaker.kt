package com.denil.profe.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

enum class TtsStatus { INITIALIZING, READY, LANGUAGE_MISSING, ENGINE_UNAVAILABLE }

/** Envoltorio de TextToSpeech en inglés; funciona sin internet si la voz está descargada. */
@Singleton
class Speaker @Inject constructor(@ApplicationContext private val context: Context) {

    private val _status = MutableStateFlow(TtsStatus.INITIALIZING)
    val status: StateFlow<TtsStatus> = _status

    private val _speaking = MutableStateFlow(false)
    val speaking: StateFlow<Boolean> = _speaking

    /** Velocidad base configurada por el usuario. */
    var baseRate: Float = 0.95f

    private var tts: TextToSpeech? = null

    init {
        tts = try {
            TextToSpeech(context) { result ->
                if (result == TextToSpeech.SUCCESS) configureLanguage() else _status.value = TtsStatus.ENGINE_UNAVAILABLE
            }
        } catch (e: Exception) {
            _status.value = TtsStatus.ENGINE_UNAVAILABLE
            null
        }
    }

    private fun configureLanguage() {
        val engine = tts ?: run { _status.value = TtsStatus.ENGINE_UNAVAILABLE; return }
        engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) { _speaking.value = true }
            override fun onDone(utteranceId: String?) { _speaking.value = false }
            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) { _speaking.value = false }
            override fun onError(utteranceId: String?, errorCode: Int) { _speaking.value = false }
        })
        val result = engine.setLanguage(Locale.US)
        _status.value = when (result) {
            TextToSpeech.LANG_MISSING_DATA, TextToSpeech.LANG_NOT_SUPPORTED -> {
                val fallback = engine.setLanguage(Locale.ENGLISH)
                if (fallback == TextToSpeech.LANG_MISSING_DATA || fallback == TextToSpeech.LANG_NOT_SUPPORTED)
                    TtsStatus.LANGUAGE_MISSING else TtsStatus.READY
            }
            else -> TtsStatus.READY
        }
    }

    /** Pronuncia en inglés. `slow` = 60 % de la velocidad base. Devuelve false si no se pudo. */
    fun speak(text: String, slow: Boolean = false): Boolean {
        val engine = tts ?: return false
        if (_status.value != TtsStatus.READY || text.isBlank()) return false
        engine.setSpeechRate(if (slow) baseRate * 0.6f else baseRate)
        val clean = text.replace("___", "blank").replace(Regex("\\*\\*?"), "")
        val res = engine.speak(clean, TextToSpeech.QUEUE_FLUSH, null, "profe-${System.currentTimeMillis()}")
        return res == TextToSpeech.SUCCESS
    }

    fun stop() {
        tts?.stop()
        _speaking.value = false
    }

    fun shutdown() {
        tts?.shutdown()
        tts = null
    }
}
