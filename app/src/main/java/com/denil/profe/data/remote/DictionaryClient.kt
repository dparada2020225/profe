package com.denil.profe.data.remote

import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

data class Definition(val partOfSpeech: String, val definition: String, val example: String)

data class DictionaryEntry(
    val word: String,
    val phonetic: String,
    val audioUrl: String,
    val definitions: List<Definition>,
    val synonyms: List<String>
)

data class SentencePair(val en: String, val es: String)

/**
 * Servicios de consulta gratuitos y sin key:
 * - Free Dictionary API (definiciones, fonética y audio)
 * - Tatoeba (oraciones de ejemplo con traducción al español)
 * - MyMemory (traducción corta inglés → español)
 */
@Singleton
class DictionaryClient @Inject constructor() {

    suspend fun lookup(word: String): DictionaryEntry? {
        val res = Http.get("https://api.dictionaryapi.dev/api/v2/entries/en/" + Http.enc(word.trim().lowercase()).replace("+", "%20"))
        if (res.code == 404) return null
        if (res.code !in 200..299) throw NetworkException("El diccionario respondió ${res.code}.", res.code)
        val arr = JSONArray(res.body)
        if (arr.length() == 0) return null
        var phonetic = ""
        var audio = ""
        val defs = ArrayList<Definition>()
        val synonyms = LinkedHashSet<String>()
        for (i in 0 until arr.length()) {
            val e = arr.getJSONObject(i)
            if (phonetic.isBlank()) phonetic = e.optString("phonetic")
            e.optJSONArray("phonetics")?.let { ph ->
                for (j in 0 until ph.length()) {
                    val p = ph.getJSONObject(j)
                    if (phonetic.isBlank()) phonetic = p.optString("text")
                    val a = p.optString("audio")
                    // Preferimos el audio de EE. UU.
                    if (a.isNotBlank() && (audio.isBlank() || a.contains("-us"))) audio = a
                }
            }
            e.optJSONArray("meanings")?.let { meanings ->
                for (j in 0 until meanings.length()) {
                    val m = meanings.getJSONObject(j)
                    val pos = m.optString("partOfSpeech")
                    val ds = m.optJSONArray("definitions") ?: JSONArray()
                    for (k in 0 until minOf(ds.length(), 3)) {
                        val d = ds.getJSONObject(k)
                        defs += Definition(pos, d.optString("definition"), d.optString("example"))
                    }
                    m.optJSONArray("synonyms")?.let { s -> for (k in 0 until s.length()) synonyms += s.getString(k) }
                }
            }
        }
        if (audio.startsWith("//")) audio = "https:$audio"
        return DictionaryEntry(arr.getJSONObject(0).optString("word", word), phonetic, audio, defs.take(8), synonyms.take(8).toList())
    }

    /** Oraciones de Tatoeba que contienen la palabra, con su traducción al español. */
    suspend fun examples(word: String, limit: Int = 6): List<SentencePair> {
        val q = Http.enc("=" + word.trim())
        val url = "https://tatoeba.org/eng/api_v0/search?from=eng&to=spa&query=$q&trans_filter=limit&trans_to=spa&sort=relevance"
        val res = Http.get(url)
        if (res.code !in 200..299) throw NetworkException("Tatoeba respondió ${res.code}.", res.code)
        return parseTatoeba(res.body, limit)
    }

    fun parseTatoeba(body: String, limit: Int): List<SentencePair> {
        val results = JSONObject(body).optJSONArray("results") ?: return emptyList()
        val out = ArrayList<SentencePair>()
        for (i in 0 until results.length()) {
            val r = results.getJSONObject(i)
            val en = r.optString("text")
            val es = firstSpanish(r.opt("translations"))
            if (en.isNotBlank() && es.isNotBlank()) out += SentencePair(en, es)
            if (out.size >= limit) break
        }
        return out
    }

    /** Las traducciones vienen como lista de listas (directas e indirectas). */
    private fun firstSpanish(node: Any?): String {
        when (node) {
            is JSONArray -> for (i in 0 until node.length()) {
                val found = firstSpanish(node.get(i))
                if (found.isNotBlank()) return found
            }
            is JSONObject -> if (node.optString("lang") == "spa") return node.optString("text")
        }
        return ""
    }

    /** Traducción corta inglés → español (MyMemory, gratis sin key). */
    suspend fun translate(text: String, from: String = "en", to: String = "es"): String? {
        val url = "https://api.mymemory.translated.net/get?q=" + Http.enc(text.take(400)) + "&langpair=$from%7C$to"
        val res = Http.get(url)
        if (res.code !in 200..299) return null
        val t = JSONObject(res.body).optJSONObject("responseData")?.optString("translatedText").orEmpty()
        return t.takeIf { it.isNotBlank() && !it.startsWith("MYMEMORY WARNING", ignoreCase = true) }
    }
}
