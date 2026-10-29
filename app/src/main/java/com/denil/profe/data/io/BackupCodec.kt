package com.denil.profe.data.io

import com.denil.profe.data.local.CardEntity
import com.denil.profe.data.local.DeckEntity
import com.denil.profe.data.local.ReviewLogEntity
import com.denil.profe.data.local.ActivityEntity
import com.denil.profe.data.local.UnitProgressEntity
import org.json.JSONArray
import org.json.JSONObject

/** Respaldo completo (mazos, tarjetas, historial y progreso del curso) en JSON. También lee respaldos de Repaso. */
object BackupCodec {
    const val FORMAT = "profe-backup"
    const val REPASO_FORMAT = "repaso-backup"
    const val VERSION = 1

    data class Backup(
        val decks: List<DeckEntity>,
        val cards: List<CardEntity>,
        val logs: List<ReviewLogEntity>,
        val progress: List<UnitProgressEntity> = emptyList(),
        val activities: List<ActivityEntity> = emptyList(),
        val exportedAt: Long = System.currentTimeMillis(),
        /** true si el archivo venía de la app Repaso (no trae progreso del curso). */
        val fromRepaso: Boolean = false
    )

    fun encode(backup: Backup): String {
        val root = JSONObject()
        root.put("format", FORMAT)
        root.put("version", VERSION)
        root.put("exportedAt", backup.exportedAt)
        root.put("decks", JSONArray().apply {
            backup.decks.forEach { d ->
                put(JSONObject().apply {
                    put("id", d.id); put("name", d.name); put("description", d.description); put("createdAt", d.createdAt)
                    put("course", d.course); put("position", d.position)
                })
            }
        })
        root.put("cards", JSONArray().apply {
            backup.cards.forEach { c ->
                put(JSONObject().apply {
                    put("id", c.id); put("deckId", c.deckId); put("front", c.front); put("back", c.back)
                    put("example", c.example); put("tags", c.tags); put("easeFactor", c.easeFactor)
                    put("intervalDays", c.intervalDays); put("repetitions", c.repetitions); put("lapses", c.lapses)
                    put("status", c.status); put("dueAt", c.dueAt)
                    put("lastReviewedAt", c.lastReviewedAt ?: JSONObject.NULL); put("createdAt", c.createdAt)
                    put("type", c.type); put("hint", c.hint); put("audio", c.audio); put("section", c.section)
                })
            }
        })
        root.put("logs", JSONArray().apply {
            backup.logs.forEach { l ->
                put(JSONObject().apply {
                    put("id", l.id); put("cardId", l.cardId); put("deckId", l.deckId); put("reviewedAt", l.reviewedAt)
                    put("rating", l.rating); put("intervalAfterDays", l.intervalAfterDays); put("easeAfter", l.easeAfter)
                    put("wasNew", l.wasNew)
                })
            }
        })
        root.put("progress", JSONArray().apply {
            backup.progress.forEach { p ->
                put(JSONObject().apply {
                    put("unitId", p.unitId); put("level", p.level); put("bestScore", p.bestScore); put("lastScore", p.lastScore)
                    put("attempts", p.attempts); put("completedAt", p.completedAt ?: JSONObject.NULL)
                    put("vocabAdded", p.vocabAdded); put("openedAt", p.openedAt); put("updatedAt", p.updatedAt)
                })
            }
        })
        root.put("activities", JSONArray().apply {
            backup.activities.forEach { a ->
                put(JSONObject().apply { put("id", a.id); put("at", a.at); put("kind", a.kind); put("xp", a.xp); put("detail", a.detail) })
            }
        })
        return root.toString(2)
    }

