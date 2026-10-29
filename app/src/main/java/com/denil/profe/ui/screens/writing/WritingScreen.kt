package com.denil.profe.ui.screens.writing

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.denil.profe.data.local.ActivityKind
import com.denil.profe.data.prefs.UserPreferences
import com.denil.profe.data.remote.AiTeacher
import com.denil.profe.data.remote.GrammarIssue
import com.denil.profe.data.remote.LanguageToolClient
import com.denil.profe.data.remote.TutorPrompts
import com.denil.profe.data.remote.WritingFeedback
import com.denil.profe.data.repository.LearningRepository
import com.denil.profe.domain.Cefr
import com.denil.profe.ui.components.inlineMarkup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WritingUiState(
    val level: Cefr = Cefr.A1,
    val prompts: List<String> = emptyList(),
    val promptIndex: Int = 0,
    val checking: Boolean = false,
    val checkedText: String = "",
    val issues: List<GrammarIssue>? = null,
    val ltError: String? = null,
    val ai: WritingFeedback? = null,
    val aiError: String? = null,
    val aiAvailable: Boolean = false
) {
    val prompt: String get() = prompts.getOrElse(promptIndex) { "" }
}

@HiltViewModel
class WritingViewModel @Inject constructor(
    private val prefs: UserPreferences,
    private val languageTool: LanguageToolClient,
    private val ai: AiTeacher,
    private val learning: LearningRepository
) : ViewModel() {
    private val _state = MutableStateFlow(WritingUiState())
    val state: StateFlow<WritingUiState> = _state

    init {
        viewModelScope.launch {
            val level = prefs.current().level
            val prompts = TutorPrompts.writingPrompts(level)
            _state.value = WritingUiState(level, prompts, prompts.indices.random(), aiAvailable = ai.isAvailable())
        }
    }

    fun nextPrompt() = _state.update { it.copy(promptIndex = (it.promptIndex + 1) % it.prompts.size.coerceAtLeast(1)) }

    fun check(text: String) {
        if (text.isBlank()) return
        _state.update { it.copy(checking = true, checkedText = text, issues = null, ltError = null, ai = null, aiError = null) }
        viewModelScope.launch {
            val issues = try { languageTool.check(text) } catch (e: Exception) { _state.update { it.copy(ltError = e.message) }; null }
            _state.update { it.copy(issues = issues) }
            if (_state.value.aiAvailable) {
                try {
                    val fb = ai.reviewWriting(_state.value.level, _state.value.prompt, text)
                    _state.update { it.copy(ai = fb) }
                } catch (e: Exception) {
                    _state.update { it.copy(aiError = e.message) }
                }
            }
            _state.update { it.copy(checking = false) }
            learning.addActivity(ActivityKind.WRITING, 10, "${text.length} caracteres")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WritingScreen(onBack: () -> Unit, onOpenSettings: () -> Unit, viewModel: WritingViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var text by rememberSaveable { mutableStateOf("") }
    val words = text.trim().split(Regex("\\s+")).count { it.isNotBlank() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Escritura · ${state.level.name}") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás") } }
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Tema", style = MaterialTheme.typography.labelMedium)
                        Text(state.prompt, style = MaterialTheme.typography.titleMedium)
                    }
                    IconButton(onClick = viewModel::nextPrompt) { Icon(Icons.Filled.Refresh, contentDescription = "Otro tema") }
                }
            }
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Escribe en inglés ($words palabras)") },
                modifier = Modifier.fillMaxWidth().heightIn(min = 180.dp),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
            )
            Button(onClick = { viewModel.check(text) }, enabled = text.isNotBlank() && !state.checking, modifier = Modifier.fillMaxWidth()) {
                if (state.checking) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                Text("Revisar mi texto", modifier = Modifier.padding(start = 8.dp))
            }
            if (!state.aiAvailable) {
                Text(
                    "Con tu key de Groq además recibes una corrección completa explicada en español.",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.clickable(onClick = onOpenSettings),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            state.ltError?.let { Text("LanguageTool: $it", color = MaterialTheme.colorScheme.error) }
            state.issues?.let { issues ->
                Text("Corrector gramatical (LanguageTool)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                if (issues.isEmpty()) Text("No encontró errores. ✔", style = MaterialTheme.typography.bodyMedium)
                else {
                    HighlightedText(state.checkedText, issues)
                    issues.forEach { issue ->
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(12.dp)) {
                                val wrong = state.checkedText.substring(
                                    issue.offset.coerceIn(0, state.checkedText.length),
                                    (issue.offset + issue.length).coerceIn(0, state.checkedText.length)
                                )
                                Text("«$wrong» → ${issue.replacements.joinToString(" / ").ifBlank { "?" }}", fontWeight = FontWeight.SemiBold)
                                Text(issue.message, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }

            state.aiError?.let { Text("IA: $it", color = MaterialTheme.colorScheme.error) }
            state.ai?.let { fb ->
                Text("Corrección del profe (IA)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Nota: ${fb.score}/100 · Nivel estimado: ${fb.level}", fontWeight = FontWeight.Bold)
                        Text(inlineMarkup(fb.feedback))
                    }
                }
                fb.mistakes.forEach { m ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Text("✘ ${m.original}", color = MaterialTheme.colorScheme.error)
                            Text("✔ ${m.fix}", color = Color(0xFF2E7D32), fontWeight = FontWeight.SemiBold)
                            Text(m.why, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                if (fb.corrected.isNotBlank()) {
                    Text("Tu texto corregido", style = MaterialTheme.typography.titleSmall)
                    Text(fb.corrected, style = MaterialTheme.typography.bodyMedium)
                }
                if (fb.better.isNotBlank()) {
                    Text("Versión más natural / avanzada", style = MaterialTheme.typography.titleSmall)
                    Text(fb.better, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun HighlightedText(text: String, issues: List<GrammarIssue>) {
    val error = MaterialTheme.colorScheme.error
    val annotated = buildAnnotatedString {
        var last = 0
        issues.sortedBy { it.offset }.forEach { issue ->
            val start = issue.offset.coerceIn(last, text.length)
            val end = (issue.offset + issue.length).coerceIn(start, text.length)
            append(text.substring(last, start))
            withStyle(SpanStyle(color = error, textDecoration = TextDecoration.Underline, fontWeight = FontWeight.Bold)) {
                append(text.substring(start, end))
            }
            last = end
        }
        append(text.substring(last))
    }
    Card(Modifier.fillMaxWidth()) { Text(annotated, modifier = Modifier.padding(12.dp)) }
}
