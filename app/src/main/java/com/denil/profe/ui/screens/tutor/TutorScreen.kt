package com.denil.profe.ui.screens.tutor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.denil.profe.data.local.ActivityKind
import com.denil.profe.data.local.ChatDao
import com.denil.profe.data.local.ChatMessageEntity
import com.denil.profe.data.prefs.UserPreferences
import com.denil.profe.data.prefs.UserSettings
import com.denil.profe.data.remote.ChatTurn
import com.denil.profe.data.remote.GroqClient
import com.denil.profe.data.remote.Scenario
import com.denil.profe.data.remote.TutorPrompts
import com.denil.profe.data.repository.LearningRepository
import com.denil.profe.tts.Speaker
import com.denil.profe.ui.components.GroqKeyMissingCard
import com.denil.profe.ui.components.LevelBadge
import com.denil.profe.ui.components.inlineMarkup
import com.denil.profe.ui.components.rememberSpeechInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// ---------- Selección de escenario ----------

@HiltViewModel
class ScenarioPickerViewModel @Inject constructor(prefs: UserPreferences) : ViewModel() {
    val settings: StateFlow<UserSettings?> = prefs.settings.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScenarioPickerScreen(
    onBack: () -> Unit,
    onOpenScenario: (String) -> Unit,
    onOpenSettings: () -> Unit,
    viewModel: ScenarioPickerViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tutor con IA") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás") } }
            )
        }
    ) { padding ->
        val s = settings ?: return@Scaffold
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (!s.hasGroqKey) GroqKeyMissingCard(onOpenSettings)
            Text(
                "Elige una situación. El tutor adapta su inglés a tu nivel (${s.level.name}) y te corrige con ✏️ cuando te equivocas.",
                style = MaterialTheme.typography.bodyMedium
            )
            TutorPrompts.scenarios.forEach { sc ->
                val recommended = s.level >= sc.minLevel
                Card(
                    Modifier.fillMaxWidth().clickable { onOpenScenario(sc.id) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (recommended) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(sc.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Text(sc.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        LevelBadge(sc.minLevel.name + "+")
                    }
                }
            }
        }
    }
}

// ---------- Conversación ----------

data class TutorUiState(
    val scenario: Scenario = TutorPrompts.scenarios.first(),
    val sending: Boolean = false,
    val error: String? = null,
    val suggestions: List<String> = emptyList(),
    val suggesting: Boolean = false
)

