package com.denil.profe.data.remote

import com.denil.profe.data.prefs.UserPreferences
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

data class ChatTurn(val role: String, val content: String)

/**
 * Cliente de Groq (API compatible con OpenAI). La key se guarda solo en el teléfono (Ajustes).
 * Capa gratis: si se pasa del límite, Groq responde 429 (no cobra).
 */
@Singleton
class GroqClient @Inject constructor(private val prefs: UserPreferences) {

    companion object {
        const val ENDPOINT = "https://api.groq.com/openai/v1/chat/completions"
        const val MODELS_ENDPOINT = "https://api.groq.com/openai/v1/models"
        const val FALLBACK_MODEL = "llama-3.1-8b-instant"
    }

    suspend fun isConfigured(): Boolean = prefs.current().hasGroqKey

    /** Envía la conversación y devuelve la respuesta del modelo. */
    suspend fun chat(
        system: String,
        turns: List<ChatTurn>,
        temperature: Double = 0.6,
        maxTokens: Int = 700,
        jsonMode: Boolean = false
    ): String {
        val settings = prefs.current()
        val key = settings.groqApiKey
        if (key.isBlank()) throw NetworkException("Falta tu API key de Groq. Ponla en Ajustes → Tutor con IA.")
        return try {
            send(key, settings.groqModel, system, turns, temperature, maxTokens, jsonMode)
        } catch (e: NetworkException) {
            // Si el modelo elegido no existe o está saturado, intenta con el modelo pequeño.
            if ((e.code == 404 || e.code == 400 || e.code == 429 || e.code == 503) && settings.groqModel != FALLBACK_MODEL) {
                send(key, FALLBACK_MODEL, system, turns, temperature, maxTokens, jsonMode)
            } else throw e
        }
    }

    private suspend fun send(
        key: String, model: String, system: String, turns: List<ChatTurn>,
        temperature: Double, maxTokens: Int, jsonMode: Boolean
    ): String {
        val messages = JSONArray()
        messages.put(JSONObject().put("role", "system").put("content", system))
        turns.forEach { messages.put(JSONObject().put("role", it.role).put("content", it.content)) }
        val body = JSONObject()
            .put("model", model)
            .put("messages", messages)
            .put("temperature", temperature)
            .put("max_tokens", maxTokens)
        if (jsonMode) body.put("response_format", JSONObject().put("type", "json_object"))
        val res = Http.postJson(ENDPOINT, body.toString(), mapOf("Authorization" to "Bearer $key"))
        if (res.code !in 200..299) throw NetworkException(errorMessage(res), res.code)
        val root = JSONObject(res.body)
        return root.getJSONArray("choices").getJSONObject(0).getJSONObject("message").optString("content").trim()
    }

    /** Verifica que la key funcione. Devuelve null si todo bien, o un mensaje de error. */
    suspend fun testKey(key: String): String? = try {
        val res = Http.get(MODELS_ENDPOINT, mapOf("Authorization" to "Bearer ${key.trim()}"))
        if (res.code in 200..299) null else errorMessage(res)
    } catch (e: NetworkException) {
        e.message
    }

    private fun errorMessage(res: Http.Response): String {
        val apiMsg = runCatching { JSONObject(res.body).getJSONObject("error").optString("message") }.getOrNull().orEmpty()
        return when (res.code) {
            401 -> "La API key de Groq no es válida. Revísala en Ajustes."
            403 -> "Groq rechazó la solicitud (403). $apiMsg"
            429 -> "Llegaste al límite gratis de Groq por ahora. Espera un momento e inténtalo de nuevo."
            in 500..599 -> "Groq no está disponible en este momento (${res.code})."
            else -> "Error de Groq (${res.code}). $apiMsg".trim()
        }
    }
}
