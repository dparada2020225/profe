package com.denil.profe

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Recorre la app como lo haría el usuario: examen de ubicación, lección, todas las
 * pestañas y las pantallas de práctica. Falla si alguna pantalla truena o no aparece.
 */
@RunWith(AndroidJUnit4::class)
class SmokeTest {

    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    private fun waitFor(text: String, substring: Boolean = false, timeout: Long = 15_000) {
        rule.waitUntil(timeout) {
            rule.onAllNodesWithText(text, substring = substring, useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun click(text: String, substring: Boolean = false) {
        waitFor(text, substring)
        val matcher: SemanticsMatcher = hasText(text, substring = substring) and hasClickAction()
        val nodes = rule.onAllNodes(matcher)
        if (nodes.fetchSemanticsNodes().isNotEmpty()) {
            nodes.onFirst().performScrollToSafe().performClick()
        } else {
            rule.onAllNodesWithText(text, substring = substring, useUnmergedTree = true).onFirst().performClick()
        }
        rule.waitForIdle()
    }

    private fun androidx.compose.ui.test.SemanticsNodeInteraction.performScrollToSafe() =
        try { performScrollTo() } catch (e: Throwable) { this }

    private fun back() {
        Espresso.pressBack()
        rule.waitForIdle()
    }

    private fun tab(label: String) = click(label)

    @Test
    fun recorridoCompleto() {
        // Inicio
        waitFor("días de racha", substring = true)

        // Examen de ubicación: respondiendo "No sé" siempre debe terminar en A1.
        tab("Ajustes")
        click("Hacer el examen de ubicación")
        click("Empezar el examen")
        var guard = 0
        while (rule.onAllNodesWithText("Tu nivel demostrado").fetchSemanticsNodes().isEmpty() && guard++ < 40) {
            click("No sé")
        }
        waitFor("Tu nivel demostrado")
        waitFor("Te recomiendo estudiar el nivel A1", substring = true)
        click("Ir a mi curso")

        // Lección desde Inicio
        tab("Inicio")
        click("Continuar")
        waitFor("Hola, soy", substring = true)
        click("Palabras")
        waitFor("Vocabulario A1", substring = true)
        click("Practica")
        waitFor("1 de", substring = true)
        click("Aprende")
        back()

        // Curso
        tab("Curso")
        waitFor("Curso de inglés")
        click("Hola, soy…", substring = true)
        waitFor("Aprende")
        back()

        // Práctica
        tab("Practicar")
        click("Dictado")
        waitFor("Escuchar")
        back()
        click("Pronunciación")
        waitFor("Modelo")
        back()
        click("Escritura")
        waitFor("Revisar mi texto")
        back()
        click("Diccionario")
        waitFor("Palabra en inglés")
        back()
        click("Tutor con IA")
        click("Conversación libre")
        waitFor("I'm your English tutor", substring = true)
        back()
        back()

        // Palabras (mazos): la lección abierta agregó su vocabulario
        tab("Palabras")
        waitFor("Vocabulario A1", substring = true)

        // Ajustes
        tab("Ajustes")
        waitFor("Tu perfil")
        waitFor("Tutor con IA (Groq, gratis)")

        // Estadísticas desde Inicio
        tab("Inicio")
        rule.onAllNodes(androidx.compose.ui.test.hasContentDescription("Estadísticas")).onFirst().performClick()
        rule.waitForIdle()
    }
}
