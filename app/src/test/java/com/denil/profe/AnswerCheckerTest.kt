package com.denil.profe

import com.denil.profe.domain.AnswerChecker
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AnswerCheckerTest {

    @Test
    fun `ignora mayusculas puntuacion y espacios`() {
        assertTrue(AnswerChecker.matches("  i'm from antigua!! ", listOf("I'm from Antigua.")))
        assertTrue(AnswerChecker.matches("Where are you from", listOf("Where are you from?")))
    }

    @Test
    fun `contracciones equivalen a formas completas`() {
        assertTrue(AnswerChecker.matches("I am from Antigua", listOf("I'm from Antigua")))
        assertTrue(AnswerChecker.matches("She does not work", listOf("She doesn't work")))
        assertTrue(AnswerChecker.matches("I cannot sing", listOf("I can't sing")))
        assertTrue(AnswerChecker.matches("I will not tell", listOf("I won't tell")))
        assertTrue(AnswerChecker.matches("They are ready", listOf("They're ready")))
        assertTrue(AnswerChecker.matches("It is late", listOf("It's late")))
    }

    @Test
    fun `apostrofos tipograficos y acentos`() {
        assertTrue(AnswerChecker.matches("I’m José", listOf("I'm Jose")))
    }

    @Test
    fun `respuestas incorrectas no coinciden`() {
        assertFalse(AnswerChecker.matches("She work from home", listOf("She works from home")))
        assertFalse(AnswerChecker.matches("", listOf("anything")))
    }

    @Test
    fun `comparacion perfecta`() {
        val c = AnswerChecker.compare("I'm looking for my keys.", "I am looking for my keys")
        assertEquals(100, c.score)
        assertTrue(c.perfect)
    }

    @Test
    fun `comparacion marca palabras faltantes y extra`() {
        val c = AnswerChecker.compare("She works from home", "she work from the home")
        assertEquals(listOf(true, false, true, true), c.target.map { it.ok })
        assertEquals(listOf("work", "the"), c.extra)
        assertTrue(c.score in 1..99)
    }

    @Test
    fun `fichas para ordenar conservan la oracion`() {
        val t = AnswerChecker.orderTokens("I'm from Guatemala.")
        assertEquals(listOf("I'm", "from", "Guatemala."), t)
    }
}
