package com.denil.profe.data.seed

import android.content.Context
import com.denil.profe.data.local.CardEntity
import com.denil.profe.data.local.CardType
import com.denil.profe.data.local.DeckEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/** Cursos incluidos en la app (archivos JSON en assets/courses). */
data class CourseInfo(val id: String, val name: String, val description: String, val deckCount: Int, val cardCount: Int)

data class CourseContent(val info: CourseInfo, val decks: List<Pair<DeckEntity, List<CardEntity>>>)

@Singleton
class CourseLoader @Inject constructor(@ApplicationContext private val context: Context) {

    companion object {
        val BUILT_IN = listOf("gic2")
    }

    fun listCourses(): List<CourseInfo> = BUILT_IN.mapNotNull { id -> runCatching { load(id).info }.getOrNull() }

    /** Lee el JSON del curso y lo convierte en entidades (sin deckId todavía: se asigna al insertar). */
    fun load(id: String): CourseContent {
        val text = context.assets.open("courses/$id.json").bufferedReader(Charsets.UTF_8).use { it.readText() }
        return parse(text)
    }

    fun parse(text: String): CourseContent {
        val root = JSONObject(text)
        val courseName = root.getString("name")
        val decksArr = root.getJSONArray("decks")
        val decks = ArrayList<Pair<DeckEntity, List<CardEntity>>>()
        var cardCount = 0
        for (i in 0 until decksArr.length()) {
            val d = decksArr.getJSONObject(i)
            val deck = DeckEntity(
                name = d.getString("name"),
                description = d.optString("description", ""),
                course = courseName,
                position = d.optInt("unit", i + 1)
            )
            val cardsArr = d.getJSONArray("cards")
            val cards = ArrayList<CardEntity>(cardsArr.length())
            for (j in 0 until cardsArr.length()) {
                val c = cardsArr.getJSONObject(j)
                val type = runCatching { CardType.valueOf(c.optString("type", "BASIC")) }.getOrDefault(CardType.BASIC)
                cards.add(
                    CardEntity(
                        deckId = 0,
                        front = c.getString("front"),
                        back = c.optString("back", ""),
                        example = c.optString("example", ""),
                        tags = c.optString("tags", ""),
                        type = type.name,
                        hint = c.optString("hint", ""),
                        audio = c.optString("audio", ""),
                        section = c.optString("section", ""),
                        createdAt = j.toLong() // orden estable dentro del mazo; se ajusta al insertar
                    )
                )
            }
            cardCount += cards.size
            decks.add(deck to cards)
        }
        val info = CourseInfo(
            id = root.optString("id", courseName),
            name = courseName,
            description = root.optString("description", ""),
            deckCount = decks.size,
            cardCount = cardCount
        )
        return CourseContent(info, decks)
    }
}
