package com.closetmixer.android.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.closetmixer.android.ui.component.OutfitRow
import com.closetmixer.presentation.viewmodel.OutfitViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutfitScreen(viewModel: OutfitViewModel = koinInject()) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Générer une tenue") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            when {
                state.isLoading -> CircularProgressIndicator()
                state.generatedOutfit != null -> {
                    Text("Tenue générée", style = MaterialTheme.typography.titleMedium)
                    val outfit = state.generatedOutfit!!
                    OutfitRow(
                        articles = listOf(
                            outfit.haut, outfit.bas, outfit.chaussure,
                            outfit.bijou, outfit.couvreChef
                        )
                    )
                    outfit.haut?.let { Text("Haut : ${it.sousCategorie} ${it.couleur ?: ""}") }
                    outfit.bas?.let { Text("Bas : ${it.sousCategorie} ${it.couleur ?: ""}") }
                    outfit.chaussure?.let { Text("Chaussures : ${it.sousCategorie} ${it.couleur ?: ""}") }
                    outfit.bijou?.let { Text("Bijou : ${it.sousCategorie}") }
                }
                else -> {
                    Spacer(Modifier.weight(1f))
                    Text(
                        "Appuyez sur le bouton pour générer une tenue",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.weight(1f))
                }
            }

            Button(
                onClick = { viewModel.generate(state.culturalStyle) },
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text("Générer une tenue")
            }
        }
    }
}
