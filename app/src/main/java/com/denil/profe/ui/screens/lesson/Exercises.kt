package com.denil.profe.ui.screens.lesson

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.denil.profe.data.curriculum.Exercise
import com.denil.profe.data.curriculum.ExerciseType
import com.denil.profe.domain.AnswerChecker
import com.denil.profe.ui.components.WordDiff
import com.denil.profe.ui.components.inlineMarkup
import com.denil.profe.ui.components.rememberSpeechInput
import kotlinx.coroutines.launch

/** Resultado de un ejercicio: true/false, o null si se saltó (no cuenta para la nota). */
typealias ExerciseResult = Boolean?

data class AiVerdict(val correct: Boolean, val feedback: String, val better: String)

private val Green = Color(0xFF2E7D32)

private fun typeLabel(type: ExerciseType) = when (type) {
    ExerciseType.CHOICE -> "Elige la respuesta"
    ExerciseType.FILL -> "Completa"
    ExerciseType.TRANSLATE -> "Traduce al inglés"
    ExerciseType.ORDER -> "Ordena la oración"
    ExerciseType.LISTEN -> "Dictado: escucha y escribe"
    ExerciseType.SPEAK -> "Pronunciación: dilo en voz alta"
}

/**
 * Un ejercicio interactivo. Usa `key(...)` desde fuera para reiniciar el estado entre ejercicios.
 */
@Composable
fun ExerciseView(
    exercise: Exercise,
    onSpeak: (String, Boolean) -> Unit,
    onFinished: (ExerciseResult) -> Unit,
    aiCheck: (suspend (Exercise, String) -> AiVerdict)? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(typeLabel(exercise.type), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        when (exercise.type) {
            ExerciseType.CHOICE -> ChoiceExercise(exercise, onSpeak, onFinished)
            ExerciseType.FILL -> TypedExercise(exercise, onSpeak, onFinished, aiCheck = null)
            ExerciseType.TRANSLATE -> TypedExercise(exercise, onSpeak, onFinished, aiCheck)
            ExerciseType.ORDER -> OrderExercise(exercise, onSpeak, onFinished)
            ExerciseType.LISTEN -> ListenExercise(exercise, onSpeak, onFinished)
            ExerciseType.SPEAK -> SpeakExercise(exercise, onSpeak, onFinished)
        }
    }
}

@Composable
private fun Feedback(correct: Boolean, title: String? = null, body: String = "", extra: @Composable () -> Unit = {}) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (correct) Color(0x1A2E7D32) else MaterialTheme.colorScheme.errorContainer
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                title ?: if (correct) "¡Correcto!" else "No exactamente",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (correct) Green else MaterialTheme.colorScheme.onErrorContainer
            )
            if (body.isNotBlank()) Text(inlineMarkup(body), style = MaterialTheme.typography.bodyMedium)
            extra()
        }
    }
}

@Composable
private fun ContinueButton(onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth()) { Text("Continuar") }
}

@Composable
private fun ChoiceExercise(exercise: Exercise, onSpeak: (String, Boolean) -> Unit, onFinished: (ExerciseResult) -> Unit) {
    var picked by rememberSaveable { mutableStateOf(-1) }
    Text(inlineMarkup(exercise.prompt), style = MaterialTheme.typography.titleLarge)
    exercise.options.forEachIndexed { i, option ->
        val answered = picked >= 0
        val isCorrect = i == exercise.answerIndex
        val colors = when {
            !answered -> ButtonDefaults.outlinedButtonColors()
            isCorrect -> ButtonDefaults.outlinedButtonColors(containerColor = Color(0x332E7D32))
            i == picked -> ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            else -> ButtonDefaults.outlinedButtonColors()
        }
        OutlinedButton(
            onClick = { if (!answered) picked = i },
            colors = colors,
            border = BorderStroke(1.dp, if (answered && isCorrect) Green else MaterialTheme.colorScheme.outline),
            modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)
        ) { Text(option, style = MaterialTheme.typography.bodyLarge) }
    }
    if (picked >= 0) {
        val ok = picked == exercise.answerIndex
        val answerText = exercise.options.getOrNull(exercise.answerIndex).orEmpty()
        Feedback(ok, body = buildString {
            if (!ok) append("Respuesta: **$answerText**\n")
            append(exercise.explain)
        })
        // Si la oración tiene hueco, la escuchamos completa.
        if (exercise.prompt.contains("___")) {
            TextButton(onClick = { onSpeak(exercise.prompt.replace("___", answerText), false) }) {
                Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null)
                Text("Escuchar la oración", modifier = Modifier.padding(start = 6.dp))
            }
        }
        ContinueButton { onFinished(ok) }
    }
}

