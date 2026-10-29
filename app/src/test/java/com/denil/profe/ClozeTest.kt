package com.denil.profe

import com.denil.profe.domain.Cloze
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ClozeTest {

    @Test
    fun `detecta y parsea huecos`() {
        assertTrue(Cloze.hasBlanks("She {{is}} here."))
        assertFalse(Cloze.hasBlanks("She is here."))
        val segs = Cloze.parse("She {{is}} a {{doctor|nurse}}.")
        assertEquals(5, segs.size)
        assertEquals(listOf(false, true, false, true, false), segs.map { it.isBlank })
        assertEquals(listOf("doctor", "nurse"), segs[3].alternatives)
        assertEquals("doctor", segs[3].display)
    }

    @Test
    fun `enmascara y rellena`() {
        val t = "We {{are}} from {{Antigua}}."
        assertEquals("We ______ from ______.", Cloze.masked(t))
        assertEquals("We are from Antigua.", Cloze.filled(t))
    }

    @Test
    fun `comprueba respuestas ignorando mayusculas, acentos y espacios`() {
        assertTrue(Cloze.check("She {{isn't|is not}} here.", "  IS NOT ").allCorrect)
        assertTrue(Cloze.check("She {{isn't|is not}} here.", "isn’t").allCorrect) // apóstrofo tipográfico
        assertFalse(Cloze.check("She {{isn't|is not}} here.", "is").allCorrect)
        assertTrue(Cloze.check("Dogs are {{Ø|-|nada}} animals.", "-").allCorrect)
    }

    @Test
    fun `varios huecos se responden separados por coma`() {
        val t = "{{Is}} your sister at home? — Yes, she {{is}}."
        val r = Cloze.check(t, "is, is")
        assertTrue(r.allCorrect)
        val partial = Cloze.check(t, "are, is")
        assertFalse(partial.allCorrect)
        assertFalse(partial.blanks[0].correct)
        assertTrue(partial.blanks[1].correct)
        val missing = Cloze.check(t, "is")
        assertFalse(missing.allCorrect)
        assertEquals("", missing.blanks[1].given)
    }

    @Test
    fun `sin huecos siempre es correcto`() {
        assertTrue(Cloze.check("plain text", "anything").allCorrect)
    }
}
