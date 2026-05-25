package com.closetmixer.android.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.closetmixer.presentation.viewmodel.VoyageViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoyageScreen(viewModel: VoyageViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Voyages") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO: create voyage dialog */ }) {
                Icon(Icons.Default.Add, contentDescription = "Nouveau voyage")
            }
        }
    ) { padding ->
        if (state.voyages.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text("Aucun voyage planifié")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.voyages) { voyage ->
                    Card(onClick = { viewModel.selectVoyage(voyage) }) {
                        ListItem(
                            headlineContent = { Text(voyage.nom) },
                            supportingContent = {
                                voyage.destination?.let { Text(it) }
                            },
                            trailingContent = {
                                voyage.dateDebut?.let { Text(it) }
                            }
                        )
                    }
                }
            }
        }
    }
}
