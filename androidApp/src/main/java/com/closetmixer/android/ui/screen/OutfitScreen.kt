package com.closetmixer.android.ui.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.closetmixer.android.ui.component.StitchChip
import com.closetmixer.android.ui.component.WeatherBanner
import com.closetmixer.android.util.shareOutfit
import com.closetmixer.data.model.Article
import com.closetmixer.domain.model.CulturalStyle
import com.closetmixer.presentation.viewmodel.OutfitViewModel
import org.koin.compose.koinInject

private val culturalStyles = listOf("Neutre", "Modeste", "K-Fashion", "J-Fashion", "Traditionnel")
private val occasions = listOf("Travail", "Casual", "Soirée", "Voyage", "Sport")

@Composable
fun OutfitScreen(viewModel: OutfitViewModel = koinInject()) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var selectedStyle by remember { mutableStateOf(culturalStyles.first()) }
    var selectedOccasion by remember { mutableStateOf(occasions[1]) }
    val locationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) fetchAndLoadWeather(context, viewModel)
    }

    LaunchedEffect(Unit) {
        viewModel.generate()
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (hasPermission) fetchAndLoadWeather(context, viewModel)
        else locationLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
            // ── Top bar ────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Closet Mixer",
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (state.generatedOutfit != null) {
                        IconButton(onClick = { shareOutfit(context, state.generatedOutfit!!) }) {
                            Icon(
                                Icons.Outlined.Share,
                                contentDescription = "Partager la tenue",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // ── Weather banner ─────────────────────────────────────────────
            state.weather?.let { weather ->
                WeatherBanner(
                    weather = weather,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
            }

            // ── Cultural style list ────────────────────────────────────────
            Column(Modifier.padding(horizontal = 20.dp)) {
                Text(
                    "Style culturel",
                    style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 1.2.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.70f)
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                culturalStyles.forEach { style ->
                    StitchChip(
                        label = style,
                        isSelected = selectedStyle == style,
                        onClick = {
                            selectedStyle = style
                            val mapped = when (style) {
                                "Modeste"      -> CulturalStyle.MODEST
                                "K-Fashion"    -> CulturalStyle.K_FASHION
                                "J-Fashion"    -> CulturalStyle.J_FASHION
                                "Traditionnel" -> CulturalStyle.TRADITIONAL
                                else           -> CulturalStyle.NEUTRAL
                            }
                            viewModel.generate(mapped)
                        }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Occasion list ──────────────────────────────────────────────
            Column(Modifier.padding(horizontal = 20.dp)) {
                Text(
                    "Occasion",
                    style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 1.2.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.70f)
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                occasions.forEach { occ ->
                    StitchChip(
                        label = occ,
                        isSelected = selectedOccasion == occ,
                        onClick = { selectedOccasion = occ }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── "Today's Mix" section title ────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Today's Mix", style = MaterialTheme.typography.headlineMedium)
            }

            Spacer(Modifier.height(12.dp))

            // ── Outfit display ─────────────────────────────────────────────
            when {
                state.isLoading -> Box(
                    Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }

                state.generatedOutfit != null -> {
                    val outfit = state.generatedOutfit!!
                    AsymmetricOutfitGrid(
                        top = outfit.haut,
                        bottom = outfit.bas,
                        shoe = outfit.chaussure,
                        accessory = outfit.bijou,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }

                else -> Box(
                    Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Générez votre première tenue",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Generate button ────────────────────────────────────────────
            Button(
                onClick = {
                    val mapped = when (selectedStyle) {
                        "Modeste"      -> CulturalStyle.MODEST
                        "K-Fashion"    -> CulturalStyle.K_FASHION
                        "J-Fashion"    -> CulturalStyle.J_FASHION
                        "Traditionnel" -> CulturalStyle.TRADITIONAL
                        else           -> CulturalStyle.NEUTRAL
                    }
                    viewModel.generate(mapped)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(
                    "GÉNÉRER UNE TENUE",
                    style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 2.sp)
                )
            }

            Spacer(Modifier.height(32.dp))
        }
}

@android.annotation.SuppressLint("MissingPermission")
private fun fetchAndLoadWeather(context: Context, viewModel: OutfitViewModel) {
    val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        ?: lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        ?: lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
    location?.let { viewModel.loadWeather(it.latitude, it.longitude) }
}

@Composable
private fun AsymmetricOutfitGrid(
    top: Article?,
    bottom: Article?,
    shoe: Article?,
    accessory: Article?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Row 1: top (hero, wider) + accessories (stacked)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutfitItemCard(
                article = top,
                label = "Haut",
                modifier = Modifier
                    .weight(7f)
                    .aspectRatio(3f / 4f)
            )
            Column(
                modifier = Modifier
                    .weight(5f)
                    .aspectRatio(3f / 4f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutfitItemCard(
                    article = shoe,
                    label = "Chaussures",
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
                OutfitItemCard(
                    article = accessory,
                    label = "Bijou",
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
        }

        // Row 2: bottom full width
        OutfitItemCard(
            article = bottom,
            label = "Bas",
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        )
    }
}

@Composable
private fun OutfitItemCard(
    article: Article?,
    label: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(16.dp), ambientColor = Color(0x08000000), spotColor = Color(0x08000000))
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        article?.let {
            AsyncImage(
                model = it.photoPath,
                contentDescription = it.sousCategorie,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Frosted label pill at bottom-left
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.80f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = article?.let {
                    val base = it.sousCategorie.uppercase()
                    val metalPart = it.metal?.takeIf { m -> m != "aucun" }?.uppercase()
                    if (metalPart != null) "$base · $metalPart" else base
                } ?: label.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp, fontSize = 9.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Bottom gradient overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.35f)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.10f))
                    )
                )
        )
    }
}
