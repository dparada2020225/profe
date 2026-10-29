package com.denil.profe.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.denil.profe.data.curriculum.CourseUnit
import com.denil.profe.data.curriculum.CurriculumRepository
import com.denil.profe.data.prefs.UserPreferences
import com.denil.profe.data.prefs.UserSettings
import com.denil.profe.data.repository.LearningRepository
import com.denil.profe.data.repository.StudyRepository
import com.denil.profe.domain.StudyStats
import com.denil.profe.ui.components.LevelBadge
import com.denil.profe.ui.components.ProfeBottomBar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

data class HomeUiState(
    val settings: UserSettings = UserSettings(),
    val streak: Int = 0,
    val xpToday: Int = 0,
    val totalXp: Int = 0,
    val nextUnit: CourseUnit? = null,
    val completedUnits: Int = 0,
    val totalUnits: Int = 0,
    val dueCards: Int = 0,
    val newCards: Int = 0,
    val loaded: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    prefs: UserPreferences,
    learning: LearningRepository,
    study: StudyRepository,
    curriculumRepo: CurriculumRepository
) : ViewModel() {

    private val progressInfo = combine(prefs.settings, learning.observeProgress()) { s, p ->
        val curriculum = curriculumRepo.curriculum()
        Triple(learning.nextUnit(s.level, p), p.values.count { it.completed }, curriculum.units.size)
    }

    private val stats = combine(learning.observeStreak(), learning.observeXpToday(), learning.observeTotalXp()) { a, b, c ->
        Triple(a, b, c)
    }

    private val decks = study.observeDeckSummaries(StudyStats.endOfDay(LocalDate.now())).map { list ->
        list.sumOf { it.dueCards } to list.sumOf { it.newCards }
    }

    val state: StateFlow<HomeUiState> = combine(prefs.settings, progressInfo, stats, decks) { s, p, st, d ->
        HomeUiState(
            settings = s, streak = st.first, xpToday = st.second, totalXp = st.third,
            nextUnit = p.first, completedUnits = p.second, totalUnits = p.third,
            dueCards = d.first, newCards = d.second, loaded = true
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    currentRoute: String?,
    onNavigateTopLevel: (String) -> Unit,
    onOpenLesson: (String) -> Unit,
    onPlacement: () -> Unit,
    onStudyVocab: () -> Unit,
    onOpenRoute: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val s = state.settings
    val hour = LocalTime.now().hour
    val greeting = when (hour) { in 5..11 -> "Buenos días"; in 12..18 -> "Buenas tardes"; else -> "Buenas noches" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (s.name.isNotBlank()) "$greeting, ${s.name}" else greeting) },
                actions = {
                    IconButton(onClick = { onOpenRoute(com.denil.profe.ui.navigation.Routes.STATS) }) {
                        Icon(Icons.Filled.BarChart, contentDescription = "Estadísticas")
                    }
                }
            )
        },
        bottomBar = { ProfeBottomBar(currentRoute, onNavigateTopLevel) }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Racha y meta diaria
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.LocalFireDepartment, contentDescription = null, tint = Color(0xFFFF7043))
                        Text(
                            "${state.streak} ${if (state.streak == 1) "día" else "días"} de racha",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(start = 6.dp).weight(1f)
                        )
                        LevelBadge(s.level.name)
                    }
                    val goal = s.dailyGoalXp.coerceAtLeast(1)
                    LinearProgressIndicator(
                        progress = { (state.xpToday.toFloat() / goal).coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        if (state.xpToday >= goal) "¡Meta del día cumplida! ${state.xpToday} XP hoy · ${state.totalXp} XP en total"
                        else "${state.xpToday} / $goal XP hoy · ${state.totalXp} XP en total",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (state.loaded && !s.placementDone) {
                Card(
                    Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Empecemos por tu nivel", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(
                            "Haz el examen de ubicación (5–10 minutos). Mide de A1 a C2 y te pone en la lección correcta.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Button(onClick = onPlacement) {
                            Icon(Icons.Filled.Quiz, contentDescription = null)
                            Text("Hacer el examen", modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            }

            // Continuar el curso
            state.nextUnit?.let { unit ->
                Card(
                    Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Tu siguiente lección", style = MaterialTheme.typography.labelLarge)
                        Text("${unit.level.name} · ${unit.number}. ${unit.title}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(unit.subtitle, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "Llevas ${state.completedUnits} de ${state.totalUnits} lecciones",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Button(onClick = { onOpenLesson(unit.id) }) {
                            Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null)
                            Text("Continuar", modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            }

            // Vocabulario pendiente
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Style, contentDescription = null)
                    Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                        Text("Repaso de palabras", style = MaterialTheme.typography.titleMedium)
                        Text(
                            when {
                                state.dueCards > 0 -> "${state.dueCards} tarjetas por repasar hoy"
                                state.newCards > 0 -> "Al día · ${state.newCards} palabras nuevas disponibles"
                                else -> "Abre una lección para sumar palabras"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    FilledTonalButton(onClick = onStudyVocab) { Text("Repasar") }
                }
            }

            Text("Práctica rápida", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                QuickAction("Tutor", Icons.AutoMirrored.Filled.Chat, Modifier.weight(1f)) { onOpenRoute(com.denil.profe.ui.navigation.Routes.TUTOR_PICK) }
                QuickAction("Dictado", Icons.Filled.Headphones, Modifier.weight(1f)) { onOpenRoute(com.denil.profe.ui.navigation.Routes.DICTATION) }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                QuickAction("Pronunciar", Icons.Filled.Mic, Modifier.weight(1f)) { onOpenRoute(com.denil.profe.ui.navigation.Routes.PRONUNCIATION) }
                QuickAction("Escribir", Icons.Filled.Edit, Modifier.weight(1f)) { onOpenRoute(com.denil.profe.ui.navigation.Routes.WRITING) }
            }
        }
    }
}

@Composable
private fun QuickAction(label: String, icon: ImageVector, modifier: Modifier, onClick: () -> Unit) {
    OutlinedButton(onClick = onClick, modifier = modifier) {
        Icon(icon, contentDescription = null)
        Text(label, modifier = Modifier.padding(start = 8.dp))
    }
}
