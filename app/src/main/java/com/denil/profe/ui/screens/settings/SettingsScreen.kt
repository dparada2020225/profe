package com.denil.profe.ui.screens.settings

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.denil.profe.data.prefs.ThemeMode
import com.denil.profe.data.prefs.UserPreferences
import com.denil.profe.data.prefs.UserSettings
import com.denil.profe.data.repository.StudyRepository
import com.denil.profe.data.seed.CourseInfo
import kotlinx.coroutines.flow.MutableStateFlow
import com.denil.profe.notifications.ReminderScheduler
import com.denil.profe.ui.components.ProfeBottomBar
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
class SettingsViewModel @Inject constructor(
    private val prefs: UserPreferences,
    private val repository: StudyRepository,
    private val groq: com.denil.profe.data.remote.GroqClient,
    private val speaker: com.denil.profe.tts.Speaker,
    @ApplicationContext private val context: Context
) : ViewModel() {

    /** null = sin probar, "" = probando, "ok" = funciona, otro = mensaje de error. */
    private val _groqStatus = MutableStateFlow<String?>(null)
    val groqStatus: StateFlow<String?> = _groqStatus

    fun setName(v: String) { viewModelScope.launch { prefs.setName(v) } }
    fun setLevel(level: com.denil.profe.domain.Cefr) { viewModelScope.launch { prefs.setLevel(level) } }
    fun setDailyGoal(v: Int) { viewModelScope.launch { prefs.setDailyGoal(v) } }
    fun setGroqModel(v: String) { viewModelScope.launch { prefs.setGroqModel(v) } }
    fun setTutorSpeaks(v: Boolean) { viewModelScope.launch { prefs.setTutorSpeaks(v) } }
    fun setSpeechRate(v: Float) {
        viewModelScope.launch {
            prefs.setSpeechRate(v)
            speaker.baseRate = v
        }
    }
    fun testVoice() { speaker.speak("Hi! I'm your English teacher. Let's practice together.") }

    fun saveGroqKey(key: String) {
        viewModelScope.launch {
            prefs.setGroqApiKey(key)
            if (key.isBlank()) { _groqStatus.value = null; return@launch }
            _groqStatus.value = ""
            _groqStatus.value = groq.testKey(key) ?: "ok"
        }
    }

    val settings: StateFlow<UserSettings> = prefs.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UserSettings())

    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 4)
    val messages: SharedFlow<String> = _messages

    private fun launchUnit(block: suspend () -> Unit) {
        viewModelScope.launch { block() }
    }

    fun setNewPerDay(v: Int) { viewModelScope.launch { prefs.setNewPerDay(v) } }
    fun setReviewsPerDay(v: Int) { viewModelScope.launch { prefs.setReviewsPerDay(v) } }
    fun setThemeMode(m: ThemeMode) { viewModelScope.launch { prefs.setThemeMode(m) } }
    fun setDynamicColors(v: Boolean) { viewModelScope.launch { prefs.setDynamicColors(v) } }

    fun setReminderEnabled(enabled: Boolean): Unit = launchUnit {
        prefs.setReminderEnabled(enabled)
        val s = prefs.current()
        if (enabled) ReminderScheduler.schedule(context, s.reminderHour, s.reminderMinute)
        else ReminderScheduler.cancel(context)
    }

    fun setReminderTime(hour: Int, minute: Int): Unit = launchUnit {
        prefs.setReminderTime(hour, minute)
        if (prefs.current().reminderEnabled) ReminderScheduler.schedule(context, hour, minute)
    }

    fun exportBackup(uri: Uri): Unit = launchUnit {
        try {
            val json = repository.exportBackup()
            withContext(Dispatchers.IO) {
                context.contentResolver.openOutputStream(uri, "wt")?.bufferedWriter(Charsets.UTF_8)?.use { it.write(json) }
                    ?: throw IllegalStateException("No se pudo escribir el archivo")
            }
            _messages.emit("Respaldo exportado correctamente")
        } catch (e: Exception) {
            _messages.emit("Error al exportar: ${e.message ?: "desconocido"}")
        }
    }

    fun importBackup(uri: Uri): Unit = launchUnit {
        try {
            val text = withContext(Dispatchers.IO) {
                context.contentResolver.openInputStream(uri)?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }
            } ?: throw IllegalStateException("No se pudo abrir el archivo")
            val backup = repository.restoreBackup(text)
            _messages.emit((if (backup.fromRepaso) "Importado de Repaso: " else "Respaldo restaurado: ") + "${backup.decks.size} mazos, ${backup.cards.size} tarjetas")
        } catch (e: Exception) {
            _messages.emit("Error al restaurar: ${e.message ?: "archivo inválido"}")
        }
    }

    fun restoreSampleDeck(): Unit = launchUnit {
        repository.insertSampleDeck()
        _messages.emit("Mazo de ejemplo añadido")
    }

    private val _courses = MutableStateFlow<List<CourseInfo>>(emptyList())
    val courses: StateFlow<List<CourseInfo>> = _courses

    private val _installed = MutableStateFlow<Set<String>>(emptySet())
    val installedCourses: StateFlow<Set<String>> = _installed

    init {
        viewModelScope.launch {
            _courses.value = withContext(Dispatchers.IO) { repository.listBuiltInCourses() }
            refreshInstalled()
        }
    }

    fun refreshInstalled(): Unit = launchUnit {
        _installed.value = _courses.value.filter { repository.isCourseInstalled(it.name) }.map { it.name }.toSet()
    }

    fun installCourse(id: String): Unit = launchUnit {
        try {
            val info = repository.installBuiltInCourse(id)
            _messages.emit("Curso añadido: ${info.deckCount} mazos, ${info.cardCount} tarjetas")
        } catch (e: Exception) {
            _messages.emit("No se pudo añadir el curso: ${e.message ?: "error"}")
        }
        refreshInstalled()
    }

    fun deleteCourse(name: String): Unit = launchUnit {
        repository.deleteCourse(name)
        _messages.emit("Curso eliminado")
        refreshInstalled()
    }
}

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    currentRoute: String?,
    onNavigateTopLevel: (String) -> Unit,
    onPlacement: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val groqStatus by viewModel.groqStatus.collectAsStateWithLifecycle()
    var keyInput by rememberSaveable { mutableStateOf<String?>(null) }
    var showKey by rememberSaveable { mutableStateOf(false) }
    var nameInput by rememberSaveable { mutableStateOf<String?>(null) }
    var modelInput by rememberSaveable { mutableStateOf<String?>(null) }
    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
    val snackbar = remember { SnackbarHostState() }
    var showTimePicker by rememberSaveable { mutableStateOf(false) }
    var confirmRestore by remember { mutableStateOf<Uri?>(null) }
    var confirmCourse by remember { mutableStateOf<CourseInfo?>(null) }
    var confirmDeleteCourse by remember { mutableStateOf<CourseInfo?>(null) }
    val installed by viewModel.installedCourses.collectAsStateWithLifecycle()
    val courses by viewModel.courses.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.messages.collect { snackbar.showSnackbar(it) } }

    val notificationPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        viewModel.setReminderEnabled(granted)
    }
    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        uri?.let { viewModel.exportBackup(it) }
    }
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { confirmRestore = it }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = { TopAppBar(title = { Text("Ajustes") }) },
        bottomBar = { ProfeBottomBar(currentRoute, onNavigateTopLevel) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SectionCard("Tu perfil") {
                OutlinedTextField(
                    value = nameInput ?: settings.name,
                    onValueChange = { nameInput = it },
                    label = { Text("¿Cómo te llamo?") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        if (nameInput != null && nameInput != settings.name) {
                            TextButton(onClick = { viewModel.setName(nameInput.orEmpty()); nameInput = null }) { Text("Guardar") }
                        }
                    }
                )
                Text("Nivel de estudio: ${settings.level.name} (${settings.level.label})", style = MaterialTheme.typography.labelLarge)
                if (settings.placementResult.isNotBlank()) {
                    Text("Último examen: ${settings.placementResult}", style = MaterialTheme.typography.bodySmall)
                }
                androidx.compose.foundation.layout.FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    com.denil.profe.domain.Cefr.entries.forEach { lvl ->
                        FilterChip(selected = settings.level == lvl, onClick = { viewModel.setLevel(lvl) }, label = { Text(lvl.name) })
                    }
                }
                OutlinedButton(onClick = onPlacement, modifier = Modifier.fillMaxWidth()) { Text("Hacer el examen de ubicación") }
                SliderSetting(
                    label = "Meta diaria (XP)",
                    value = settings.dailyGoalXp,
                    range = 10..200,
                    step = 10,
                    onChange = viewModel::setDailyGoal
                )
            }

            SectionCard("Tutor con IA (Groq, gratis)") {
                Text(
                    "Cómo obtener tu key:\n1. Entra a console.groq.com e inicia sesión.\n2. Ve a «API Keys» → «Create API Key».\n3. Copia la key (empieza con gsk_) y pégala aquí.\nSe guarda solo en este teléfono.",
                    style = MaterialTheme.typography.bodySmall
                )
                TextButton(onClick = { uriHandler.openUri("https://console.groq.com/keys") }) { Text("Abrir console.groq.com/keys") }
                OutlinedTextField(
                    value = keyInput ?: settings.groqApiKey,
                    onValueChange = { keyInput = it.trim() },
                    label = { Text("API key de Groq") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (showKey) androidx.compose.ui.text.input.VisualTransformation.None
                        else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    trailingIcon = {
                        TextButton(onClick = { showKey = !showKey }) { Text(if (showKey) "Ocultar" else "Ver") }
                    }
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = { viewModel.saveGroqKey(keyInput ?: settings.groqApiKey); keyInput = null }) { Text("Guardar y probar") }
                    if (settings.hasGroqKey) {
                        TextButton(onClick = { viewModel.saveGroqKey(""); keyInput = null }) { Text("Borrar") }
                    }
                }
                when (val st = groqStatus) {
                    null -> Text(
                        if (settings.hasGroqKey) "Key guardada." else "Sin key: el tutor, la corrección con IA y los ejercicios extra estarán apagados.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    "" -> Text("Probando conexión…", style = MaterialTheme.typography.bodySmall)
                    "ok" -> Text("✔ ¡Funciona! El tutor ya está listo.", color = androidx.compose.ui.graphics.Color(0xFF2E7D32))
                    else -> Text("✘ $st", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
                OutlinedTextField(
                    value = modelInput ?: settings.groqModel,
                    onValueChange = { modelInput = it },
                    label = { Text("Modelo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = { Text("Recomendado: llama-3.3-70b-versatile. Si falla, la app usa llama-3.1-8b-instant.") },
                    trailingIcon = {
                        if (modelInput != null && modelInput != settings.groqModel) {
                            TextButton(onClick = { viewModel.setGroqModel(modelInput.orEmpty()); modelInput = null }) { Text("Guardar") }
                        }
                    }
                )
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text("El tutor lee sus respuestas en voz alta", modifier = Modifier.weight(1f))
                    Switch(checked = settings.tutorSpeaks, onCheckedChange = viewModel::setTutorSpeaks)
                }
            }

            SectionCard("Voz en inglés") {
                var rate by remember(settings.speechRate) { mutableStateOf(settings.speechRate) }
                Text("Velocidad: ${"%.2f".format(rate)}x")
                Slider(
                    value = rate,
                    onValueChange = { rate = it },
                    onValueChangeFinished = { viewModel.setSpeechRate(rate) },
                    valueRange = 0.5f..1.5f
                )
                OutlinedButton(onClick = viewModel::testVoice) { Text("Probar voz") }
                Text(
                    "Si no se escucha, instala la voz en inglés: Ajustes del teléfono → Administración general → Texto a voz.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            SectionCard("Límites diarios") {
                SliderSetting(
                    label = "Tarjetas nuevas por día",
                    value = settings.newPerDay,
                    range = 0..100,
                    onChange = viewModel::setNewPerDay
                )
                SliderSetting(
                    label = "Repasos por día",
                    value = settings.reviewsPerDay,
                    range = 0..500,
                    step = 10,
                    onChange = viewModel::setReviewsPerDay
                )
            }

            SectionCard("Recordatorio diario") {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text("Avisarme para repasar", modifier = Modifier.weight(1f))
                    Switch(
                        checked = settings.reminderEnabled,
                        onCheckedChange = { enable ->
                            if (enable && Build.VERSION.SDK_INT >= 33) {
                                notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                viewModel.setReminderEnabled(enable)
                            }
                        }
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text("Hora: %02d:%02d".format(settings.reminderHour, settings.reminderMinute), modifier = Modifier.weight(1f))
                    OutlinedButton(onClick = { showTimePicker = true }) { Text("Cambiar") }
                }
            }

            SectionCard("Apariencia") {
                Text("Tema", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ThemeMode.entries.forEach { mode ->
                        FilterChip(
                            selected = settings.themeMode == mode,
                            onClick = { viewModel.setThemeMode(mode) },
                            label = {
                                Text(
                                    when (mode) {
                                        ThemeMode.SYSTEM -> "Sistema"
                                        ThemeMode.LIGHT -> "Claro"
                                        ThemeMode.DARK -> "Oscuro"
                                    }
                                )
                            }
                        )
                    }
                }
                if (Build.VERSION.SDK_INT >= 31) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Text("Colores dinámicos (Material You)", modifier = Modifier.weight(1f))
                        Switch(checked = settings.dynamicColors, onCheckedChange = viewModel::setDynamicColors)
                    }
                }
            }

            SectionCard("Respaldo") {
                Text(
                    "Exporta todo (mazos, tarjetas, historial y progreso del curso) en un JSON. Al restaurar un respaldo de Profe se reemplaza todo; si eliges un respaldo de la app Repaso, sus mazos se agregan sin borrar nada.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = {
                            val stamp = SimpleDateFormat("yyyyMMdd-HHmm", Locale.US).format(Date())
                            exportLauncher.launch("profe-respaldo-$stamp.json")
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text("Exportar") }
                    OutlinedButton(
                        onClick = { importLauncher.launch(arrayOf("application/json", "text/*", "application/octet-stream")) },
                        modifier = Modifier.weight(1f)
                    ) { Text("Restaurar") }
                }
            }

            SectionCard("Cursos incluidos") {
                if (courses.isEmpty()) {
                    Text("Cargando…", style = MaterialTheme.typography.bodySmall)
                }
                courses.forEach { course ->
                    val isInstalled = course.name in installed
                    Text(course.name, style = MaterialTheme.typography.titleSmall)
                    Text(
                        "${course.description} ${course.deckCount} mazos · ${course.cardCount} tarjetas.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { if (isInstalled) confirmCourse = course else viewModel.installCourse(course.id) },
                            modifier = Modifier.weight(1f)
                        ) { Text(if (isInstalled) "Añadir de nuevo" else "Añadir curso") }
                        if (isInstalled) {
                            OutlinedButton(onClick = { confirmDeleteCourse = course }, modifier = Modifier.weight(1f)) {
                                Text("Eliminar curso")
                            }
                        }
                    }
                }
            }

            SectionCard("Mazo de ejemplo") {
                Text(
                    "Añade el mazo de Repaso con 50 palabras de inglés (B1-B2).",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedButton(onClick = viewModel::restoreSampleDeck) { Text("Añadir mazo de ejemplo") }
            }

            Text(
                "Profe · Tus datos se guardan solo en este teléfono. Internet se usa únicamente para el diccionario, el corrector y el tutor con IA.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    if (showTimePicker) {
        val timeState = rememberTimePickerState(
            initialHour = settings.reminderHour,
            initialMinute = settings.reminderMinute,
            is24Hour = true
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Hora del recordatorio") },
            text = { TimePicker(state = timeState) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.setReminderTime(timeState.hour, timeState.minute)
                    showTimePicker = false
                }) { Text("Guardar") }
            },
            dismissButton = { TextButton(onClick = { showTimePicker = false }) { Text("Cancelar") } }
        )
    }

    confirmCourse?.let { course ->
        AlertDialog(
            onDismissRequest = { confirmCourse = null },
            title = { Text("El curso ya está en tus mazos") },
            text = { Text("Si lo añades de nuevo se crearán mazos duplicados con progreso en cero. ¿Quieres continuar?") },
            confirmButton = {
                TextButton(onClick = { viewModel.installCourse(course.id); confirmCourse = null }) { Text("Añadir de nuevo") }
            },
            dismissButton = { TextButton(onClick = { confirmCourse = null }) { Text("Cancelar") } }
        )
    }

    confirmDeleteCourse?.let { course ->
        AlertDialog(
            onDismissRequest = { confirmDeleteCourse = null },
            title = { Text("¿Eliminar el curso «${course.name}»?") },
            text = { Text("Se borrarán todos sus mazos, tarjetas e historial. Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteCourse(course.name); confirmDeleteCourse = null }) { Text("Eliminar") }
            },
            dismissButton = { TextButton(onClick = { confirmDeleteCourse = null }) { Text("Cancelar") } }
        )
    }

    confirmRestore?.let { uri ->
        AlertDialog(
            onDismissRequest = { confirmRestore = null },
            title = { Text("¿Restaurar respaldo?") },
            text = { Text("Si es un respaldo de Profe, se reemplazarán todos tus datos actuales. Si es de Repaso, sus mazos se agregarán a los tuyos.") },
            confirmButton = {
                TextButton(onClick = { viewModel.importBackup(uri); confirmRestore = null }) { Text("Restaurar") }
            },
            dismissButton = { TextButton(onClick = { confirmRestore = null }) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            content()
        }
    }
}

@Composable
private fun SliderSetting(label: String, value: Int, range: IntRange, step: Int = 1, onChange: (Int) -> Unit) {
    var local by remember(value) { mutableStateOf(value.toFloat()) }
    Column {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(label, modifier = Modifier.weight(1f))
            Spacer(Modifier.width(8.dp))
            Text(local.toInt().toString(), style = MaterialTheme.typography.titleMedium)
        }
        Slider(
            value = local,
            onValueChange = { local = (it / step).toInt() * step.toFloat() },
            onValueChangeFinished = { onChange(local.toInt()) },
            valueRange = range.first.toFloat()..range.last.toFloat()
        )
        Spacer(Modifier.height(4.dp))
    }
}
