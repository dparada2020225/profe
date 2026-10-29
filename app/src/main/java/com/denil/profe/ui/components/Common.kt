package com.denil.profe.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.denil.profe.ui.navigation.Routes

private data class TopLevelItem(val route: String, val label: String, val icon: ImageVector)

private val topLevelItems = listOf(
    TopLevelItem(Routes.HOME, "Inicio", Icons.Filled.Home),
    TopLevelItem(Routes.COURSE, "Curso", Icons.Filled.School),
    TopLevelItem(Routes.PRACTICE, "Practicar", Icons.Filled.RecordVoiceOver),
    TopLevelItem(Routes.DECKS, "Palabras", Icons.Filled.Style),
    TopLevelItem(Routes.SETTINGS, "Ajustes", Icons.Filled.Settings)
)

@Composable
fun ProfeBottomBar(currentRoute: String?, onNavigate: (String) -> Unit) {
    NavigationBar {
        topLevelItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { if (currentRoute != item.route) onNavigate(item.route) },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label, maxLines = 1) }
            )
        }
    }
}

@Composable
fun EmptyState(title: String, subtitle: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
        Text(
            subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
