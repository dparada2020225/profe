package com.denil.profe.ui.screens.deckdetail

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.denil.profe.data.local.CardEntity
import com.denil.profe.data.local.DeckEntity
import com.denil.profe.data.repository.StudyRepository
import com.denil.profe.domain.CardStatus
import com.denil.profe.domain.Cloze
import com.denil.profe.ui.components.EmptyState
import com.denil.profe.ui.screens.decks.DeckFormDialog
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DeckDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: StudyRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val deckId: Long = checkNotNull(savedStateHandle.get<Long>("deckId"))

    val deck: StateFlow<DeckEntity?> = repository.observeDeck(deckId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val cards: StateFlow<List<CardEntity>?> = repository.observeCards(deckId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 4)
    val messages: SharedFlow<String> = _messages

    fun updateDeck(name: String, description: String) {
        val current = deck.value ?: return
        viewModelScope.launch { repository.updateDeck(current.copy(name = name, description = description)) }
    }

    fun deleteDeck(onDone: () -> Unit) {
        val current = deck.value ?: return
        viewModelScope.launch {
            repository.deleteDeck(current)
            onDone()
        }
    }

    fun deleteCard(card: CardEntity) {
        viewModelScope.launch { repository.deleteCard(card) }
    }

    fun resetCard(card: CardEntity) {
        viewModelScope.launch {
            repository.resetCard(card)
            _messages.emit("Progreso reiniciado")
        }
    }

    fun importCsv(uri: Uri) {
        viewModelScope.launch {
            try {
                val text = withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri)?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }
                } ?: throw IllegalStateException("No se pudo abrir el archivo")
                val result = repository.importCsv(deckId, text)
                val msg = buildString {
                    append("Importadas ${result.rows.size} tarjetas")
                    if (result.skippedLines > 0) append(" · ${result.skippedLines} líneas omitidas")
                }
                _messages.emit(msg)
            } catch (e: Exception) {
                _messages.emit("Error al importar: ${e.message ?: "archivo inválido"}")
            }
        }
    }

    fun exportCsv(uri: Uri) {
        viewModelScope.launch {
            try {
                val csv = repository.exportCsv(deckId)
                withContext(Dispatchers.IO) {
                    context.contentResolver.openOutputStream(uri, "wt")?.bufferedWriter(Charsets.UTF_8)?.use { it.write(csv) }
                        ?: throw IllegalStateException("No se pudo escribir el archivo")
                }
                _messages.emit("Mazo exportado a CSV")
            } catch (e: Exception) {
                _messages.emit("Error al exportar: ${e.message ?: "desconocido"}")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckDetailScreen(
    onBack: () -> Unit,
    onStudy: () -> Unit,
    onAddCard: () -> Unit,
    onEditCard: (Long) -> Unit,
    onDeleted: () -> Unit,
    viewModel: DeckDetailViewModel = hiltViewModel()
) {
    val deck by viewModel.deck.collectAsStateWithLifecycle()
    val cards by viewModel.cards.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    var menuOpen by remember { mutableStateOf(false) }
    var showEdit by rememberSaveable { mutableStateOf(false) }
    var showDelete by rememberSaveable { mutableStateOf(false) }
    var query by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) { viewModel.messages.collect { snackbar.showSnackbar(it) } }

    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { viewModel.importCsv(it) }
    }
    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri ->
        uri?.let { viewModel.exportCsv(it) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = { Text(deck?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") }
                },
                actions = {
                    Box {
                        IconButton(onClick = { menuOpen = true }) { Icon(Icons.Filled.MoreVert, contentDescription = "Más opciones") }
                        DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                            DropdownMenuItem(text = { Text("Editar mazo") }, onClick = { menuOpen = false; showEdit = true })
                            DropdownMenuItem(
                                text = { Text("Importar CSV") },
                                onClick = {
                                    menuOpen = false
                                    importLauncher.launch(arrayOf("text/*", "text/csv", "text/comma-separated-values", "application/octet-stream"))
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Exportar CSV") },
                                onClick = {
                                    menuOpen = false
                                    val safeName = (deck?.name ?: "mazo").replace(Regex("[^\\p{L}\\p{N} _-]"), "").trim().ifEmpty { "mazo" }
                                    exportLauncher.launch("$safeName.csv")
                                }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(text = { Text("Eliminar mazo") }, onClick = { menuOpen = false; showDelete = true })
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddCard) { Icon(Icons.Filled.Add, contentDescription = "Añadir tarjeta") }
        }
    ) { padding ->
        val list = cards
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            deck?.description?.takeIf { it.isNotBlank() }?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
            if (list != null) {
                val newCount = list.count { it.cardStatus == CardStatus.NEW }
                val learning = list.count { it.cardStatus == CardStatus.LEARNING }
                val mature = list.count { it.cardStatus == CardStatus.MATURE }
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AssistChip(onClick = {}, label = { Text("$newCount nuevas") })
                    AssistChip(onClick = {}, label = { Text("$learning aprendiendo") })
                    AssistChip(onClick = {}, label = { Text("$mature maduras") })
                }
                Button(
                    onClick = onStudy,
                    enabled = list.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Estudiar ahora")
                }
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Buscar tarjeta") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
                )
                val filtered = if (query.isBlank()) list else list.filter {
                    it.front.contains(query, true) || it.back.contains(query, true) || it.tags.contains(query, true) || it.section == query.trim()
                }
                if (list.isEmpty()) {
                    EmptyState(
                        title = "Este mazo está vacío",
                        subtitle = "Añade tarjetas con el botón + o importa un archivo CSV (frente;reverso;ejemplo). Si el frente lleva {{huecos}}, se crea como tarjeta de completar."
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filtered, key = { it.id }) { card ->
                            CardRow(
                                card = card,
                                onEdit = { onEditCard(card.id) },
                                onReset = { viewModel.resetCard(card) },
                                onDelete = { viewModel.deleteCard(card) }
                            )
                        }
                    }
                }
            }
        }
    }

    val currentDeck = deck
    if (showEdit && currentDeck != null) {
        DeckFormDialog(
            title = "Editar mazo",
            initialName = currentDeck.name,
            initialDescription = currentDeck.description,
            onDismiss = { showEdit = false },
            onConfirm = { n, d -> viewModel.updateDeck(n, d); showEdit = false }
        )
    }
    if (showDelete) {
        AlertDialog(
            onDismissRequest = { showDelete = false },
            title = { Text("¿Eliminar mazo?") },
            text = { Text("Se borrarán todas sus tarjetas y su historial. Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = { showDelete = false; viewModel.deleteDeck(onDeleted) }) { Text("Eliminar") }
            },
            dismissButton = { TextButton(onClick = { showDelete = false }) { Text("Cancelar") } }
        )
    }
}

private val dateFormat = SimpleDateFormat("d MMM", Locale("es"))

@Composable
private fun CardRow(card: CardEntity, onEdit: () -> Unit, onReset: () -> Unit, onDelete: () -> Unit) {
    var menu by remember { mutableStateOf(false) }
    var confirmDelete by remember { mutableStateOf(false) }
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onEdit)) {
        Row(modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 4.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                if (card.isCloze) {
                    Text(Cloze.masked(card.front), style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Respuesta: " + Cloze.blanks(card.front).joinToString(", ") { it.display },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(card.front, style = MaterialTheme.typography.titleMedium)
                    Text(card.back, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 3)
                }
                Spacer(Modifier.height(4.dp))
                val statusLabel = when (card.cardStatus) {
                    CardStatus.NEW -> "Nueva"
                    CardStatus.LEARNING -> "Aprendiendo · vence ${dateFormat.format(Date(card.dueAt))}"
                    CardStatus.MATURE -> "Madura · vence ${dateFormat.format(Date(card.dueAt))}"
                }
                val tags = card.tagList.joinToString(" · ")
                val section = if (card.section.isNotBlank()) "§${card.section} · " else ""
                Text(
                    section + (if (tags.isEmpty()) statusLabel else "$statusLabel · $tags"),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Box {
                IconButton(onClick = { menu = true }) { Icon(Icons.Filled.MoreVert, contentDescription = "Opciones de la tarjeta") }
                DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                    DropdownMenuItem(text = { Text("Editar") }, onClick = { menu = false; onEdit() })
                    DropdownMenuItem(text = { Text("Reiniciar progreso") }, onClick = { menu = false; onReset() })
                    DropdownMenuItem(text = { Text("Eliminar") }, onClick = { menu = false; confirmDelete = true })
                }
            }
        }
    }
    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("¿Eliminar tarjeta?") },
            text = { Text("“${card.front}” se eliminará junto con su historial.") },
            confirmButton = { TextButton(onClick = { confirmDelete = false; onDelete() }) { Text("Eliminar") } },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("Cancelar") } }
        )
    }
}
