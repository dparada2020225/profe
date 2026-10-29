package com.denil.profe.ui.screens.lesson

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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.denil.profe.data.curriculum.CourseUnit
import com.denil.profe.data.curriculum.CurriculumRepository
import com.denil.profe.data.curriculum.Exercise
import com.denil.profe.data.prefs.UserPreferences
import com.denil.profe.data.remote.AiTeacher
import com.denil.profe.data.repository.LearningRepository
import com.denil.profe.tts.Speaker
import com.denil.profe.ui.components.LessonBody
import com.denil.profe.ui.components.SpeakIcon
import com.denil.profe.ui.components.inlineMarkup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class LessonTab { LEARN, VOCAB, PRACTICE }

data class QA(val question: String, val answer: String?, val error: String? = null)

data class LessonUiState(
    val unit: CourseUnit? = null,
    val nextUnitId: String? = null,
    val tab: LessonTab = LessonTab.LEARN,
    val exercises: List<Exercise> = emptyList(),
    val index: Int = 0,
    val correct: Int = 0,
    val counted: Int = 0,
    val finished: Boolean = false,
    val score: Int = 0,
    val xpEarned: Int = 0,
    val bestScore: Int = 0,
    val aiAvailable: Boolean = false,
    val generating: Boolean = false,
    val generateError: String? = null,
    val qa: List<QA> = emptyList()
)

