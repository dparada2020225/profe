package com.denil.profe

import com.denil.profe.domain.Cefr
import com.denil.profe.domain.PlacementEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PlacementEngineTest {

    private fun PlacementEngine.answerAll(vararg pattern: Boolean) = pattern.forEach { answer(it) }

    @Test
    fun `todo correcto llega a C2`() {
        val e = PlacementEngine()
        var guard = 0
        while (!e.finished && guard++ < 100) e.answer(true)
        assertTrue(e.finished)
        assertEquals(Cefr.C2, e.demonstratedLevel())
        assertEquals(Cefr.C2, e.recommendedLevel())
        assertEquals(listOf(Cefr.A2, Cefr.B1, Cefr.B2, Cefr.C1, Cefr.C2), e.blocks.map { it.level })
    }

    @Test
    fun `todo incorrecto prueba A1 y recomienda A1`() {
        val e = PlacementEngine()
        var guard = 0
        while (!e.finished && guard++ < 100) e.answer(false)
        assertEquals(listOf(Cefr.A2, Cefr.A1), e.blocks.map { it.level })
        assertNull(e.demonstratedLevel())
        assertEquals(Cefr.A1, e.recommendedLevel())
    }

    @Test
    fun `aprueba A2 y B1, falla B2`() {
        val e = PlacementEngine()
        e.answerAll(true, true, true, true)           // A2 aprobado (corte anticipado)
        assertEquals(Cefr.B1, e.current)
        e.answerAll(true, false, true, true, true)    // B1 aprobado
        assertEquals(Cefr.B2, e.current)
        e.answerAll(false, false)                     // B2: ya no puede aprobar
        assertTrue(e.finished)
        assertEquals(Cefr.B1, e.demonstratedLevel())
        assertEquals(Cefr.B2, e.recommendedLevel())
    }

    @Test
    fun `falla A2 pero aprueba A1`() {
        val e = PlacementEngine()
        e.answerAll(false, false)
        assertEquals(Cefr.A1, e.current)
        e.answerAll(true, true, true, true)
        assertTrue(e.finished)
        assertEquals(Cefr.A1, e.demonstratedLevel())
        assertEquals(Cefr.A2, e.recommendedLevel())
    }
}
