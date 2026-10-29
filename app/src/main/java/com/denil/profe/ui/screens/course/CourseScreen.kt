package com.denil.profe.ui.screens.course

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.denil.profe.data.curriculum.Curriculum
import com.denil.profe.data.curriculum.CurriculumRepository
import com.denil.profe.data.local.UnitProgressEntity
import com.denil.profe.data.prefs.UserPreferences
import com.denil.profe.data.repository.LearningRepository
import com.denil.profe.domain.Cefr
import com.denil.profe.ui.components.LevelBadge
import com.denil.profe.ui.components.ProfeBottomBar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseViewModel @Inject constructor(
    curriculumRepo: CurriculumRepository,
    learning: LearningRepository,
    prefs: UserPreferences
) : ViewModel() {
    private val _curriculum = MutableStateFlow<Curriculum?>(null)
    val curriculum: StateFlow<Curriculum?> = _curriculum

    val progress: StateFlow<Map<String, UnitProgressEntity>> =
        learning.observeProgress().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())

    val level: StateFlow<Cefr?> = prefs.settings.map { it.level }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    init {
        viewModelScope.launch { _curriculum.value = curriculumRepo.curriculum() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseScreen(
    currentRoute: String?,
    onNavigateTopLevel: (String) -> Unit,
    onOpenLesson: (String) -> Unit,
    viewModel: CourseViewModel = hiltViewModel()
) {
    val curriculum by viewModel.curriculum.collectAsStateWithLifecycle()
    val progress by viewModel.progress.collectAsStateWithLifecycle()
    val level by viewModel.level.collectAsStateWithLifecycle()
    var expanded by remember { mutableStateOf<Set<Cefr>?>(null) }
    LaunchedEffect(level) { if (expanded == null && level != null) expanded = setOf(level!!) }
    val listState = rememberLazyListState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Curso de inglés") }) },
        bottomBar = { ProfeBottomBar(currentRoute, onNavigateTopLevel) }
    ) { padding ->
        val c = curriculum ?: return@Scaffold
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            c.levels.forEach { lvl ->
                val isOpen = expanded?.contains(lvl.level) == true
                val done = lvl.units.count { progress[it.id]?.completed == true }
                item(key = "level-${lvl.level}") {
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable {
                            val cur = expanded ?: emptySet()
                            expanded = if (isOpen) cur - lvl.level else cur + lvl.level
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = if (lvl.level == level) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                LevelBadge(lvl.level.name)
                                Text(
                                    lvl.title.substringAfter("· ", lvl.title),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 10.dp).weight(1f)
                                )
                                if (lvl.level == level) Text("Tu nivel", style = MaterialTheme.typography.labelMedium)
                                Icon(if (isOpen) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore, contentDescription = null)
                            }
                            Text(lvl.description, style = MaterialTheme.typography.bodySmall)
                            LinearProgressIndicator(
                                progress = { if (lvl.units.isEmpty()) 0f else done.toFloat() / lvl.units.size },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text("$done de ${lvl.units.size} lecciones", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
                if (isOpen) {
                    items(lvl.units, key = { it.id }) { unit ->
                        val p = progress[unit.id]
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(start = 12.dp).clickable { onOpenLesson(unit.id) }
                        ) {
                            Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                when {
                                    p?.completed == true -> Icon(Icons.Filled.CheckCircle, contentDescription = "Completada", tint = Color(0xFF2E7D32), modifier = Modifier.size(28.dp))
                                    p != null -> Icon(Icons.Filled.PlayCircle, contentDescription = "En progreso", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                                    else -> Icon(Icons.Outlined.Circle, contentDescription = "Pendiente", tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(28.dp))
                                }
                                Column(Modifier.weight(1f).padding(start = 12.dp)) {
                                    Text("${unit.number}. ${unit.title}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                    Text(unit.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                if (p != null && p.attempts > 0) {
                                    Text("${p.bestScore}%", style = MaterialTheme.typography.labelLarge)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
