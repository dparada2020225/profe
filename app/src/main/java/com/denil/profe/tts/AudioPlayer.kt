package com.denil.profe.tts

import android.media.AudioAttributes
import android.media.MediaPlayer
import javax.inject.Inject
import javax.inject.Singleton

/** Reproduce audios cortos desde internet (pronunciación del diccionario). */
@Singleton
class AudioPlayer @Inject constructor() {
    private var player: MediaPlayer? = null

    /** Devuelve false si no se pudo iniciar; `onError` se llama si falla al cargar. */
    fun play(url: String, onError: () -> Unit = {}): Boolean {
        stop()
        return try {
            val mp = MediaPlayer()
            mp.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
            )
            mp.setDataSource(url)
            mp.setOnPreparedListener { it.start() }
            mp.setOnCompletionListener { stop() }
            mp.setOnErrorListener { _, _, _ -> stop(); onError(); true }
            mp.prepareAsync()
            player = mp
            true
        } catch (e: Exception) {
            stop()
            false
        }
    }

    fun stop() {
        player?.let { runCatching { it.release() } }
        player = null
    }
}
