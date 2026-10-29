package com.denil.profe.ui.screens.decks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.denil.profe.data.local.DeckSummary
import com.denil.profe.data.repository.StudyRepository
import com.denil.profe.domain.StudyStats
import com.denil.profe.ui.components.EmptyState
import com.denil.profe.ui.components.ProfeBottomBar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DeckListViewModel @Inject constructor(private val repository: StudyRepository) : ViewModel() {

    val decks: StateFlow<List<DeckSummary>?> =
        repository.observeDeckSummaries(StudyStats.endOfDay(LocalDate.now()))
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun createDeck(name: String, description: String) {
        if (name.isBlank()) return
        viewModelScope.launch { repository.createDeck(name, description) }
    }
}

/** Un grupo de mazos: un curso o los mazos sueltos. */
private data class DeckGroup(val course: String, val decks: List<DeckSummary>) {
    val isCourse: Boolean get() = course.isNotBlank()
    val total: Int get() = decks.sumOf { it.totalCards }
    val mature: Int get() = decks.sumOf { it.matureCards }
    val learning: Int get() = decks.sumOf { it.learningCards }
    val due: Int get() = decks.sumOf { it.dueCards }
    val fresh: Int get() = decks.sumOf { it.newCards }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckListScreen(
    currentRoute: String?,
    onNavigateTopLevel: (String) -> Unit,
    onOpenDeck: (Long) -> Unit,
    onStudyDeck: (Long) -> Unit,
    onStudyCourse: (String) -> Unit,
    viewModel: DeckListViewModel = hiltViewModel()
) {
    val decks by viewModel.decks.collectAsStateWithLifecycle()
    var showCreate by rememberSaveable { mutableStateOf(false) }
    var collapsed by androidx.compose.runtime.remember { mutableStateOf(setOf<String>()) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mis palabras") }) },
        bottomBar = { ProfeBottomBar(currentRoute, onNavigateTopLevel) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreate = true },
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Nuevo mazo") }
            )
        }
    ) { padding ->
        val list = decks
        when {
            list == null -> Unit
            list.isEmpty() -> EmptyState(
                title = "Aún no tienes mazos",
                subtitle = "Abre una lección del curso: sus palabras aparecen aquí. También puedes crear mazos o guardar palabras desde el diccionario.",
                modifier = Modifier.padding(padding)
            )
            else -> {
                // Mazos sueltos primero, luego cada curso.
                val groups = list.groupBy { it.course }
                    .map { (course, ds) -> DeckGroup(course, ds) }
                    .sortedWith(compareBy<DeckGroup> { it.isCourse }.thenBy { it.course.lowercase() })
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    groups.forEach { group ->
                        val key = group.course.ifBlank { "__loose__" }
                        val isCollapsed = key in collapsed
                        item(key = "header-$key") {
                            GroupHeader(
                                group = group,
                                collapsed = isCollapsed,
                                onToggle = { collapsed = if (isCollapsed) collapsed - key else collapsed + key },
                                onStudyCourse = { onStudyCourse(group.course) }
                            )
                        }
                        if (!isCollapsed) {
                            items(group.decks, key = { it.id }) { deck ->
                                DeckCard(deck, onOpen = { onOpenDeck(deck.id) }, onStudy = { onStudyDeck(deck.id) })
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreate) {
        DeckFormDialog(
            title = "Nuevo mazo",
            onDismiss = { showCreate = false },
            onConfirm = { name, desc ->
                viewModel.createDeck(name, desc)
                showCreate = false
            }
        )
    }
}

@Composable
private fun GroupHeader(group: DeckGroup, collapsed: Boolean, onToggle: () -> Unit, onStudyCourse: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable(onClick = onToggle)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    if (group.isCourse) group.course else "Mis mazos",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${group.decks.size} mazos · ${group.total} tarjetas · ${group.fresh} nuevas · ${group.due} pendientes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onToggle) {
                Icon(
                    if (collapsed) Icons.Filled.ExpandMore else Icons.Filled.ExpandLess,
                    contentDescription = if (collapsed) "Mostrar" else "Ocultar"
                )
            }
        }
        if (group.isCourse) {
            ProgressBar(total = group.total, mature = group.mature, learning = group.learning)
            Spacer(Modifier.height(8.dp))
            FilledTonalButton(onClick = onStudyCourse, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.PlayArrow, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(if (group.due + group.fresh > 0) "Estudiar el curso completo" else "Estudiar el curso (nada pendiente hoy)")
            }
        }
    }
}

/** Barra de progreso: maduras (dominadas) + aprendiendo sobre el total. */
@Composable
private fun ProgressBar(total: Int, mature: Int, learning: Int, modifier: Modifier = Modifier) {
    val t = total.coerceAtLeast(1).toFloat()
    val matureFraction = mature / t
    val learningFraction = (mature + learning) / t
    Column(modifier = modifier.fillMaxWidth()) {
        androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxWidth()) {
            LinearProgressIndicator(
                progress = { learningFraction },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = MaterialTheme.colorScheme.tertiary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            LinearProgressIndicator(
                progress = { matureFraction },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = Color.Transparent
            )
        }
        Text(
            "${(matureFraction * 100).toInt()} % dominado · ${(learningFraction * 100).toInt()} % en progreso",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun DeckCard(deck: DeckSummary, onOpen: () -> Unit, onStudy: () -> Unit) {
    val pending = deck.dueCards + deck.newCards
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onOpen),
        colors = if (deck.course.isNotBlank()) CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
        else CardDefaults.cardColors()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(deck.name, style = MaterialTheme.typography.titleMedium)
            if (deck.description.isNotBlank()) {
                Text(
                    deck.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                    maxLines = 2
                )
            }
            Spacer(Modifier.height(10.dp))
            ProgressBar(total = deck.totalCards, mature = deck.matureCards, learning = deck.learningCards)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(
                    "${deck.totalCards} tarjetas · ${deck.newCards} nuevas · ${deck.dueCards} pendientes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                Button(onClick = onStudy, enabled = deck.totalCards > 0) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text(if (pending > 0) "Estudiar ($pending)" else "Estudiar")
                }
            }
        }
    }
}

@Composable
fun DeckFormDialog(
    title: String,
    initialName: String = "",
    initialDescription: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var name by rememberSaveable { mutableStateOf(initialName) }
    var description by rememberSaveable { mutableStateOf(initialDescription) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción (opcional)") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(name.trim(), description.trim()) }, enabled = name.isNotBlank()) {
                Text("Guardar")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
