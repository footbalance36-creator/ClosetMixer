package com.closetmixer.android.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.closetmixer.presentation.viewmodel.CalendarViewModel
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(viewModel: CalendarViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val now = LocalDate.now()
    val currentMonth = "${now.year}-${now.monthValue.toString().padStart(2, '0')}"

    LaunchedEffect(Unit) { viewModel.loadMonth(currentMonth) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Calendrier") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Mois : $currentMonth")
            Text("${state.entries.size} tenues planifiées")
            // TODO: Full calendar grid widget
        }
    }
}
