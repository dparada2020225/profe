package com.denil.profe

import com.denil.profe.data.io.CsvCodec
import org.junit.Assert.assertEquals
import org.junit.Test

class CsvCodecTest {

    @Test
    fun `parsea lineas simples e ignora cabecera y vacias`() {
        val text = "frente;reverso;ejemplo\nachieve;lograr;She achieved it.\n\nborrow;pedir prestado\n"
        val r = CsvCodec.parse(text)
        assertEquals(2, r.rows.size)
        assertEquals(CsvCodec.Row("achieve", "lograr", "She achieved it."), r.rows[0])
        assertEquals(CsvCodec.Row("borrow", "pedir prestado", ""), r.rows[1])
        assertEquals(0, r.skippedLines)
    }

    @Test
    fun `omite lineas sin frente o reverso`() {
        val r = CsvCodec.parse("solo frente\n;solo reverso\nok;bien")
        assertEquals(1, r.rows.size)
        assertEquals(2, r.skippedLines)
    }

    @Test
    fun `respeta comillas con separadores dentro`() {
        val r = CsvCodec.parse("\"a; b\";\"dice \"\"hola\"\"\";ej")
        assertEquals(CsvCodec.Row("a; b", "dice \"hola\"", "ej"), r.rows.single())
    }

    @Test
    fun `exportar e importar es reversible`() {
        val rows = listOf(
            CsvCodec.Row("give up", "rendirse", "Don't give up!"),
            CsvCodec.Row("semi; colon", "con \"comillas\"", "línea1\nlínea2")
        )
        val text = CsvCodec.encode(rows)
        assertEquals(rows, CsvCodec.parse(text).rows)
    }
}