@HiltViewModel
class LessonViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val curriculumRepo: CurriculumRepository,
    private val learning: LearningRepository,
    private val prefs: UserPreferences,
    private val ai: AiTeacher,
    private val speaker: Speaker
) : ViewModel() {

    private val unitId: String = savedStateHandle.get<String>("unitId").orEmpty()
    private val _state = MutableStateFlow(LessonUiState())
    val state: StateFlow<LessonUiState> = _state

    init {
        viewModelScope.launch {
            val curriculum = curriculumRepo.curriculum()
            val unit = curriculum.unit(unitId) ?: return@launch
            speaker.baseRate = prefs.current().speechRate
            learning.openUnit(unit)
            _state.update {
                it.copy(
                    unit = unit,
                    nextUnitId = curriculum.next(unit.id)?.id,
                    exercises = arrange(unit.exercises),
                    bestScore = learning.progress(unit.id)?.bestScore ?: 0,
                    aiAvailable = ai.isAvailable()
                )
            }
        }
    }

    /** Mezcla los ejercicios pero deja pronunciación al final y alterna tipos. */
    private fun arrange(list: List<Exercise>): List<Exercise> {
        val (speak, rest) = list.partition { it.type == com.denil.profe.data.curriculum.ExerciseType.SPEAK }
        return rest.shuffled() + speak
    }

    fun setTab(tab: LessonTab) = _state.update { it.copy(tab = tab) }

    fun speak(text: String, slow: Boolean = false) {
        speaker.speak(text, slow)
    }

    fun onExerciseFinished(result: Boolean?) {
        val s = _state.value
        val correct = s.correct + if (result == true) 1 else 0
        val counted = s.counted + if (result != null) 1 else 0
        val nextIndex = s.index + 1
        if (nextIndex >= s.exercises.size) {
            val score = if (counted == 0) 0 else correct * 100 / counted
            _state.update { it.copy(correct = correct, counted = counted, index = nextIndex, finished = true, score = score) }
            val unit = s.unit ?: return
            viewModelScope.launch {
                val xp = if (counted > 0) learning.recordLessonResult(unit, score) else 0
                _state.update { it.copy(xpEarned = xp, bestScore = maxOf(it.bestScore, score)) }
            }
        } else {
            _state.update { it.copy(correct = correct, counted = counted, index = nextIndex) }
        }
    }

    fun restart(newExercises: List<Exercise>? = null) {
        val unit = _state.value.unit ?: return
        _state.update {
            it.copy(
                exercises = newExercises ?: arrange(unit.exercises),
                index = 0, correct = 0, counted = 0, finished = false, score = 0, xpEarned = 0,
                tab = LessonTab.PRACTICE, generateError = null
            )
        }
    }

    fun generateMore() {
        val unit = _state.value.unit ?: return
        _state.update { it.copy(generating = true, generateError = null) }
        viewModelScope.launch {
            try {
                val level = prefs.current().level
                val list = ai.extraExercises(level, unit)
                if (list.isEmpty()) _state.update { it.copy(generating = false, generateError = "La IA no generó ejercicios válidos. Intenta de nuevo.") }
                else {
                    _state.update { it.copy(generating = false) }
                    restart(list.shuffled())
                }
            } catch (e: Exception) {
                _state.update { it.copy(generating = false, generateError = e.message ?: "Error") }
            }
        }
    }

    suspend fun checkTranslation(exercise: Exercise, attempt: String): AiVerdict {
        val v = ai.checkTranslation(exercise.prompt, exercise.answers, attempt)
        return AiVerdict(v.correct, v.feedback, v.better)
    }

    fun ask(question: String) {
        val unit = _state.value.unit ?: return
        if (question.isBlank()) return
        val index = _state.value.qa.size
        _state.update { it.copy(qa = it.qa + QA(question, null)) }
        viewModelScope.launch {
            val updated = try {
                QA(question, ai.askAboutLesson(prefs.current().level, unit, question))
            } catch (e: Exception) {
                QA(question, null, e.message ?: "Error")
            }
            _state.update { s -> s.copy(qa = s.qa.toMutableList().also { if (index < it.size) it[index] = updated }) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonScreen(
    onBack: () -> Unit,
    onOpenLesson: (String) -> Unit,
    onOpenSettings: () -> Unit,
    viewModel: LessonViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val unit = state.unit
    var showAsk by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(unit?.let { "${it.level.name} · ${it.number}. ${it.title}" } ?: "", maxLines = 1)
                        if (unit != null) Text(unit.subtitle, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                    }
                },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás") } },
                actions = {
                    IconButton(onClick = { showAsk = true }) { Icon(Icons.Filled.QuestionAnswer, contentDescription = "Preguntarle al profe") }
                }
            )
        }
    ) { padding ->
        if (unit == null) {
            Column(Modifier.fillMaxSize().padding(padding), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }
        Column(Modifier.fillMaxSize().padding(padding)) {
            PrimaryTabRow(selectedTabIndex = state.tab.ordinal) {
                Tab(selected = state.tab == LessonTab.LEARN, onClick = { viewModel.setTab(LessonTab.LEARN) }, text = { Text("Aprende") })
                Tab(selected = state.tab == LessonTab.VOCAB, onClick = { viewModel.setTab(LessonTab.VOCAB) }, text = { Text("Palabras") })
                Tab(selected = state.tab == LessonTab.PRACTICE, onClick = { viewModel.setTab(LessonTab.PRACTICE) }, text = { Text("Practica") })
            }
            when (state.tab) {
                LessonTab.LEARN -> LearnTab(unit, onSpeak = { viewModel.speak(it) }, onPractice = { viewModel.setTab(LessonTab.VOCAB) })
                LessonTab.VOCAB -> VocabTab(unit, onSpeak = { viewModel.speak(it) }, onPractice = { viewModel.setTab(LessonTab.PRACTICE) })
                LessonTab.PRACTICE -> PracticeTab(
                    state = state,
                    viewModel = viewModel,
                    onOpenLesson = onOpenLesson,
                    onOpenSettings = onOpenSettings
                )
            }
        }
    }

    if (showAsk && unit != null) {
        AskSheet(
            state = state,
            onAsk = viewModel::ask,
            onOpenSettings = { showAsk = false; onOpenSettings() },
            onDismiss = { showAsk = false }
        )
    }
}

@Composable
private fun LearnTab(unit: CourseUnit, onSpeak: (String) -> Unit, onPractice: () -> Unit) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Text("🎯 ${unit.goal}", modifier = Modifier.padding(14.dp), style = MaterialTheme.typography.bodyLarge)
        }
        unit.sections.forEach { section ->
            Text(section.heading, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            LessonBody(section.body, onSpeak)
        }
        HorizontalDivider()
        Text("Ejemplos", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        unit.examples.forEach { ex ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(ex.en, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                    Text(ex.es, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                SpeakIcon(ex.en, onSpeak)
            }
        }
        Button(onClick = onPractice, modifier = Modifier.fillMaxWidth()) { Text("Ver las palabras") }
    }
}

@Composable
private fun VocabTab(unit: CourseUnit, onSpeak: (String) -> Unit, onPractice: () -> Unit) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            "Estas palabras ya están en tu mazo «Vocabulario ${unit.level.name}» para repasarlas con repetición espaciada.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        unit.vocab.forEach { v ->
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(v.en, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(v.es, style = MaterialTheme.typography.bodyMedium)
                        if (v.example.isNotBlank()) Text(v.example, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    SpeakIcon(if (v.example.isNotBlank()) "${v.en}. ${v.example}" else v.en, onSpeak)
                }
            }
        }
        Button(onClick = onPractice, modifier = Modifier.fillMaxWidth()) { Text("Empezar ejercicios") }
    }
}