@HiltViewModel
class TutorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val chatDao: ChatDao,
    private val groq: GroqClient,
    private val prefs: UserPreferences,
    private val speaker: Speaker,
    private val learning: LearningRepository
) : ViewModel() {

    private val scenarioId: String = savedStateHandle.get<String>("scenario") ?: "free"
    private val conversation = "tutor-$scenarioId"

    val settings: StateFlow<UserSettings?> = prefs.settings.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
    val messages: StateFlow<List<ChatMessageEntity>> =
        chatDao.observe(conversation).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _state = MutableStateFlow(TutorUiState(scenario = TutorPrompts.scenario(scenarioId)))
    val state: StateFlow<TutorUiState> = _state

    init {
        viewModelScope.launch {
            speaker.baseRate = prefs.current().speechRate
            if (chatDao.get(conversation).isEmpty()) {
                chatDao.insert(ChatMessageEntity(conversation = conversation, role = "assistant", content = _state.value.scenario.opener))
            }
        }
    }

    fun send(text: String) {
        val clean = text.trim()
        if (clean.isEmpty() || _state.value.sending) return
        _state.value = _state.value.copy(sending = true, error = null, suggestions = emptyList())
        viewModelScope.launch {
            chatDao.insert(ChatMessageEntity(conversation = conversation, role = "user", content = clean))
            try {
                val settings = prefs.current()
                val history = chatDao.get(conversation).takeLast(24).map { ChatTurn(it.role, it.content) }
                val reply = groq.chat(TutorPrompts.tutorSystem(settings.level, _state.value.scenario, settings.name), history)
                chatDao.insert(ChatMessageEntity(conversation = conversation, role = "assistant", content = reply))
                learning.addActivity(ActivityKind.TUTOR, 2, scenarioId)
                if (settings.tutorSpeaks) speak(reply)
                _state.value = _state.value.copy(sending = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(sending = false, error = e.message ?: "Error")
            }
        }
    }

    fun speak(content: String) {
        speaker.speak(mainPart(content))
    }

    fun suggest() {
        if (_state.value.suggesting) return
        _state.value = _state.value.copy(suggesting = true, error = null)
        viewModelScope.launch {
            try {
                val settings = prefs.current()
                val last = chatDao.get(conversation).lastOrNull { it.role == "assistant" }?.content.orEmpty()
                val text = groq.chat(
                    "You help an English learner (CEFR ${settings.level.name}) reply in a conversation. " +
                        "Give exactly 3 short, natural possible replies, one per line, no numbering, no quotes, no explanations.",
                    listOf(ChatTurn("user", "The other person said: ${mainPart(last)}")),
                    temperature = 0.9, maxTokens = 150
                )
                val lines = text.lines().map { it.trim().trimStart('-', '•', '*', ' ').trim('"') }.filter { it.isNotBlank() }.take(3)
                _state.value = _state.value.copy(suggesting = false, suggestions = lines)
            } catch (e: Exception) {
                _state.value = _state.value.copy(suggesting = false, error = e.message)
            }
        }
    }

    fun reset() {
        viewModelScope.launch {
            speaker.stop()
            chatDao.clear(conversation)
            chatDao.insert(ChatMessageEntity(conversation = conversation, role = "assistant", content = _state.value.scenario.opener))
            _state.value = _state.value.copy(error = null, suggestions = emptyList())
        }
    }

    override fun onCleared() {
        speaker.stop()
    }

    companion object {
        fun mainPart(content: String): String = content.substringBefore("✏️").trim()
        fun correctionPart(content: String): String? =
            content.indexOf("✏️").takeIf { it >= 0 }?.let { content.substring(it).trim() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorScreen(onBack: () -> Unit, onOpenSettings: () -> Unit, viewModel: TutorViewModel = hiltViewModel()) {
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    var input by rememberSaveable { mutableStateOf("") }
    var confirmReset by remember { mutableStateOf(false) }
    var micError by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()
    val mic = rememberSpeechInput(
        prompt = "Habla en inglés…",
        onResult = { results -> micError = null; viewModel.send(results.first()) },
        onError = { micError = it }
    )
    LaunchedEffect(messages.size, state.sending) {
        val count = messages.size + if (state.sending) 1 else 0
        if (count > 0) listState.animateScrollToItem(count - 1)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(state.scenario.title, maxLines = 1)
                        settings?.let { Text("Nivel ${it.level.name}", style = MaterialTheme.typography.bodySmall) }
                    }
                },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás") } },
                actions = { IconButton(onClick = { confirmReset = true }) { Icon(Icons.Filled.RestartAlt, contentDescription = "Reiniciar") } }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).imePadding()) {
            if (settings?.hasGroqKey == false) {
                GroqKeyMissingCard(onOpenSettings, Modifier.padding(12.dp))
            }
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages, key = { it.id }) { m -> Bubble(m, onSpeak = { viewModel.speak(m.content) }) }
                if (state.sending) item { Box(Modifier.padding(8.dp)) { CircularProgressIndicator(Modifier.size(24.dp)) } }
            }
            (state.error ?: micError)?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(horizontal = 12.dp))
            }
            if (state.suggestions.isNotEmpty()) {
                Column(Modifier.padding(horizontal = 12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Podrías decir:", style = MaterialTheme.typography.labelMedium)
                    state.suggestions.forEach { s ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.fillMaxWidth().clickable { input = s }
                        ) { Text(s, modifier = Modifier.padding(8.dp), style = MaterialTheme.typography.bodyMedium) }
                    }
                }
            }
            Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = viewModel::suggest, enabled = !state.suggesting && settings?.hasGroqKey == true) {
                    if (state.suggesting) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                    else Icon(Icons.Filled.Lightbulb, contentDescription = "¿Qué digo?")
                }
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    placeholder = { Text("Escribe en inglés…") },
                    modifier = Modifier.weight(1f),
                    maxLines = 4,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                )
                if (input.isBlank()) {
                    FilledIconButton(onClick = mic, enabled = !state.sending && settings?.hasGroqKey == true) {
                        Icon(Icons.Filled.Mic, contentDescription = "Hablar")
                    }
                } else {
                    FilledIconButton(onClick = { viewModel.send(input); input = "" }, enabled = !state.sending) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar")
                    }
                }
            }
        }
    }

    if (confirmReset) {
        AlertDialog(
            onDismissRequest = { confirmReset = false },
            title = { Text("¿Reiniciar la conversación?") },
            text = { Text("Se borrarán los mensajes de este escenario.") },
            confirmButton = { TextButton(onClick = { confirmReset = false; viewModel.reset() }) { Text("Reiniciar") } },
            dismissButton = { TextButton(onClick = { confirmReset = false }) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun Bubble(message: ChatMessageEntity, onSpeak: () -> Unit) {
    val mine = message.role == "user"
    Row(Modifier.fillMaxWidth(), horizontalArrangement = if (mine) Arrangement.End else Arrangement.Start) {
        Column(Modifier.widthIn(max = 320.dp), horizontalAlignment = if (mine) Alignment.End else Alignment.Start) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (mine) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text(
                        inlineMarkup(if (mine) message.content else TutorViewModel.mainPart(message.content)),
                        color = if (mine) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    if (!mine) {
                        IconButton(onClick = onSpeak, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Escuchar", modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
            if (!mine) {
                TutorViewModel.correctionPart(message.content)?.let { correction ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(inlineMarkup(correction), modifier = Modifier.padding(10.dp), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
