package com.denil.profe.ui.components

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.denil.profe.domain.AnswerChecker

/** Convierte **negrita** y *cursiva* en texto con estilo. */
fun inlineMarkup(text: String, boldColor: Color = Color.Unspecified): AnnotatedString = buildAnnotatedString {
    val regex = Regex("\\*\\*(.+?)\\*\\*|\\*(.+?)\\*")
    var last = 0
    for (m in regex.findAll(text)) {
        append(text.substring(last, m.range.first))
        val bold = m.groups[1]?.value
        if (bold != null) {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = boldColor)) { append(bold) }
        } else {
            withStyle(SpanStyle(fontStyle = FontStyle.Italic)) { append(m.groups[2]?.value.orEmpty()) }
        }
        last = m.range.last + 1
    }
    append(text.substring(last))
}

/**
 * Muestra el cuerpo de una sección del curso:
 * párrafos, viñetas ("- "), ejemplos ("> ", se pueden escuchar) y **negrita**.
 */
@Composable
fun LessonBody(body: String, onSpeak: (String) -> Unit, modifier: Modifier = Modifier) {
    val accent = MaterialTheme.colorScheme.primary
    Column(modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        body.lines().forEach { raw ->
            val line = raw.trimEnd()
            when {
                line.isBlank() -> Unit
                line.startsWith("- ") -> Row {
                    Text("•  ", color = accent, style = MaterialTheme.typography.bodyLarge)
                    Text(inlineMarkup(line.removePrefix("- "), accent), style = MaterialTheme.typography.bodyLarge)
                }
                line.startsWith("> ") -> {
                    val sentence = line.removePrefix("> ")
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.fillMaxWidth().clickable { onSpeak(sentence.replace("*", "")) }
                    ) {
                        Row(Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Escuchar",
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                inlineMarkup(sentence),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
                else -> Text(inlineMarkup(line, accent), style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
fun SpeakIcon(text: String, onSpeak: (String) -> Unit, modifier: Modifier = Modifier) {
    IconButton(onClick = { onSpeak(text) }, modifier = modifier) {
        Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Escuchar")
    }
}

/** Palabras de la frase objetivo en verde (correctas) o rojo (faltantes). */
@Composable
fun WordDiff(comparison: AnswerChecker.Comparison, modifier: Modifier = Modifier) {
    val ok = Color(0xFF2E7D32)
    val bad = MaterialTheme.colorScheme.error
    val text = buildAnnotatedString {
        comparison.target.forEachIndexed { i, w ->
            if (i > 0) append(" ")
            withStyle(SpanStyle(color = if (w.ok) ok else bad, fontWeight = if (w.ok) FontWeight.Normal else FontWeight.Bold)) {
                append(w.word)
            }
        }
    }
    Column(modifier) {
        Text(text, style = MaterialTheme.typography.titleMedium)
        if (comparison.extra.isNotEmpty()) {
            Text(
                "De más: " + comparison.extra.joinToString(" "),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Reconocimiento de voz en inglés usando el servicio del sistema (Google).
 * Devuelve una función que abre el micrófono.
 */
@Composable
fun rememberSpeechInput(
    prompt: String = "Habla en inglés…",
    onResult: (List<String>) -> Unit,
    onError: (String) -> Unit
): () -> Unit {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val list = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).orEmpty()
            if (list.isEmpty()) onError("No te escuché. Intenta de nuevo.") else onResult(list)
        }
    }
    return remember(launcher) {
        {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en-US")
                putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
                putExtra(RecognizerIntent.EXTRA_PROMPT, prompt)
            }
            try {
                launcher.launch(intent)
            } catch (e: ActivityNotFoundException) {
                onError("Tu teléfono no tiene reconocimiento de voz. Instala o actualiza la app de Google.")
            }
        }
    }
}

/** Tarjeta que avisa que falta la key de Groq. */
@Composable
fun GroqKeyMissingCard(onOpenSettings: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Key, contentDescription = null)
                Text(
                    "Falta tu API key de Groq",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            Text(
                "El tutor con IA usa Groq (gratis). Pega tu key en Ajustes → Tutor con IA y listo.",
                style = MaterialTheme.typography.bodyMedium
            )
            OutlinedButton(onClick = onOpenSettings) { Text("Ir a Ajustes") }
        }
    }
}

@Composable
fun LevelBadge(level: String, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
    ) {
        Text(
            level,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

