package com.denil.profe.ui.screens.cardeditor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.denil.profe.data.local.CardType
import com.denil.profe.data.repository.StudyRepository
import com.denil.profe.domain.Cloze
import androidx.compose.material3.FilterChip
import com.denil.profe.tts.Speaker
import com.denil.profe.tts.TtsStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CardEditorState(
    val loading: Boolean = true,
    val isEditing: Boolean = false,
    val front: String = "",
    val back: String = "",
    val example: String = "",
    val tags: String = "",
    val hint: String = "",
    val type: CardType = CardType.BASIC,
    val savedCount: Int = 0
) {
    val canSave: Boolean get() = when (type) {
        CardType.BASIC -> front.isNotBlank() && back.isNotBlank()
        CardType.CLOZE -> Cloze.hasBlanks(front)
    }
}

@HiltViewModel
class CardEditorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: StudyRepository,
    private val speaker: Speaker
) : ViewModel() {

    val deckId: Long = checkNotNull(savedStateHandle.get<Long>("deckId"))
    private val cardId: Long = savedStateHandle.get<Long>("cardId") ?: -1L

    private val _state = MutableStateFlow(CardEditorState())
    val state: StateFlow<CardEditorState> = _state
    val ttsStatus: StateFlow<TtsStatus> = speaker.status

    init {
        viewModelScope.launch {
            if (cardId > 0) {
                val card = repository.getCard(cardId)
                if (card != null) {
                    _state.update {
                        it.copy(
                            loading = false, isEditing = true,
                            front = card.front, back = card.back, example = card.example, tags = card.tags,
                            hint = card.hint, type = card.cardType
                        )
                    }
                    return@launch
                }
            }
            _state.update { it.copy(loading = false) }
        }
    }

    fun onFront(v: String) = _state.update { it.copy(front = v) }
    fun onBack(v: String) = _state.update { it.copy(back = v) }
    fun onExample(v: String) = _state.update { it.copy(example = v) }
    fun onTags(v: String) = _state.update { it.copy(tags = v) }
    fun onHint(v: String) = _state.update { it.copy(hint = v) }
    fun onType(t: CardType) = _state.update { it.copy(type = t) }

    fun speak(): Boolean {
        val s = _state.value
        val text = if (s.type == CardType.CLOZE) Cloze.filled(s.front) else s.front
        return speaker.speak(text.trim())
    }

    /** Guarda; si `keepAdding` es true limpia el formulario para agregar otra. */
    fun save(keepAdding: Boolean, onDone: () -> Unit) {
        val s = _state.value
        if (!s.canSave) return
        viewModelScope.launch {
            if (s.isEditing) {
                repository.getCard(cardId)?.let {
                    repository.updateCardContent(it, s.front, s.back, s.example, s.tags, type = s.type, hint = s.hint)
                }
                onDone()
            } else {
                repository.addCard(deckId, s.front, s.back, s.example, s.tags, type = s.type, hint = s.hint)
                if (keepAdding) {
                    _state.update { it.copy(front = "", back = "", example = "", hint = "", savedCount = it.savedCount + 1) }
                } else {
                    onDone()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardEditorScreen(onBack: () -> Unit, viewModel: CardEditorViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val tts by viewModel.ttsStatus.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.savedCount) {
        if (state.savedCount > 0) snackbar.showSnackbar("Tarjeta guardada (${state.savedCount} en esta sesión)")
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditing) "Editar tarjeta" else "Nueva tarjeta") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") }
                }
            )
        }
    ) { padding ->
        if (state.loading) return@Scaffold
        val isCloze = state.type == CardType.CLOZE
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = !isCloze,
                    onClick = { viewModel.onType(CardType.BASIC) },
                    label = { Text("Básica (frente / reverso)") }
                )
                FilterChip(
                    selected = isCloze,
                    onClick = { viewModel.onType(CardType.CLOZE) },
                    label = { Text("Completar (huecos)") }
                )
            }
            if (isCloze) {
                Text(
                    "Escribe la oración en inglés y marca cada respuesta entre llaves dobles: She {{is}} a doctor. " +
                        "Puedes dar varias respuestas válidas con |: {{isn't|is not}}.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            OutlinedTextField(
                value = state.front,
                onValueChange = viewModel::onFront,
                label = { Text(if (isCloze) "Oración con {{huecos}}" else "Frente (inglés)") },
                singleLine = !isCloze,
                minLines = if (isCloze) 2 else 1,
                isError = isCloze && state.front.isNotBlank() && !Cloze.hasBlanks(state.front),
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (!viewModel.speak()) scope.launch { snackbar.showSnackbar(ttsMessage(tts)) }
                        },
                        enabled = state.front.isNotBlank()
                    ) { Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Pronunciar") }
                }
            )
            if (isCloze) {
                OutlinedTextField(
                    value = state.hint,
                    onValueChange = viewModel::onHint,
                    label = { Text("Pista (opcional, se ve antes de responder)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            OutlinedTextField(
                value = state.back,
                onValueChange = viewModel::onBack,
                label = { Text(if (isCloze) "Explicación (opcional, se ve al revelar)" else "Reverso (español)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.example,
                onValueChange = viewModel::onExample,
                label = { Text("Ejemplo de uso (opcional)") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.tags,
                onValueChange = viewModel::onTags,
                label = { Text("Etiquetas separadas por coma (opcional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                "Ejemplo: verbo, phrasal verb, trabajo",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            if (state.isEditing) {
                Button(onClick = { viewModel.save(false, onBack) }, enabled = state.canSave, modifier = Modifier.fillMaxWidth()) {
                    Text("Guardar cambios")
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { viewModel.save(true, onBack) },
                        enabled = state.canSave,
                        modifier = Modifier.weight(1f)
                    ) { Text("Guardar y añadir otra") }
                    Spacer(Modifier.width(0.dp))
                    Button(
                        onClick = { viewModel.save(false, onBack) },
                        enabled = state.canSave,
                        modifier = Modifier.weight(1f)
                    ) { Text("Guardar") }
                }
            }
        }
    }
}

fun ttsMessage(status: TtsStatus): String = when (status) {
    TtsStatus.INITIALIZING -> "El motor de voz se está iniciando, intenta de nuevo en un momento."
    TtsStatus.READY -> "No se pudo reproducir la pronunciación."
    TtsStatus.LANGUAGE_MISSING -> "La voz en inglés no está disponible sin conexión. Descárgala en Ajustes del teléfono › Idiomas y entrada › Texto a voz."
    TtsStatus.ENGINE_UNAVAILABLE -> "No hay ningún motor de texto a voz disponible en este teléfono."
}
