package com.denil.profe

import com.denil.profe.domain.CardStatus
import com.denil.profe.domain.Rating
import com.denil.profe.domain.Sm2
import com.denil.profe.domain.Sm2Result
import com.denil.profe.domain.Sm2State
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class Sm2Test {

    private val fresh = Sm2State()

    @Test
    fun `tarjeta nueva con Bien pasa a 1 dia`() {
        val r = Sm2.review(fresh, Rating.GOOD)
        assertEquals(1, r.state.intervalDays)
        assertEquals(1, r.state.repetitions)
        assertEquals(Sm2.DEFAULT_EASE, r.state.easeFactor, 1e-9)
        assertEquals(1L * Sm2Result.MINUTES_PER_DAY, r.nextIntervalMinutes)
    }

    @Test
    fun `segunda vez Bien pasa a 6 dias`() {
        val first = Sm2.review(fresh, Rating.GOOD).state
        val second = Sm2.review(first, Rating.GOOD)
        assertEquals(6, second.state.intervalDays)
        assertEquals(2, second.state.repetitions)
    }

    @Test
    fun `tercera vez Bien multiplica por el factor de facilidad`() {
        var s = fresh
        repeat(2) { s = Sm2.review(s, Rating.GOOD).state }
        val third = Sm2.review(s, Rating.GOOD)
        // 6 * 2.5 = 15
        assertEquals(15, third.state.intervalDays)
        assertEquals(3, third.state.repetitions)
    }

    @Test
    fun `Otra vez reinicia repeticiones, baja facilidad y programa en 10 minutos`() {
        var s = fresh
        repeat(3) { s = Sm2.review(s, Rating.GOOD).state }
        val r = Sm2.review(s, Rating.AGAIN)
        assertEquals(0, r.state.repetitions)
        assertEquals(0, r.state.intervalDays)
        assertEquals(1, r.state.lapses)
        assertEquals(Sm2.RELEARN_MINUTES, r.nextIntervalMinutes)
        assertTrue(r.state.easeFactor < s.easeFactor)
    }

    @Test
    fun `factor de facilidad nunca baja de 1_3`() {
        var s = fresh
        repeat(20) { s = Sm2.review(s, Rating.AGAIN).state }
        assertEquals(Sm2.MIN_EASE, s.easeFactor, 1e-9)
    }

    @Test
    fun `Facil sube la facilidad y da intervalo mayor que Bien`() {
        var s = fresh
        repeat(2) { s = Sm2.review(s, Rating.GOOD).state }
        val good = Sm2.review(s, Rating.GOOD)
        val easy = Sm2.review(s, Rating.EASY)
        assertTrue(easy.state.intervalDays > good.state.intervalDays)
        assertTrue(easy.state.easeFactor > good.state.easeFactor)
        assertEquals(s.easeFactor + 0.1, easy.state.easeFactor, 1e-9)
    }

    @Test
    fun `Dificil baja la facilidad y crece menos que Bien`() {
        var s = fresh
        repeat(2) { s = Sm2.review(s, Rating.GOOD).state }
        val hard = Sm2.review(s, Rating.HARD)
        val good = Sm2.review(s, Rating.GOOD)
        assertTrue(hard.state.intervalDays > s.intervalDays)
        assertTrue(hard.state.intervalDays < good.state.intervalDays)
        assertEquals(s.easeFactor - 0.14, hard.state.easeFactor, 1e-9)
    }

    @Test
    fun `el intervalo siempre crece al menos un dia con Bien`() {
        var s = Sm2State(easeFactor = 1.3, intervalDays = 3, repetitions = 5)
        val r = Sm2.review(s, Rating.GOOD)
        assertTrue(r.state.intervalDays >= 4)
    }

    @Test
    fun `preview devuelve los cuatro botones`() {
        val p = Sm2.preview(fresh)
        assertEquals(4, p.size)
        assertEquals(Sm2.RELEARN_MINUTES, p.getValue(Rating.AGAIN).nextIntervalMinutes)
        assertEquals(1, p.getValue(Rating.HARD).nextIntervalDays)
        assertEquals(1, p.getValue(Rating.GOOD).nextIntervalDays)
        assertEquals(4, p.getValue(Rating.EASY).nextIntervalDays)
    }

    @Test
    fun `estado segun intervalo`() {
        assertEquals(CardStatus.NEW, Sm2.statusFor(fresh, everReviewed = false))
        assertEquals(CardStatus.LEARNING, Sm2.statusFor(Sm2State(intervalDays = 6, repetitions = 2), everReviewed = true))
        assertEquals(CardStatus.MATURE, Sm2.statusFor(Sm2State(intervalDays = 21, repetitions = 4), everReviewed = true))
    }

    @Test
    fun `formato de intervalos en espanol`() {
        assertEquals("10 min", Sm2.formatInterval(10))
        assertEquals("1 d", Sm2.formatInterval(Sm2Result.MINUTES_PER_DAY))
        assertEquals("15 d", Sm2.formatInterval(15 * Sm2Result.MINUTES_PER_DAY))
        assertEquals("2 m", Sm2.formatInterval(60 * Sm2Result.MINUTES_PER_DAY))
        assertEquals("1 a", Sm2.formatInterval(365 * Sm2Result.MINUTES_PER_DAY))
    }
}
