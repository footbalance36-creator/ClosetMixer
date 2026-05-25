package com.closetmixer.android.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.closetmixer.domain.model.AppLanguage
import com.closetmixer.domain.model.CulturalStyle
import com.closetmixer.presentation.viewmodel.SettingsViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Paramètres") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            ListItem(
                headlineContent = { Text("Mode sombre") },
                trailingContent = {
                    Switch(
                        checked = state.isDarkMode,
                        onCheckedChange = { viewModel.toggleDarkMode() }
                    )
                }
            )
            HorizontalDivider()
            ListItem(
                headlineContent = { Text("Langue") },
                supportingContent = { Text(state.language.nativeName) },
                trailingContent = { /* TODO: language picker */ }
            )
            HorizontalDivider()
            ListItem(
                headlineContent = { Text("Style culturel") },
                supportingContent = { Text(state.culturalStyle.key) },
                trailingContent = { /* TODO: style picker */ }
            )
        }
    }
}
