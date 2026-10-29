package com.denil.profe

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.denil.profe.data.curriculum.CourseUnit
import com.denil.profe.data.curriculum.CurriculumParser
import com.denil.profe.data.curriculum.Exercise
import com.denil.profe.domain.Cefr
import com.denil.profe.ui.components.LessonBody
import com.denil.profe.ui.screens.lesson.ExerciseView
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Dibuja la explicación y TODOS los ejercicios de cada nivel para detectar contenido que truene la UI. */
@RunWith(AndroidJUnit4::class)
class ContentRenderTest {

    @get:Rule
    val rule = createComposeRule()

    private val curriculum by lazy {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        CurriculumParser.parseCurriculum(ctx.assets.open("curriculum/curriculum.json").bufferedReader().use { it.readText() })
    }

    private sealed interface Item {
        data class Body(val unit: CourseUnit) : Item
        data class Ex(val unit: CourseUnit, val exercise: Exercise) : Item
    }

    private fun renderLevel(level: Cefr) {
        val units = curriculum.units.filter { it.level == level }
        assertTrue(units.isNotEmpty())
        val items: List<Item> = units.flatMap { u -> listOf<Item>(Item.Body(u)) + u.exercises.map { Item.Ex(u, it) } }
        rule.setContent {
            MaterialTheme {
                LazyColumn(Modifier.testTag("list")) {
                    items(items) { item ->
                        Column(Modifier.padding(8.dp)) {
                            when (item) {
                                is Item.Body -> {
                                    Text(item.unit.title)
                                    item.unit.sections.forEach { LessonBody(it.body, onSpeak = {}) }
                                }
                                is Item.Ex -> ExerciseView(item.exercise, onSpeak = { _, _ -> }, onFinished = {})
                            }
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
        for (i in items.indices step 3) {
            rule.onNodeWithTag("list").performScrollToIndex(i)
        }
        rule.onNodeWithTag("list").performScrollToIndex(items.lastIndex)
        rule.waitForIdle()
    }

    @Test fun a1() = renderLevel(Cefr.A1)
    @Test fun a2() = renderLevel(Cefr.A2)
    @Test fun b1() = renderLevel(Cefr.B1)
    @Test fun b2() = renderLevel(Cefr.B2)
    @Test fun c1() = renderLevel(Cefr.C1)
    @Test fun c2() = renderLevel(Cefr.C2)
}
