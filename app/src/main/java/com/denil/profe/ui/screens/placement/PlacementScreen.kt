package com.denil.profe.ui.screens.placement

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.denil.profe.data.curriculum.CurriculumRepository
import com.denil.profe.data.curriculum.PlacementQuestion
import com.denil.profe.data.local.ActivityKind
import com.denil.profe.data.prefs.UserPreferences
import com.denil.profe.data.repository.LearningRepository
import com.denil.profe.domain.Cefr
import com.denil.profe.domain.PlacementEngine
import com.denil.profe.ui.components.inlineMarkup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlacementUiState(
    val started: Boolean = false,
    val question: PlacementQuestion? = null,
    /** Opciones barajadas y el índice correcto dentro de ellas. */
    val options: List<String> = emptyList(),
    val correctIndex: Int = -1,
    val answered: Int = 0,
    val currentLevel: Cefr? = null,
    val finished: Boolean = false,
    val demonstrated: Cefr? = null,
    val recommended: Cefr = Cefr.A1,
    val blocks: List<PlacementEngine.BlockResult> = emptyList()
)

@HiltViewModel
class PlacementViewModel @Inject constructor(
    private val curriculumRepo: CurriculumRepository,
    private val prefs: UserPreferences,
    private val learning: LearningRepository
) : ViewModel() {

    private var engine = PlacementEngine()
    private var pool: Map<Cefr, ArrayDeque<PlacementQuestion>> = emptyMap()
    private val _state = MutableStateFlow(PlacementUiState())
    val state: StateFlow<PlacementUiState> = _state

    fun start() {
        viewModelScope.launch {
            engine = PlacementEngine()
            pool = curriculumRepo.placementQuestions().groupBy { it.level }
                .mapValues { (_, qs) -> ArrayDeque(qs.shuffled()) }
            _state.value = PlacementUiState(started = true)
            nextQuestion()
        }
    }

    private fun nextQuestion() {
        val level = engine.current
        if (level == null) { finish(); return }
        val q = pool[level]?.removeFirstOrNull()
        if (q == null) {
            // Sin preguntas para este nivel: se considera respondido con lo que haya.
            while (!engine.finished && engine.current == level) engine.answer(false)
            nextQuestion(); return
        }
        val order = q.options.indices.shuffled()
        _state.update {
            it.copy(
                question = q,
                options = order.map { i -> q.options[i] },
                correctIndex = order.indexOf(q.answer),
                currentLevel = level
            )
        }
    }

    fun answer(index: Int) {
        val s = _state.value
        if (s.question == null || s.finished) return
        engine.answer(index == s.correctIndex)
        _state.update { it.copy(answered = it.answered + 1) }
        nextQuestion()
    }

    /** "No sé" cuenta como incorrecta (mejor que adivinar). */
    fun dontKnow() = answer(-1)

    private fun finish() {
        val demonstrated = engine.demonstratedLevel()
        val recommended = engine.recommendedLevel()
        _state.update {
            it.copy(finished = true, question = null, demonstrated = demonstrated, recommended = recommended, blocks = engine.blocks)
        }
        viewModelScope.launch {
            prefs.setPlacement(demonstrated, recommended)
            learning.markLevelsBelowAsKnown(recommended)
            learning.addActivity(ActivityKind.PLACEMENT, 20, recommended.name)
        }
    }

    fun skip(level: Cefr) {
        viewModelScope.launch { prefs.skipPlacement(level) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlacementScreen(onDone: () -> Unit, viewModel: PlacementViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Examen de ubicación") },
                navigationIcon = { IconButton(onClick = onDone) { Icon(Icons.Filled.Close, contentDescription = "Cerrar") } }
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            when {
                !state.started -> Intro(onStart = viewModel::start, onSkip = { viewModel.skip(it); onDone() })
                state.finished -> Result(state, onDone)
                else -> state.question?.let { q ->
                    LinearProgressIndicator(
                        progress = { (state.answered / 30f).coerceAtMost(0.95f) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        "Pregunta ${state.answered + 1} · evaluando ${state.currentLevel?.name ?: ""}",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(inlineMarkup(q.prompt), style = MaterialTheme.typography.headlineSmall)
                    state.options.forEachIndexed { i, option ->
                        OutlinedButton(onClick = { viewModel.answer(i) }, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)) {
                            Text(option, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                    TextButton(onClick = viewModel::dontKnow) { Text("No sé") }
                    Text(
                        "No hay retroalimentación durante el examen. Si no sabes, elige «No sé»: así el resultado es más preciso.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun Intro(onStart: () -> Unit, onSkip: (Cefr) -> Unit) {
    Text("¿Cuál es tu nivel de inglés?", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
    Text(
        "El examen es adaptativo: empieza en A2 y sube (o baja) según tus respuestas, hasta C2. " +
            "Son bloques de 5 preguntas; toma entre 5 y 10 minutos.",
        style = MaterialTheme.typography.bodyLarge
    )
    Text(
        "Al terminar, las lecciones de los niveles que ya dominas quedan marcadas y te llevo directo a lo que te toca.",
        style = MaterialTheme.typography.bodyMedium
    )
    Button(onClick = onStart, modifier = Modifier.fillMaxWidth()) { Text("Empezar el examen") }
    Text("¿Prefieres elegir tu nivel?", style = MaterialTheme.typography.titleSmall)
    Cefr.entries.chunked(3).forEach { row ->
        androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            row.forEach { lvl ->
                OutlinedButton(onClick = { onSkip(lvl) }, modifier = Modifier.weight(1f)) { Text(lvl.name) }
            }
        }
    }
}

@Composable
private fun Result(state: PlacementUiState, onDone: () -> Unit) {
    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(Modifier.padding(20.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Tu nivel demostrado", style = MaterialTheme.typography.labelLarge)
            Text(state.demonstrated?.name ?: "Pre-A1", style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Bold)
            Text(state.demonstrated?.label ?: "Empezamos desde cero", style = MaterialTheme.typography.titleMedium)
        }
    }
    Text(
        "Te recomiendo estudiar el nivel ${state.recommended.name} (${state.recommended.label}).",
        style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Start
    )
    state.blocks.forEach { b ->
        Text(
            "${b.level.name}: ${b.correct} de ${b.total} ${if (b.correct * 5 >= b.total * 4) "✔" else "✘"}",
            style = MaterialTheme.typography.bodyMedium
        )
    }
    Text(
        "Puedes repetir el examen cuando quieras desde Ajustes para medir tu avance.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Button(onClick = onDone, modifier = Modifier.fillMaxWidth()) { Text("Ir a mi curso") }
}
