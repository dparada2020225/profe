package com.denil.profe.data.io

/** Importación/exportación de tarjetas en CSV con formato `frente;reverso;ejemplo`. */
object CsvCodec {
    const val SEPARATOR = ';'
    const val HEADER = "frente;reverso;ejemplo"

    data class Row(val front: String, val back: String, val example: String)

    data class ParseResult(val rows: List<Row>, val skippedLines: Int)

    /**
     * Admite líneas `frente;reverso;ejemplo` (ejemplo opcional). Los campos pueden ir
     * entre comillas dobles si contienen `;` o saltos de línea; `""` representa una comilla.
     * Se ignoran líneas vacías, la cabecera y las líneas sin frente o reverso.
     */
    fun parse(text: String): ParseResult {
        val rows = ArrayList<Row>()
        var skipped = 0
        val records = splitRecords(text.removePrefix("﻿"))
        for ((index, record) in records.withIndex()) {
            if (record.isBlank()) continue
            val fields = splitFields(record)
            val front = fields.getOrNull(0)?.trim().orEmpty()
            val back = fields.getOrNull(1)?.trim().orEmpty()
            val example = fields.getOrNull(2)?.trim().orEmpty()
            if (index == 0 && front.equals("frente", true) && back.equals("reverso", true)) continue
            if (index == 0 && front.equals("front", true) && back.equals("back", true)) continue
            if (front.isEmpty() || back.isEmpty()) {
                skipped++
                continue
            }
            rows.add(Row(front, back, example))
        }
        return ParseResult(rows, skipped)
    }

    fun encode(rows: List<Row>): String = buildString {
        append(HEADER).append('\n')
        for (r in rows) {
            append(escape(r.front)).append(SEPARATOR)
            append(escape(r.back)).append(SEPARATOR)
            append(escape(r.example)).append('\n')
        }
    }

    private fun escape(value: String): String {
        val needsQuotes = value.any { it == SEPARATOR || it == '"' || it == '\n' || it == '\r' }
        if (!needsQuotes) return value
        return "\"" + value.replace("\"", "\"\"") + "\""
    }

    /** Divide el texto en registros respetando saltos de línea dentro de comillas. */
    private fun splitRecords(text: String): List<String> {
        val out = ArrayList<String>()
        val sb = StringBuilder()
        var inQuotes = false
        var i = 0
        while (i < text.length) {
            val c = text[i]
            when {
                c == '"' -> {
                    inQuotes = !inQuotes
                    sb.append(c)
                }
                (c == '\n' || c == '\r') && !inQuotes -> {
                    if (c == '\r' && i + 1 < text.length && text[i + 1] == '\n') i++
                    out.add(sb.toString())
                    sb.setLength(0)
                }
                else -> sb.append(c)
            }
            i++
        }
        if (sb.isNotEmpty()) out.add(sb.toString())
        return out
    }

    private fun splitFields(record: String): List<String> {
        val out = ArrayList<String>()
        val sb = StringBuilder()
        var inQuotes = false
        var i = 0
        while (i < record.length) {
            val c = record[i]
            when {
                inQuotes && c == '"' && i + 1 < record.length && record[i + 1] == '"' -> {
                    sb.append('"'); i++
                }
                c == '"' -> inQuotes = !inQuotes
                c == SEPARATOR && !inQuotes -> {
                    out.add(sb.toString()); sb.setLength(0)
                }
                else -> sb.append(c)
            }
            i++
        }
        out.add(sb.toString())
        return out
    }
}
