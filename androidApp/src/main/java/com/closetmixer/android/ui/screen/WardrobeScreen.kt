package com.closetmixer.android.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.closetmixer.android.ui.component.ArticleCard
import com.closetmixer.android.ui.component.CategoryChips
import com.closetmixer.presentation.viewmodel.WardrobeViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WardrobeScreen(viewModel: WardrobeViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Garde-robe") },
                actions = {
                    IconButton(onClick = { /* TODO: navigate to add article */ }) {
                        Icon(Icons.Default.Add, contentDescription = "Ajouter")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding)) {
            CategoryChips(
                selected = state.selectedCategory,
                onSelect = { viewModel.loadArticles(it) }
            )
            Spacer(Modifier.height(8.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.articles) { article ->
                    ArticleCard(
                        article = article,
                        onFavoriteClick = { viewModel.toggleFavorite(article.id) }
                    )
                }
            }
        }
    }
}
