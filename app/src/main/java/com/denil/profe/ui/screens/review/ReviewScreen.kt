package com.denil.profe.ui.screens.review

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.denil.profe.data.local.CardEntity
import com.denil.profe.data.repository.StudyRepository
import com.denil.profe.domain.CardStatus
import com.denil.profe.domain.Cloze
import com.denil.profe.domain.Rating
import com.denil.profe.domain.Sm2
import com.denil.profe.domain.Sm2Result
import com.denil.profe.tts.Speaker
import com.denil.profe.tts.TtsStatus
import com.denil.profe.ui.screens.cardeditor.ttsMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReviewUiState(
    val loading: Boolean = true,
    val deckName: String = "",
    val typedAnswer: String = "",
    val checkResult: Cloze.CheckResult? = null,
    val queue: List<Long> = emptyList(),
    val current: CardEntity? = null,
    val revealed: Boolean = false,
    val previews: Map<Rating, Sm2Result> = emptyMap(),
    val total: Int = 0,
    val completed: Int = 0,
    val againCount: Int = 0,
    val finished: Boolean = false,
    val nothingToStudy: Boolean = false
)

@HiltViewModel
class ReviewViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: StudyRepository,
    private val speaker: Speaker
) : ViewModel() {

    private val deckId: Long = savedStateHandle.get<Long>("deckId") ?: -1L
    private val course: String = savedStateHandle.get<String>("course").orEmpty()

    private val _state = MutableStateFlow(ReviewUiState())
    val state: StateFlow<ReviewUiState> = _state
    val ttsStatus: StateFlow<TtsStatus> = speaker.status

    private var processing = false

    companion object {
        /** deckId especial: repasar todos los mazos juntos. */
        const val ALL_DECKS = -2L
    }

    init {
        viewModelScope.launch {
            val title: String
            val deckIds: List<Long>
            if (course.isNotBlank()) {
                title = course
                deckIds = repository.getDecksOfCourse(course).map { it.id }
            } else if (deckId == ALL_DECKS) {
                title = "Todas tus palabras"
                deckIds = repository.allDeckIds()
            } else {
                title = repository.getDeck(deckId)?.name ?: ""
                deckIds = listOf(deckId)
            }
            val plan = repository.planSession(deckIds)
            if (plan.total == 0) {
                _state.update { it.copy(loading = false, deckName = title, nothingToStudy = true, finished = true) }
            } else {
                _state.update { it.copy(deckName = title, queue = plan.queue, total = plan.total) }
                loadCurrent()
            }
        }
    }

    private suspend fun loadCurrent() {
        val id = _state.value.queue.firstOrNull()
        if (id == null) {
            _state.update { it.copy(loading = false, current = null, finished = true, revealed = false) }
            return
        }
        val card = repository.getCard(id)
        if (card == null) {
            _state.update { it.copy(queue = it.queue.drop(1)) }
            loadCurrent()
            return
        }
        _state.update {
            it.copy(
                loading = false, current = card, revealed = false, previews = Sm2.preview(card.sm2State),
                typedAnswer = "", checkResult = null
            )
        }
    }

    fun onTypedAnswer(value: String) = _state.update { it.copy(typedAnswer = value) }

    /** Revela la respuesta; en tarjetas de completar comprueba lo escrito (si se escribió algo). */
    fun reveal() = _state.update { s ->
        val card = s.current
        val result = if (card != null && card.isCloze && s.typedAnswer.isNotBlank()) Cloze.check(card.front, s.typedAnswer) else null
        s.copy(revealed = true, checkResult = result)
    }

    fun speakCurrent(): Boolean {
        val card = _state.value.current ?: return false
        val text = when {
            card.audio.isNotBlank() -> card.audio
            card.isCloze -> Cloze.filled(card.front)
            else -> card.front
        }
        return speaker.speak(text)
    }

    fun rate(rating: Rating) {
        val card = _state.value.current ?: return
        if (processing) return
        processing = true
        viewModelScope.launch {
            try {
                repository.reviewCard(card, rating)
                _state.update { s ->
                    val rest = s.queue.drop(1)
                    if (rating == Rating.AGAIN) {
                        // Se vuelve a mostrar más adelante en la misma sesión.
                        s.copy(queue = rest + card.id, againCount = s.againCount + 1)
                    } else {
                        s.copy(queue = rest, completed = s.completed + 1)
                    }
                }
                loadCurrent()
            } finally {
                processing = false
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(onBack: () -> Unit, viewModel: ReviewViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val tts by viewModel.ttsStatus.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Aviso si la voz en inglés no está disponible.
    LaunchedEffect(tts) {
        if (tts == TtsStatus.LANGUAGE_MISSING || tts == TtsStatus.ENGINE_UNAVAILABLE) snackbar.showSnackbar(ttsMessage(tts))
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = { Text(state.deckName.ifBlank { "Repaso" }) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Filled.Close, contentDescription = "Salir") }
                },
                actions = {
                    if (!state.finished) {
                        Text(
                            "${state.completed}/${state.total}",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    }
                }
            )
        }
    ) { padding ->
        when {
            state.loading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            state.finished -> SessionFinished(state, onBack, Modifier.padding(padding))
            else -> {
                val card = state.current ?: return@Scaffold
                Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
                    LinearProgressIndicator(
                        progress = { if (state.total == 0) 0f else state.completed.toFloat() / state.total },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    val statusLabel = when (card.cardStatus) {
                        CardStatus.NEW -> "Tarjeta nueva"
                        CardStatus.LEARNING -> "Aprendiendo"
                        CardStatus.MATURE -> "Madura"
                    }
                    val sectionLabel = if (card.section.isNotBlank()) " · Sección ${card.section}" else ""
                    Text(statusLabel + sectionLabel, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(8.dp))
                    val onSpeak = {
                        if (!viewModel.speakCurrent()) scope.launch { snackbar.showSnackbar(ttsMessage(tts)) }
                        Unit
                    }
                    if (card.isCloze) {
                        ClozeCard(
                            card = card,
                            revealed = state.revealed,
                            typedAnswer = state.typedAnswer,
                            onTypedAnswer = viewModel::onTypedAnswer,
                            checkResult = state.checkResult,
                            onCheck = { viewModel.reveal() },
                            onSpeak = onSpeak,
                            modifier = Modifier.weight(1f).fillMaxWidth()
                        )
                    } else {
                        FlipCard(
                            card = card,
                            revealed = state.revealed,
                            onFlip = { viewModel.reveal() },
                            onSpeak = onSpeak,
                            modifier = Modifier.weight(1f).fillMaxWidth()
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    if (!state.revealed) {
                        Button(onClick = { viewModel.reveal() }, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                            Text(if (card.isCloze) "Comprobar / mostrar respuesta" else "Mostrar respuesta")
                        }
                    } else {
                        RatingButtons(previews = state.previews, onRate = viewModel::rate)
                    }
                }
            }
        }
    }
}

@Composable
private fun FlipCard(
    card: CardEntity,
    revealed: Boolean,
    onFlip: () -> Unit,
    onSpeak: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(targetValue = if (revealed) 180f else 0f, animationSpec = tween(450), label = "flip")
    val showBack = rotation > 90f
    val containerColor by animateColorAsState(
        targetValue = if (showBack) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primaryContainer,
        label = "cardColor"
    )
    Card(
        modifier = modifier
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clickable(enabled = !revealed, onClick = onFlip),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                // La cara trasera se vuelve a espejar para que el texto se lea bien.
                .graphicsLayer { rotationY = if (showBack) 180f else 0f }
        ) {
            if (!showBack) {
                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        card.front,
                        style = if (card.front.length > 60) MaterialTheme.typography.titleLarge else MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Toca la tarjeta para ver la respuesta",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(card.front, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    Text(card.back, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
                    if (card.example.isNotBlank()) {
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "“${card.example}”",
                            style = MaterialTheme.typography.bodyLarge,
                            fontStyle = FontStyle.Italic,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            IconButton(onClick = onSpeak, modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)) {
                Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Pronunciar en inglés")
            }
        }
    }
}

/** Tarjeta de completar: oración con huecos, campo para escribir y comprobación. */
@Composable
private fun ClozeCard(
    card: CardEntity,
    revealed: Boolean,
    typedAnswer: String,
    onTypedAnswer: (String) -> Unit,
    checkResult: Cloze.CheckResult?,
    onCheck: () -> Unit,
    onSpeak: () -> Unit,
    modifier: Modifier = Modifier
) {
    val segments = remember(card.id) { Cloze.parse(card.front) }
    val blankCount = segments.count { it.isBlank }
    val primary = MaterialTheme.colorScheme.primary
    val okColor = Color(0xFF2E7D32)
    val badColor = Color(0xFFC62828)

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (revealed) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Completa", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(12.dp))
                val sentence = buildAnnotatedString {
                    segments.forEach { seg ->
                        if (!seg.isBlank) {
                            append(seg.text)
                        } else if (revealed) {
                            withStyle(SpanStyle(color = primary, fontWeight = FontWeight.Bold)) { append(seg.display) }
                        } else {
                            withStyle(SpanStyle(color = primary, fontWeight = FontWeight.Bold)) { append("______") }
                        }
                    }
                }
                Text(sentence, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
                if (card.hint.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Pista: ${card.hint}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(Modifier.height(16.dp))
                if (!revealed) {
                    OutlinedTextField(
                        value = typedAnswer,
                        onValueChange = onTypedAnswer,
                        label = { Text(if (blankCount > 1) "Tus respuestas (separadas por coma)" else "Tu respuesta (opcional)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { onCheck() }),
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    if (checkResult != null) {
                        val allOk = checkResult.allCorrect
                        Text(
                            if (allOk) "✓ Correcto" else "✗ Revisa tu respuesta",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (allOk) okColor else badColor,
                            fontWeight = FontWeight.Bold
                        )
                        if (!allOk) {
                            checkResult.blanks.forEach { b ->
                                Text(
                                    "Escribiste: “${b.given.ifBlank { "—" }}” · Correcto: ${b.expected}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (b.correct) okColor else badColor,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                    val alternatives = segments.filter { it.isBlank && it.alternatives.size > 1 }
                    if (alternatives.isNotEmpty()) {
                        Text(
                            "También válido: " + alternatives.joinToString(" · ") { it.alternatives.drop(1).joinToString(" / ") },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                    if (card.back.isNotBlank()) {
                        Text(card.back, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
                    }
                }
            }
            IconButton(onClick = onSpeak, modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)) {
                Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Pronunciar en inglés")
            }
        }
    }
}

private data class RatingSpec(val rating: Rating, val label: String, val color: Color)

@Composable
private fun RatingButtons(previews: Map<Rating, Sm2Result>, onRate: (Rating) -> Unit) {
    val specs = listOf(
        RatingSpec(Rating.AGAIN, "Otra vez", Color(0xFFD32F2F)),
        RatingSpec(Rating.HARD, "Difícil", Color(0xFFF57C00)),
        RatingSpec(Rating.GOOD, "Bien", Color(0xFF388E3C)),
        RatingSpec(Rating.EASY, "Fácil", Color(0xFF1976D2))
    )
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        specs.forEach { spec ->
            val interval = previews[spec.rating]?.let { Sm2.formatInterval(it.nextIntervalMinutes) } ?: ""
            FilledTonalButton(
                onClick = { onRate(spec.rating) },
                modifier = Modifier.weight(1f).height(64.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = spec.color.copy(alpha = 0.18f),
                    contentColor = spec.color
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 8.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(spec.label, style = MaterialTheme.typography.labelLarge, maxLines = 1)
                    Text(interval, style = MaterialTheme.typography.labelSmall, maxLines = 1)
                }
            }
        }
    }
}

@Composable
private fun SessionFinished(state: ReviewUiState, onBack: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (state.nothingToStudy) {
            Text("Nada pendiente por ahora", style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Text(
                "Ya alcanzaste los límites de hoy o no hay tarjetas vencidas en este mazo. Puedes ajustar los límites diarios en Ajustes.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        } else {
            Text("¡Sesión completada!", style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Text(
                "Repasaste ${state.completed} tarjetas" +
                    (if (state.againCount > 0) " y repetiste ${state.againCount} veces \"Otra vez\"." else "."),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
        Spacer(Modifier.height(24.dp))
        Button(onClick = onBack) { Text("Volver") }
    }
}
