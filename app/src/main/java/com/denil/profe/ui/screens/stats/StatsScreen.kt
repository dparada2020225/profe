package com.denil.profe.ui.screens.stats

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.denil.profe.data.repository.StudyRepository
import com.denil.profe.domain.CardStatus
import com.denil.profe.domain.StudyStats
import com.denil.profe.ui.components.ProfeBottomBar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

data class StatsUiState(
    val loaded: Boolean = false,
    val newCount: Int = 0,
    val learningCount: Int = 0,
    val matureCount: Int = 0,
    val reviewsToday: Int = 0,
    val totalReviews: Int = 0,
    val streak: Int = 0,
    val reviewsPerDay: List<Pair<LocalDate, Int>> = emptyList(),
    val forecast: List<Pair<LocalDate, Int>> = emptyList()
) {
    val total: Int get() = newCount + learningCount + matureCount
}

@HiltViewModel
class StatsViewModel @Inject constructor(repository: StudyRepository) : ViewModel() {

    val state: StateFlow<StatsUiState> = combine(
        repository.observeStatusCounts(),
        repository.observeAllReviewTimestamps(),
        repository.observeAllCards()
    ) { statusCounts, timestamps, cards ->
        val today = LocalDate.now()
        val counts = statusCounts.associate { it.status to it.count }
        val perDay = StudyStats.reviewsPerDay(timestamps, today, days = 14)
        StatsUiState(
            loaded = true,
            newCount = counts[CardStatus.NEW.name] ?: 0,
            learningCount = counts[CardStatus.LEARNING.name] ?: 0,
            matureCount = counts[CardStatus.MATURE.name] ?: 0,
            reviewsToday = perDay.lastOrNull()?.second ?: 0,
            totalReviews = timestamps.size,
            streak = StudyStats.streak(timestamps, today),
            reviewsPerDay = perDay,
            forecast = StudyStats.forecast(cards.map { it.toSchedulable() }, today, days = 7)
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StatsUiState())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    currentRoute: String?,
    onNavigateTopLevel: (String) -> Unit,
    viewModel: StatsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Estadísticas") }) },
        bottomBar = { ProfeBottomBar(currentRoute, onNavigateTopLevel) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                StatTile("Racha", "${state.streak} ${if (state.streak == 1) "día" else "días"}", Modifier.weight(1f))
                StatTile("Hoy", "${state.reviewsToday} repasos", Modifier.weight(1f))
                StatTile("Total", "${state.totalReviews} repasos", Modifier.weight(1f))
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Tarjetas por estado", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(12.dp))
                    StatusBar(state)
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        LegendItem(NewColor, "Nuevas", state.newCount)
                        LegendItem(LearningColor, "Aprendiendo", state.learningCount)
                        LegendItem(MatureColor, "Maduras", state.matureCount)
                    }
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Repasos por día (últimos 14 días)", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(12.dp))
                    BarChart(
                        values = state.reviewsPerDay.map { it.second },
                        labels = state.reviewsPerDay.map { it.first.dayOfMonth.toString() },
                        barColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth().height(180.dp)
                    )
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Pronóstico: repasos en los próximos 7 días", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(12.dp))
                    BarChart(
                        values = state.forecast.map { it.second },
                        labels = state.forecast.map { dayLabel(it.first) },
                        barColor = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.fillMaxWidth().height(180.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Las tarjetas atrasadas se cuentan en el día de hoy.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private val NewColor = Color(0xFF1E88E5)
private val LearningColor = Color(0xFFFB8C00)
private val MatureColor = Color(0xFF43A047)

private fun dayLabel(date: LocalDate): String {
    val today = LocalDate.now()
    return when (date) {
        today -> "Hoy"
        today.plusDays(1) -> "Mañ"
        else -> date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("es")).replace(".", "").take(3)
    }
}

@Composable
private fun StatTile(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String, count: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Canvas(modifier = Modifier.size(12.dp)) {
            drawCircle(color = color)
        }
        Spacer(Modifier.width(6.dp))
        Text("$label: $count", style = MaterialTheme.typography.bodySmall)
    }
}

/** Barra apilada con la proporción de tarjetas por estado. */
@Composable
private fun StatusBar(state: StatsUiState) {
    val track = MaterialTheme.colorScheme.surfaceVariant
    Canvas(modifier = Modifier.fillMaxWidth().height(20.dp)) {
        val total = state.total.coerceAtLeast(1).toFloat()
        val radius = CornerRadius(size.height / 2, size.height / 2)
        drawRoundRect(color = track, cornerRadius = radius)
        var x = 0f
        listOf(
            NewColor to state.newCount,
            LearningColor to state.learningCount,
            MatureColor to state.matureCount
        ).forEach { (color, count) ->
            val w = size.width * (count / total)
            if (w > 0f) {
                drawRoundRect(color = color, topLeft = Offset(x, 0f), size = Size(w, size.height), cornerRadius = radius)
                x += w
            }
        }
    }
}

/** Gráfica de barras dibujada con Compose Canvas. */
@Composable
private fun BarChart(values: List<Int>, labels: List<String>, barColor: Color, modifier: Modifier = Modifier) {
    val axisColor = MaterialTheme.colorScheme.outlineVariant
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant
    val textMeasurer = rememberTextMeasurer()
    val labelStyle = MaterialTheme.typography.labelSmall.copy(color = textColor)

    Canvas(modifier = modifier) {
        if (values.isEmpty()) return@Canvas
        val maxValue = (values.maxOrNull() ?: 0).coerceAtLeast(1)
        val labelHeight = 28f * density
        val valueHeight = 18f * density
        val chartHeight = size.height - labelHeight - valueHeight
        val slot = size.width / values.size
        val barWidth = slot * 0.6f
        val baseY = valueHeight + chartHeight

        // Eje base
        drawLine(color = axisColor, start = Offset(0f, baseY), end = Offset(size.width, baseY), strokeWidth = 1.5f * density)
        // Línea guía a la mitad
        drawLine(
            color = axisColor,
            start = Offset(0f, valueHeight + chartHeight / 2),
            end = Offset(size.width, valueHeight + chartHeight / 2),
            strokeWidth = 1f * density
        )

        values.forEachIndexed { i, v ->
            val h = chartHeight * (v.toFloat() / maxValue)
            val left = slot * i + (slot - barWidth) / 2
            val top = baseY - h
            if (v > 0) {
                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(left, top),
                    size = Size(barWidth, h),
                    cornerRadius = CornerRadius(4f * density, 4f * density)
                )
                val valueLayout = textMeasurer.measure(v.toString(), labelStyle)
                drawText(
                    textLayoutResult = valueLayout,
                    topLeft = Offset(left + barWidth / 2 - valueLayout.size.width / 2, top - valueLayout.size.height - 2f * density)
                )
            } else {
                drawRoundRect(
                    color = axisColor,
                    topLeft = Offset(left, baseY - 2f * density),
                    size = Size(barWidth, 2f * density),
                    cornerRadius = CornerRadius(1f * density, 1f * density),
                    style = Stroke(width = 1f)
                )
            }
            val label = labels.getOrNull(i) ?: ""
            val labelLayout = textMeasurer.measure(label, labelStyle)
            drawText(
                textLayoutResult = labelLayout,
                topLeft = Offset(left + barWidth / 2 - labelLayout.size.width / 2, baseY + 6f * density)
            )
        }
    }
}
