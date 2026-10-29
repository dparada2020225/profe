package com.denil.profe.ui.screens.practice

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.denil.profe.data.curriculum.Exercise
import com.denil.profe.data.curriculum.ExerciseType
import com.denil.profe.data.local.ActivityKind
import com.denil.profe.data.prefs.UserPreferences
import com.denil.profe.data.repository.LearningRepository
import com.denil.profe.tts.Speaker
import com.denil.profe.ui.components.ProfeBottomBar
import com.denil.profe.ui.navigation.Routes
import com.denil.profe.ui.screens.lesson.ExerciseView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private data class PracticeItem(val title: String, val subtitle: String, val icon: ImageVector, val route: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeScreen(currentRoute: String?, onNavigateTopLevel: (String) -> Unit, onOpen: (String) -> Unit) {
    val items = listOf(
        PracticeItem("Tutor con IA", "Conversa por escrito o por voz en situaciones reales; te corrige al momento.", Icons.AutoMirrored.Filled.Chat, Routes.TUTOR_PICK),
        PracticeItem("Dictado", "Escucha frases de tu nivel y escríbelas.", Icons.Filled.Headphones, Routes.DICTATION),
        PracticeItem("Pronunciación", "Lee en voz alta; el micrófono te dice qué entendió.", Icons.Filled.Mic, Routes.PRONUNCIATION),
        PracticeItem("Escritura", "Escribe un texto y recibe corrección (LanguageTool + IA).", Icons.Filled.Edit, Routes.WRITING),
        PracticeItem("Diccionario", "Definiciones, audio, ejemplos con traducción; guarda palabras.", Icons.AutoMirrored.Filled.MenuBook, Routes.DICTIONARY),
        PracticeItem("Repasar palabras", "Tus tarjetas con repetición espaciada (SM-2).", Icons.Filled.Style, Routes.DECKS),
        PracticeItem("Examen de ubicación", "Vuelve a medir tu nivel de A1 a C2.", Icons.Filled.Quiz, Routes.PLACEMENT)
    )
    Scaffold(
        topBar = { TopAppBar(title = { Text("Practicar") }) },
        bottomBar = { ProfeBottomBar(currentRoute, onNavigateTopLevel) }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items.forEach { item ->
                Card(
                    Modifier.fillMaxWidth().clickable {
                        if (item.route == Routes.DECKS) onNavigateTopLevel(item.route) else onOpen(item.route)
                    }
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(item.icon, contentDescription = null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.primary)
                        Column(Modifier.padding(start = 14.dp)) {
                            Text(item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Text(item.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

// ---------- Sesión de dictado / pronunciación ----------

data class DrillUiState(
    val loading: Boolean = true,
    val exercises: List<Exercise> = emptyList(),
    val index: Int = 0,
    val correct: Int = 0,
    val counted: Int = 0,
    val finished: Boolean = false,
    val xp: Int = 0,
    val levelName: String = ""
)

@HiltViewModel
class DrillViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val learning: LearningRepository,
    private val prefs: UserPreferences,
    private val speaker: Speaker
) : ViewModel() {

    val mode: ExerciseType =
        if (savedStateHandle.get<String>("mode") == "speak") ExerciseType.SPEAK else ExerciseType.LISTEN

    private val _state = MutableStateFlow(DrillUiState())
    val state: StateFlow<DrillUiState> = _state

    init { load() }

    fun load() {
        viewModelScope.launch {
            val settings = prefs.current()
            speaker.baseRate = settings.speechRate
            val sentences = learning.practiceSentences(settings.level).shuffled().take(10)
            _state.value = DrillUiState(
                loading = false,
                levelName = settings.level.name,
                exercises = sentences.map { Exercise(type = mode, prompt = it.es, answers = listOf(it.en)) }
            )
        }
    }

    fun speak(text: String, slow: Boolean) { speaker.speak(text, slow) }

    fun onFinished(result: Boolean?) {
        val s = _state.value
        val correct = s.correct + if (result == true) 1 else 0
        val counted = s.counted + if (result != null) 1 else 0
        val next = s.index + 1
        if (next >= s.exercises.size) {
            val xp = correct * 2 + if (counted > 0) 5 else 0
            _state.update { it.copy(correct = correct, counted = counted, index = next, finished = true, xp = xp) }
            if (counted > 0) viewModelScope.launch {
                learning.addActivity(
                    if (mode == ExerciseType.SPEAK) ActivityKind.PRONUNCIATION else ActivityKind.DICTATION,
                    xp, "$correct/$counted"
                )
            }
        } else {
            _state.update { it.copy(correct = correct, counted = counted, index = next) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrillScreen(onBack: () -> Unit, viewModel: DrillViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val title = if (viewModel.mode == ExerciseType.SPEAK) "Pronunciación" else "Dictado"
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("$title · ${state.levelName}") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás") } }
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when {
                state.loading -> CircularProgressIndicator()
                state.finished -> {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(20.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Filled.EmojiEvents, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
                            Text("${state.correct} de ${state.counted}", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
                            Text("+${state.xp} XP", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                    Button(onClick = viewModel::load, modifier = Modifier.fillMaxWidth()) { Text("Otra ronda") }
                    OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Terminar") }
                }
                else -> {
                    val ex = state.exercises.getOrNull(state.index)
                    if (ex == null) {
                        Text("No hay frases para practicar todavía.")
                    } else {
                        LinearProgressIndicator(
                            progress = { state.index.toFloat() / state.exercises.size },
                            modifier = Modifier.fillMaxWidth()
                        )
                        key(state.exercises.hashCode(), state.index) {
                            ExerciseView(ex, onSpeak = viewModel::speak, onFinished = viewModel::onFinished)
                        }
                    }
                }
            }
        }
    }
}