@Composable
private fun PracticeTab(
    state: LessonUiState,
    viewModel: LessonViewModel,
    onOpenLesson: (String) -> Unit,
    onOpenSettings: () -> Unit
) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (state.finished) {
            ResultCard(state, viewModel, onOpenLesson, onOpenSettings)
            return@Column
        }
        val exercise = state.exercises.getOrNull(state.index) ?: return@Column
        LinearProgressIndicator(
            progress = { state.index.toFloat() / state.exercises.size.coerceAtLeast(1) },
            modifier = Modifier.fillMaxWidth()
        )
        Text("${state.index + 1} de ${state.exercises.size}", style = MaterialTheme.typography.labelMedium)
        key(state.exercises.hashCode(), state.index) {
            ExerciseView(
                exercise = exercise,
                onSpeak = { text, slow -> viewModel.speak(text, slow) },
                onFinished = viewModel::onExerciseFinished,
                aiCheck = if (state.aiAvailable) viewModel::checkTranslation else null
            )
        }
    }
}

@Composable
private fun ResultCard(
    state: LessonUiState,
    viewModel: LessonViewModel,
    onOpenLesson: (String) -> Unit,
    onOpenSettings: () -> Unit
) {
    val passed = state.score >= LearningRepository.PASS_SCORE
    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (passed) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(Modifier.padding(20.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Filled.EmojiEvents, contentDescription = null, modifier = Modifier.size(56.dp), tint = MaterialTheme.colorScheme.primary)
            Text("${state.score}%", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold)
            Text(
                if (passed) "¡Lección aprobada!" else "Necesitas 80 % para aprobar. ¡Otra vez!",
                style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center
            )
            Text("${state.correct} de ${state.counted} correctas · +${state.xpEarned} XP · mejor nota ${state.bestScore}%", style = MaterialTheme.typography.bodySmall)
        }
    }
    if (passed && state.nextUnitId != null) {
        Button(onClick = { onOpenLesson(state.nextUnitId) }, modifier = Modifier.fillMaxWidth()) { Text("Siguiente lección") }
    }
    OutlinedButton(onClick = { viewModel.restart() }, modifier = Modifier.fillMaxWidth()) { Text("Repetir ejercicios") }
    if (state.aiAvailable) {
        FilledTonalButton(onClick = viewModel::generateMore, enabled = !state.generating, modifier = Modifier.fillMaxWidth()) {
            if (state.generating) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
            else Icon(Icons.Filled.AutoAwesome, contentDescription = null)
            Text("Ejercicios nuevos con IA", modifier = Modifier.padding(start = 8.dp))
        }
    } else {
        OutlinedButton(onClick = onOpenSettings, modifier = Modifier.fillMaxWidth()) { Text("Activa la IA (Groq) para ejercicios ilimitados") }
    }
    state.generateError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AskSheet(state: LessonUiState, onAsk: (String) -> Unit, onOpenSettings: () -> Unit, onDismiss: () -> Unit) {
    var question by remember { mutableStateOf("") }
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Pregúntale al profe", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            if (!state.aiAvailable) {
                Text("Para hacer preguntas necesitas tu API key gratis de Groq.", style = MaterialTheme.typography.bodyMedium)
                Button(onClick = onOpenSettings) { Text("Ir a Ajustes") }
            } else {
                state.qa.forEach { qa ->
                    Text("Tú: ${qa.question}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    when {
                        qa.answer != null -> Card { Text(inlineMarkup(qa.answer), modifier = Modifier.padding(12.dp)) }
                        qa.error != null -> Text(qa.error, color = MaterialTheme.colorScheme.error)
                        else -> CircularProgressIndicator(Modifier.size(24.dp))
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = question, onValueChange = { question = it },
                        label = { Text("Ej.: ¿cuándo uso since y cuándo for?") },
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { onAsk(question); question = "" }, enabled = question.isNotBlank()) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar")
                    }
                }
            }
            Text("", modifier = Modifier.padding(bottom = 24.dp))
        }
    }
}
