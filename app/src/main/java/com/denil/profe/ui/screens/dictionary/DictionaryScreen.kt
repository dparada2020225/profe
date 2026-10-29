package com.denil.profe.ui.screens.dictionary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.denil.profe.data.local.ActivityKind
import com.denil.profe.data.remote.DictionaryClient
import com.denil.profe.data.remote.DictionaryEntry
import com.denil.profe.data.remote.SentencePair
import com.denil.profe.data.repository.LearningRepository
import com.denil.profe.tts.AudioPlayer
import com.denil.profe.tts.Speaker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DictionaryUiState(
    val loading: Boolean = false,
    val query: String = "",
    val entry: DictionaryEntry? = null,
    val notFound: Boolean = false,
    val translation: String? = null,
    val examples: List<SentencePair> = emptyList(),
    val examplesError: String? = null,
    val error: String? = null
)

@HiltViewModel
class DictionaryViewModel @Inject constructor(
    private val client: DictionaryClient,
    private val learning: LearningRepository,
    private val speaker: Speaker,
    private val audio: AudioPlayer
) : ViewModel() {
    private val _state = MutableStateFlow(DictionaryUiState())
    val state: StateFlow<DictionaryUiState> = _state
    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 2)
    val messages: SharedFlow<String> = _messages

    fun search(raw: String) {
        val word = raw.trim()
        if (word.isEmpty()) return
        _state.value = DictionaryUiState(loading = true, query = word)
        viewModelScope.launch {
            val entryJob = async { runCatching { client.lookup(word) } }
            val transJob = async { runCatching { client.translate(word) }.getOrNull() }
            val exJob = async { runCatching { client.examples(word) } }
            val entry = entryJob.await()
            val examples = exJob.await()
            _state.update {
                it.copy(
                    loading = false,
                    entry = entry.getOrNull(),
                    notFound = entry.isSuccess && entry.getOrNull() == null,
                    error = entry.exceptionOrNull()?.message,
                    translation = transJob.await(),
                    examples = examples.getOrDefault(emptyList()),
                    examplesError = examples.exceptionOrNull()?.message
                )
            }
            if (entry.getOrNull() != null) learning.addActivity(ActivityKind.DICTIONARY, 1, word)
        }
    }

    fun playWord() {
        val e = _state.value.entry
        val word = e?.word ?: _state.value.query
        val url = e?.audioUrl.orEmpty()
        if (url.isNotBlank()) {
            if (!audio.play(url) { speaker.speak(word) }) speaker.speak(word)
        } else speaker.speak(word)
    }

    fun speak(text: String) { speaker.speak(text) }

    fun save(front: String, back: String, example: String) {
        viewModelScope.launch {
            learning.addToMyWords(front, back, example)
            _messages.emit("Guardada en «${LearningRepository.MY_WORDS_DECK}»")
        }
    }

    override fun onCleared() { audio.stop() }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DictionaryScreen(onBack: () -> Unit, viewModel: DictionaryViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var query by rememberSaveable { mutableStateOf("") }
    var saveDialog by remember { mutableStateOf(false) }
    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(Unit) { viewModel.messages.collect { snackbar.showSnackbar(it) } }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = { Text("Diccionario") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás") } }
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Palabra en inglés") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = { IconButton(onClick = { viewModel.search(query) }) { Icon(Icons.Filled.Search, contentDescription = "Buscar") } },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.None, autoCorrectEnabled = false, imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { viewModel.search(query) })
            )
            if (state.loading) CircularProgressIndicator()
            state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            if (state.notFound) Text("No encontré «${state.query}» en el diccionario.", style = MaterialTheme.typography.bodyMedium)

            val e = state.entry
            if (!state.loading && (e != null || state.translation != null)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(e?.word ?: state.query, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        val phonetic = e?.phonetic.orEmpty()
                        if (phonetic.isNotBlank()) Text(phonetic, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        state.translation?.let { Text("Español: $it", style = MaterialTheme.typography.titleMedium) }
                    }
                    IconButton(onClick = viewModel::playWord) { Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Pronunciación") }
                }
                FilledTonalButton(onClick = { saveDialog = true }) {
                    Icon(Icons.Filled.BookmarkAdd, contentDescription = null)
                    Text("Guardar en mis palabras", modifier = Modifier.padding(start = 8.dp))
                }
            }
            e?.let { entry ->
                if (entry.definitions.isNotEmpty()) {
                    HorizontalDivider()
                    Text("Definiciones", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    entry.definitions.forEach { d ->
                        Column {
                            Text("${d.partOfSpeech} · ${d.definition}", style = MaterialTheme.typography.bodyMedium)
                            if (d.example.isNotBlank()) Text("“${d.example}”", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                if (entry.synonyms.isNotEmpty()) Text("Sinónimos: " + entry.synonyms.joinToString(", "), style = MaterialTheme.typography.bodySmall)
            }
            if (state.examples.isNotEmpty()) {
                HorizontalDivider()
                Text("Ejemplos con traducción (Tatoeba)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                state.examples.forEach { ex ->
                    Card(Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(ex.en, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                Text(ex.es, style = MaterialTheme.typography.bodySmall)
                            }
                            IconButton(onClick = { viewModel.speak(ex.en) }, modifier = Modifier.size(36.dp)) {
                                Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Escuchar")
                            }
                        }
                    }
                }
            }
            if (state.query.isEmpty()) {
                Text(
                    "Busca cualquier palabra: verás su pronunciación, definiciones en inglés, traducción y oraciones reales con su traducción. " +
                        "Necesita internet.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    if (saveDialog) {
        val entry = state.entry
        var front by remember { mutableStateOf(entry?.word ?: state.query) }
        var back by remember { mutableStateOf(state.translation.orEmpty()) }
        var example by remember {
            mutableStateOf(state.examples.firstOrNull()?.en ?: entry?.definitions?.firstOrNull { it.example.isNotBlank() }?.example.orEmpty())
        }
        AlertDialog(
            onDismissRequest = { saveDialog = false },
            title = { Text("Nueva tarjeta") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(front, { front = it }, label = { Text("Inglés") })
                    OutlinedTextField(back, { back = it }, label = { Text("Significado") })
                    OutlinedTextField(example, { example = it }, label = { Text("Ejemplo") })
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.save(front, back, example); saveDialog = false }, enabled = front.isNotBlank() && back.isNotBlank()) {
                    Text("Guardar")
                }
            },
            dismissButton = { TextButton(onClick = { saveDialog = false }) { Text("Cancelar") } }
        )
    }
}