    /** Lanza IllegalArgumentException si el archivo no es un respaldo válido. */
    fun decode(text: String): Backup {
        val root = JSONObject(text)
        val format = root.optString("format")
        require(format == FORMAT || format == REPASO_FORMAT) { "El archivo no es un respaldo de Profe ni de Repaso." }
        val decks = ArrayList<DeckEntity>()
        val dArr = root.optJSONArray("decks") ?: JSONArray()
        for (i in 0 until dArr.length()) {
            val o = dArr.getJSONObject(i)
            decks.add(
                DeckEntity(
                    id = o.getLong("id"),
                    name = o.getString("name"),
                    description = o.optString("description", ""),
                    createdAt = o.optLong("createdAt", System.currentTimeMillis()),
                    course = o.optString("course", ""),
                    position = o.optInt("position", 0)
                )
            )
        }
        val cards = ArrayList<CardEntity>()
        val cArr = root.optJSONArray("cards") ?: JSONArray()
        for (i in 0 until cArr.length()) {
            val o = cArr.getJSONObject(i)
            cards.add(
                CardEntity(
                    id = o.getLong("id"),
                    deckId = o.getLong("deckId"),
                    front = o.getString("front"),
                    back = o.getString("back"),
                    example = o.optString("example", ""),
                    tags = o.optString("tags", ""),
                    easeFactor = o.optDouble("easeFactor", 2.5),
                    intervalDays = o.optInt("intervalDays", 0),
                    repetitions = o.optInt("repetitions", 0),
                    lapses = o.optInt("lapses", 0),
                    status = o.optString("status", "NEW"),
                    dueAt = o.optLong("dueAt", 0L),
                    lastReviewedAt = if (o.isNull("lastReviewedAt")) null else o.getLong("lastReviewedAt"),
                    createdAt = o.optLong("createdAt", System.currentTimeMillis()),
                    type = o.optString("type", "BASIC"),
                    hint = o.optString("hint", ""),
                    audio = o.optString("audio", ""),
                    section = o.optString("section", "")
                )
            )
        }
        val logs = ArrayList<ReviewLogEntity>()
        val lArr = root.optJSONArray("logs") ?: JSONArray()
        for (i in 0 until lArr.length()) {
            val o = lArr.getJSONObject(i)
            logs.add(
                ReviewLogEntity(
                    id = o.getLong("id"),
                    cardId = o.getLong("cardId"),
                    deckId = o.getLong("deckId"),
                    reviewedAt = o.getLong("reviewedAt"),
                    rating = o.getInt("rating"),
                    intervalAfterDays = o.optInt("intervalAfterDays", 0),
                    easeAfter = o.optDouble("easeAfter", 2.5),
                    wasNew = o.optBoolean("wasNew", false)
                )
            )
        }
        val progress = ArrayList<UnitProgressEntity>()
        val pArr = root.optJSONArray("progress") ?: JSONArray()
        for (i in 0 until pArr.length()) {
            val o = pArr.getJSONObject(i)
            progress.add(
                UnitProgressEntity(
                    unitId = o.getString("unitId"),
                    level = o.optString("level", "A1"),
                    bestScore = o.optInt("bestScore", 0),
                    lastScore = o.optInt("lastScore", 0),
                    attempts = o.optInt("attempts", 0),
                    completedAt = if (o.isNull("completedAt")) null else o.optLong("completedAt"),
                    vocabAdded = o.optBoolean("vocabAdded", false),
                    openedAt = o.optLong("openedAt", 0L),
                    updatedAt = o.optLong("updatedAt", 0L)
                )
            )
        }
        val activities = ArrayList<ActivityEntity>()
        val aArr = root.optJSONArray("activities") ?: JSONArray()
        for (i in 0 until aArr.length()) {
            val o = aArr.getJSONObject(i)
            activities.add(ActivityEntity(id = o.optLong("id"), at = o.getLong("at"), kind = o.getString("kind"), xp = o.optInt("xp"), detail = o.optString("detail")))
        }
        return Backup(decks, cards, logs, progress, activities, root.optLong("exportedAt", 0L), fromRepaso = format == REPASO_FORMAT)
    }
}
