package com.closetmixer.android.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.closetmixer.android.ui.component.ArticleCard
import com.closetmixer.android.ui.component.CategoryChips
import com.closetmixer.android.ui.component.StitchChip
import com.closetmixer.android.util.copyImageToInternalStorage
import com.closetmixer.presentation.viewmodel.SettingsViewModel
import com.closetmixer.presentation.viewmodel.WardrobeViewModel
import org.koin.compose.koinInject

@Composable
fun WardrobeScreen(
    onAddClick: () -> Unit = {},
    viewModel: WardrobeViewModel = koinInject(),
    settingsViewModel: SettingsViewModel = koinInject()
) {
    val state by viewModel.uiState.collectAsState()
    val settings by settingsViewModel.uiState.collectAsState()
    val context = LocalContext.current

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val path = copyImageToInternalStorage(context, it)
            settingsViewModel.setProfilePhoto(path)
        }
    }

    Column(
        Modifier
            .fillMaxSize()
    ) {
            // ── Stitch-style top bar ───────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Profile photo circle — tap to change
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, MaterialTheme.colorScheme.primaryContainer, CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { imagePicker.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (settings.profilePhotoPath.isNotEmpty()) {
                            AsyncImage(
                                model = settings.profilePhotoPath,
                                contentDescription = "Photo de profil",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                Icons.Outlined.Person,
                                contentDescription = "Photo de profil",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Text(
                        text = "Closet Mixer",
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onAddClick) {
                    Icon(
                        Icons.Outlined.AddCircleOutline,
                        contentDescription = "Ajouter",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // ── Category chips ─────────────────────────────────────────────
            CategoryChips(
                selected = state.selectedCategory,
                onSelect = { viewModel.loadArticles(it) }
            )

            // ── Favoris + couleur chips ────────────────────────────────────
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                item {
                    StitchChip(
                        label = "♥ Favoris",
                        isSelected = state.favorisOnly,
                        onClick = { viewModel.toggleFavoris() }
                    )
                }
                items(state.availableColors) { color ->
                    StitchChip(
                        label = color.replaceFirstChar { it.uppercase() },
                        isSelected = state.selectedColor == color,
                        onClick = { viewModel.filterByColor(color) }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // ── Content ────────────────────────────────────────────────────
            when {
                state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }

                state.articles.isEmpty() -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Outlined.Checkroom,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Aucun article dans cette catégorie",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                else -> LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.articles) { article ->
                        ArticleCard(
                            article = article,
                            onFavoriteClick = { viewModel.toggleFavorite(article.id) },
                            onDeleteClick = { viewModel.deleteArticle(article.id) }
                        )
                    }
                }
            }
        }
}
