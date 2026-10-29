package com.denil.profe.data.remote

import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

data class GrammarIssue(
    val message: String,
    val offset: Int,
    val length: Int,
    val replacements: List<String>,
    val category: String
)

/** Corrector gramatical gratuito (API pública de LanguageTool, sin key). */
@Singleton
class LanguageToolClient @Inject constructor() {

    companion object {
        const val ENDPOINT = "https://api.languagetool.org/v2/check"
    }

    suspend fun check(text: String): List<GrammarIssue> {
        val res = Http.postForm(ENDPOINT, mapOf("text" to text, "language" to "en-US", "motherTongue" to "es"))
        if (res.code == 429) throw NetworkException("LanguageTool está saturado; intenta en un minuto.", 429)
        if (res.code !in 200..299) throw NetworkException("LanguageTool respondió ${res.code}.", res.code)
        val matches = JSONObject(res.body).getJSONArray("matches")
        return (0 until matches.length()).map { i ->
            val m = matches.getJSONObject(i)
            val reps = m.optJSONArray("replacements")
            GrammarIssue(
                message = m.optString("message"),
                offset = m.optInt("offset"),
                length = m.optInt("length"),
                replacements = if (reps == null) emptyList() else (0 until minOf(reps.length(), 4)).map { reps.getJSONObject(it).optString("value") },
                category = m.optJSONObject("rule")?.optJSONObject("category")?.optString("name").orEmpty()
            )
        }
    }
}
