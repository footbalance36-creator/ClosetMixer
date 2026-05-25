package com.closetmixer.android.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.closetmixer.android.ui.component.WeatherBanner
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
            state.weather?.let { WeatherBanner(weather = it) }

            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                state.generatedOutfit?.let { outfit ->
                    OutfitRow(
                        articles = listOf(
                            outfit.haut, outfit.bas, outfit.chaussure,
                            outfit.bijou, outfit.couvreChef
                        )
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(onClick = { viewModel.generate(state.culturalStyle) }) {
                Text("Générer une tenue")
            }
        }
    }
}