@Composable
private fun TypedExercise(
    exercise: Exercise,
    onSpeak: (String, Boolean) -> Unit,
    onFinished: (ExerciseResult) -> Unit,
    aiCheck: (suspend (Exercise, String) -> AiVerdict)?
) {
    var input by rememberSaveable { mutableStateOf("") }
    var checked by rememberSaveable { mutableStateOf(false) }
    var correct by rememberSaveable { mutableStateOf(false) }
    var ai by remember { mutableStateOf<AiVerdict?>(null) }
    var aiLoading by remember { mutableStateOf(false) }
    var aiError by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val isFill = exercise.type == ExerciseType.FILL

    Text(inlineMarkup(exercise.prompt), style = MaterialTheme.typography.titleLarge)
    if (exercise.hint.isNotBlank()) Text("Pista: ${exercise.hint}", style = MaterialTheme.typography.bodySmall)
    OutlinedTextField(
        value = input,
        onValueChange = { if (!checked) input = it },
        label = { Text(if (isFill) "Lo que va en el espacio" else "Tu traducción en inglés") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = isFill,
        keyboardOptions = KeyboardOptions(
            capitalization = if (isFill) KeyboardCapitalization.None else KeyboardCapitalization.Sentences,
            autoCorrectEnabled = false,
            imeAction = ImeAction.Done
        ),
        readOnly = checked
    )
    if (!checked) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                correct = AnswerChecker.matches(input, exercise.answers)
                checked = true
            }, enabled = input.isNotBlank()) { Text("Comprobar") }
            TextButton(onClick = { onFinished(null) }) { Text("Saltar") }
        }
    } else {
        val model = exercise.answers.first()
        val full = if (isFill) exercise.prompt.replace("___", model) else model
        val verdict = ai
        when {
            correct -> Feedback(true, body = exercise.explain)
            verdict != null -> Feedback(
                verdict.correct,
                title = if (verdict.correct) "¡Tu traducción es válida!" else "Revisión del profe",
                body = verdict.feedback + if (verdict.better.isNotBlank()) "\nMás natural: **${verdict.better}**" else ""
            )
            else -> Feedback(false, body = buildString {
                append("Respuesta esperada: **$model**")
                val others = exercise.answers.drop(1).take(2)
                if (others.isNotEmpty()) append("\nTambién vale: " + others.joinToString(" · "))
                if (exercise.explain.isNotBlank()) append("\n${exercise.explain}")
            })
        }
        TextButton(onClick = { onSpeak(full, false) }) {
            Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null)
            Text("Escuchar", modifier = Modifier.padding(start = 6.dp))
        }
        if (!correct && verdict == null && exercise.type == ExerciseType.TRANSLATE) {
            aiError?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                if (aiCheck != null) {
                    FilledTonalButton(onClick = {
                        aiLoading = true; aiError = null
                        scope.launch {
                            try { ai = aiCheck(exercise, input) } catch (e: Exception) { aiError = e.message ?: "No se pudo revisar" }
                            aiLoading = false
                        }
                    }, enabled = !aiLoading) {
                        if (aiLoading) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                        else Icon(Icons.Filled.AutoAwesome, contentDescription = null)
                        Text("Revisar con IA", modifier = Modifier.padding(start = 6.dp))
                    }
                }
                TextButton(onClick = { onFinished(true) }) { Text("La mía también vale") }
            }
        }
        ContinueButton { onFinished(correct || verdict?.correct == true) }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun OrderExercise(exercise: Exercise, onSpeak: (String, Boolean) -> Unit, onFinished: (ExerciseResult) -> Unit) {
    val tokens = remember(exercise) { AnswerChecker.orderTokens(exercise.target) }
    val shuffled = remember(exercise) {
        val idx = tokens.indices.toMutableList()
        // Mezcla garantizando que no quede igual (si hay más de una ficha).
        do { idx.shuffle() } while (idx.size > 1 && idx == tokens.indices.toList())
        idx
    }
    var chosen by remember(exercise) { mutableStateOf(listOf<Int>()) }
    var checked by remember(exercise) { mutableStateOf(false) }
    var correct by remember(exercise) { mutableStateOf(false) }

    if (exercise.prompt.isNotBlank()) Text("«${exercise.prompt}»", style = MaterialTheme.typography.titleMedium)
    Card(Modifier.fillMaxWidth().heightIn(min = 64.dp)) {
        FlowRow(Modifier.padding(10.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            chosen.forEach { i ->
                InputChip(selected = true, onClick = { if (!checked) chosen = chosen - i }, label = { Text(tokens[i]) })
            }
        }
    }
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        shuffled.filter { it !in chosen }.forEach { i ->
            AssistChip(onClick = { if (!checked) chosen = chosen + i }, label = { Text(tokens[i]) })
        }
    }
    if (!checked) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                correct = AnswerChecker.matches(chosen.joinToString(" ") { tokens[it] }, exercise.answers)
                checked = true
                onSpeak(exercise.target, false)
            }, enabled = chosen.size == tokens.size) { Text("Comprobar") }
            TextButton(onClick = { chosen = emptyList() }) { Text("Borrar") }
        }
    } else {
        Feedback(correct, body = if (correct) "" else "Orden correcto: **${exercise.target}**")
        ContinueButton { onFinished(correct) }
    }
}

