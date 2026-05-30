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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.closetmixer.android.util.copyImageToInternalStorage
import com.closetmixer.domain.model.ArticleCategory
import com.closetmixer.domain.model.ArticleSubCategory
import com.closetmixer.domain.model.CulturalStyle
import com.closetmixer.domain.model.Metal
import com.closetmixer.presentation.viewmodel.WardrobeViewModel
import org.koin.compose.koinInject
import java.util.UUID

data class ColorSwatch(val name: String, val color: Color)

val colorPalette = listOf(
    ColorSwatch("Blanc",       Color(0xFFFFFFFF)),
    ColorSwatch("Crème",       Color(0xFFFFFAE6)),
    ColorSwatch("Beige",       Color(0xFFF5DEB3)),
    ColorSwatch("Camel",       Color(0xFFC19A6B)),
    ColorSwatch("Marron",      Color(0xFF8B4513)),
    ColorSwatch("Gris clair",  Color(0xFFD3D3D3)),
    ColorSwatch("Gris",        Color(0xFF808080)),
    ColorSwatch("Gris foncé",  Color(0xFF404040)),
    ColorSwatch("Noir",        Color(0xFF1A1A1A)),
    ColorSwatch("Rouge",       Color(0xFFE53935)),
    ColorSwatch("Bordeaux",    Color(0xFF800020)),
    ColorSwatch("Rose",        Color(0xFFFF80AB)),
    ColorSwatch("Fuchsia",     Color(0xFFE91E96)),
    ColorSwatch("Corail",      Color(0xFFFF6F61)),
    ColorSwatch("Orange",      Color(0xFFFF6D00)),
    ColorSwatch("Jaune",       Color(0xFFFFD600)),
    ColorSwatch("Kaki",        Color(0xFF7B7C47)),
    ColorSwatch("Vert olive",  Color(0xFF6B8E23)),
    ColorSwatch("Vert",        Color(0xFF2E7D32)),
    ColorSwatch("Menthe",      Color(0xFF80CBC4)),
    ColorSwatch("Bleu ciel",   Color(0xFF64B5F6)),
    ColorSwatch("Bleu",        Color(0xFF1565C0)),
    ColorSwatch("Bleu marine", Color(0xFF0D1B4B)),
    ColorSwatch("Lavande",     Color(0xFFB39DDB)),
    ColorSwatch("Violet",      Color(0xFF6A1B9A)),
    ColorSwatch("Doré",        Color(0xFFFFD700)),
    ColorSwatch("Argenté",     Color(0xFFC0C0C0)),
    ColorSwatch("Multicolore", Color(0xFFFF6F00)),
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddArticleScreen(
    onNavigateBack: () -> Unit,
    viewModel: WardrobeViewModel = koinInject()
) {
    val context = LocalContext.current

    var photoPath by remember { mutableStateOf("") }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var selectedCategory by remember { mutableStateOf(ArticleCategory.VETEMENT) }
    var selectedSubCategory by remember { mutableStateOf<ArticleSubCategory?>(null) }
    var selectedColor by remember { mutableStateOf<ColorSwatch?>(null) }
    var selectedMetal by remember { mutableStateOf(Metal.AUCUN) }
    var selectedCulture by remember { mutableStateOf(CulturalStyle.NEUTRAL) }

    var categoryExpanded by remember { mutableStateOf(false) }
    var subCategoryExpanded by remember { mutableStateOf(false) }
    var metalExpanded by remember { mutableStateOf(false) }
    var cultureExpanded by remember { mutableStateOf(false) }

    val subCategories = ArticleSubCategory.entries.filter { it.category == selectedCategory }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedUri = it
            photoPath = copyImageToInternalStorage(context, it)
        }
    }

    val dropdownColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
    )

    Scaffold { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // ── Top bar ────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Outlined.ArrowBack, contentDescription = "Retour",
                        tint = MaterialTheme.colorScheme.primary)
                }
                Text(
                    "Ajouter un article",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // ── Photo picker ───────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { imagePicker.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedUri != null) {
                        AsyncImage(
                            model = selectedUri,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Outlined.AddPhotoAlternate,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Appuyer pour choisir une photo",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // ── Catégorie (list) ───────────────────────────────────────
                FormSectionLabel("Catégorie")
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedCategory.key.replaceFirstChar { it.uppercase() },
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        shape = RoundedCornerShape(12.dp),
                        colors = dropdownColors,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        ArticleCategory.entries.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.key.replaceFirstChar { it.uppercase() }) },
                                onClick = {
                                    selectedCategory = cat
                                    selectedSubCategory = null
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                // ── Sous-catégorie (list) ──────────────────────────────────
                FormSectionLabel("Sous-catégorie")
                ExposedDropdownMenuBox(
                    expanded = subCategoryExpanded,
                    onExpandedChange = { subCategoryExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedSubCategory?.key?.replaceFirstChar { it.uppercase() } ?: "Choisir…",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = subCategoryExpanded) },
                        shape = RoundedCornerShape(12.dp),
                        colors = dropdownColors,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = subCategoryExpanded,
                        onDismissRequest = { subCategoryExpanded = false }
                    ) {
                        subCategories.forEach { sub ->
                            DropdownMenuItem(
                                text = { Text(sub.key.replaceFirstChar { it.uppercase() }) },
                                onClick = {
                                    selectedSubCategory = sub
                                    subCategoryExpanded = false
                                }
                            )
                        }
                    }
                }

                // ── Couleur (color swatches) ───────────────────────────────
                FormSectionLabel("Couleur${selectedColor?.let { " — ${it.name}" } ?: ""}")
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    colorPalette.forEach { swatch ->
                        val isSelected = selectedColor == swatch
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(swatch.color)
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outlineVariant,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = swatch },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    Icons.Outlined.Check,
                                    contentDescription = null,
                                    tint = if (swatch.color.red * 0.299f + swatch.color.green * 0.587f + swatch.color.blue * 0.114f > 0.5f)
                                        Color.Black else Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                // ── Métal (bijoux uniquement, list) ────────────────────────
                if (selectedCategory == ArticleCategory.BIJOU) {
                    FormSectionLabel("Métal")
                    ExposedDropdownMenuBox(
                        expanded = metalExpanded,
                        onExpandedChange = { metalExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedMetal.key.replaceFirstChar { it.uppercase() },
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = metalExpanded) },
                            shape = RoundedCornerShape(12.dp),
                            colors = dropdownColors,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        )
                        ExposedDropdownMenu(
                            expanded = metalExpanded,
                            onDismissRequest = { metalExpanded = false }
                        ) {
                            Metal.entries.forEach { metal ->
                                DropdownMenuItem(
                                    text = { Text(metal.key.replaceFirstChar { it.uppercase() }) },
                                    onClick = { selectedMetal = metal; metalExpanded = false }
                                )
                            }
                        }
                    }
                }

                // ── Style culturel (list) ──────────────────────────────────
                FormSectionLabel("Style culturel")
                ExposedDropdownMenuBox(
                    expanded = cultureExpanded,
                    onExpandedChange = { cultureExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedCulture.key.replaceFirstChar { it.uppercase() },
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cultureExpanded) },
                        shape = RoundedCornerShape(12.dp),
                        colors = dropdownColors,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = cultureExpanded,
                        onDismissRequest = { cultureExpanded = false }
                    ) {
                        CulturalStyle.entries.forEach { style ->
                            DropdownMenuItem(
                                text = { Text(style.key.replaceFirstChar { it.uppercase() }) },
                                onClick = { selectedCulture = style; cultureExpanded = false }
                            )
                        }
                    }
                }

                // ── Save button ────────────────────────────────────────────
                Button(
                    onClick = {
                        val subCat = selectedSubCategory ?: subCategories.firstOrNull() ?: return@Button
                        viewModel.addArticle(
                            id = UUID.randomUUID().toString(),
                            photoPath = photoPath,
                            categorie = selectedCategory.key,
                            sousCategorie = subCat.key,
                            couleur = selectedColor?.name,
                            metal = if (selectedCategory == ArticleCategory.BIJOU) selectedMetal.key else null,
                            culture = selectedCulture.key,
                            onDone = onNavigateBack
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = selectedSubCategory != null,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        "ENREGISTRER",
                        style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 2.sp)
                    )
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun FormSectionLabel(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 0.8.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.70f)
    )
}