@Composable
private fun ListenExercise(exercise: Exercise, onSpeak: (String, Boolean) -> Unit, onFinished: (ExerciseResult) -> Unit) {
    var input by rememberSaveable { mutableStateOf("") }
    var result by remember { mutableStateOf<AnswerChecker.Comparison?>(null) }
    LaunchedEffect(exercise) { onSpeak(exercise.target, false) }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilledTonalButton(onClick = { onSpeak(exercise.target, false) }) {
            Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null)
            Text("Escuchar", modifier = Modifier.padding(start = 6.dp))
        }
        OutlinedButton(onClick = { onSpeak(exercise.target, true) }) {
            Icon(Icons.Filled.SlowMotionVideo, contentDescription = null)
            Text("Lento", modifier = Modifier.padding(start = 6.dp))
        }
    }
    OutlinedTextField(
        value = input,
        onValueChange = { if (result == null) input = it },
        label = { Text("Escribe lo que escuchas") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, autoCorrectEnabled = false),
        readOnly = result != null
    )
    val r = result
    if (r == null) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { result = AnswerChecker.compare(exercise.target, input) }, enabled = input.isNotBlank()) { Text("Comprobar") }
            TextButton(onClick = { onFinished(null) }) { Text("Saltar") }
        }
    } else {
        val ok = r.score >= 90
        Feedback(ok, title = if (r.perfect) "¡Perfecto!" else "${r.score}% correcto") {
            WordDiff(r)
            Text("Frase: ${exercise.target}", style = MaterialTheme.typography.bodyMedium)
            if (exercise.prompt.isNotBlank()) Text("Significa: ${exercise.prompt}", style = MaterialTheme.typography.bodySmall)
        }
        ContinueButton { onFinished(ok) }
    }
}

@Composable
private fun SpeakExercise(exercise: Exercise, onSpeak: (String, Boolean) -> Unit, onFinished: (ExerciseResult) -> Unit) {
    var result by remember { mutableStateOf<AnswerChecker.Comparison?>(null) }
    var heard by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var attempts by remember { mutableStateOf(0) }
    val listen = rememberSpeechInput(
        prompt = exercise.target,
        onResult = { options ->
            error = null
            attempts++
            val best = options.map { it to AnswerChecker.compare(exercise.target, it) }.maxBy { it.second.score }
            heard = best.first
            result = best.second
        },
        onError = { error = it }
    )

    Text(exercise.target, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
    if (exercise.prompt.isNotBlank()) Text(exercise.prompt, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedButton(onClick = { onSpeak(exercise.target, false) }) {
            Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null)
            Text("Modelo", modifier = Modifier.padding(start = 6.dp))
        }
        Button(onClick = listen) {
            Icon(Icons.Filled.Mic, contentDescription = null)
            Text(if (attempts == 0) "Hablar" else "Otra vez", modifier = Modifier.padding(start = 6.dp))
        }
    }
    error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
    val r = result
    AnimatedVisibility(visible = r != null) {
        if (r != null) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Feedback(r.score >= 80, title = if (r.score >= 80) "¡Bien dicho! ${r.score}%" else "${r.score}% — inténtalo otra vez") {
                    WordDiff(r)
                    Text("Te entendí: \"$heard\"", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        if (r != null) Button(onClick = { onFinished(r.score >= 80) }) { Text("Continuar") }
        TextButton(onClick = { onFinished(null) }) { Text(if (r == null) "Saltar (no puedo hablar ahora)" else "Saltar") }
    }
}
