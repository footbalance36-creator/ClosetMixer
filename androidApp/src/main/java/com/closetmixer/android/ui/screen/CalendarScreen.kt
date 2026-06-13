package com.closetmixer.android.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.closetmixer.data.model.Article
import com.closetmixer.data.model.CalendarEntry
import com.closetmixer.data.model.Tenue
import com.closetmixer.domain.usecase.GeneratedOutfit
import com.closetmixer.presentation.viewmodel.CalendarUiState
import com.closetmixer.presentation.viewmodel.CalendarViewModel
import com.closetmixer.presentation.viewmodel.CalendarViewMode
import org.koin.compose.koinInject
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(viewModel: CalendarViewModel = koinInject()) {
    val state by viewModel.uiState.collectAsState()

    val yearMonth = remember(state.currentMonth) {
        if (state.currentMonth.isEmpty()) YearMonth.now()
        else {
            val parts = state.currentMonth.split("-")
            YearMonth.of(parts[0].toInt(), parts[1].toInt())
        }
    }

    val today = LocalDate.now()
    val isCurrentMonth = yearMonth.year == today.year && yearMonth.monthValue == today.monthValue

    var selectedDay by remember(state.currentMonth) {
        mutableStateOf(if (isCurrentMonth) today.dayOfMonth else null)
    }

    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfWeek = yearMonth.atDay(1).dayOfWeek.value % 7

    val plannedDays = state.monthEntries.keys.mapNotNull {
        runCatching { LocalDate.parse(it).dayOfMonth }.getOrNull()
    }.toSet()

    if (state.showSheet) {
        ModalBottomSheet(onDismissRequest = { viewModel.closeSheet() }) {
            val dateLabel = state.selectedDate?.let { formatDate(it) } ?: ""
            TenuePickerContent(
                dateLabel = dateLabel,
                savedTenues = state.savedTenues,
                isSavedTenuesLoading = state.isSavedTenuesLoading,
                generatedOutfit = state.generatedOutfit,
                isGenerating = state.isGenerating,
                onSelectTenue = { tenueId ->
                    state.selectedDate?.let { viewModel.assignTenue(it, tenueId) }
                },
                onGenerate = { viewModel.generateOutfit() },
                onUseGenerated = {
                    state.selectedDate?.let { viewModel.useGeneratedOutfit(it) }
                }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // ── Header: titre + navigation ─────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Calendrier",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(start = 12.dp)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    if (state.viewMode == CalendarViewMode.WEEK) viewModel.prevWeek()
                    else viewModel.prevMonth()
                }) {
                    Icon(
                        Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Précédent",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    if (state.viewMode == CalendarViewMode.WEEK) weekRangeLabel(state)
                    else yearMonth.month.getDisplayName(TextStyle.FULL, Locale.FRENCH)
                        .replaceFirstChar { it.uppercase() } + " ${yearMonth.year}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = {
                    if (state.viewMode == CalendarViewMode.WEEK) viewModel.nextWeek()
                    else viewModel.nextMonth()
                }) {
                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        contentDescription = "Suivant",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // ── Toggle Mois / Semaine ──────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            listOf("Mois" to CalendarViewMode.MONTH, "Semaine" to CalendarViewMode.WEEK).forEach { (label, mode) ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (state.viewMode == mode) MaterialTheme.colorScheme.primaryContainer
                            else Color.Transparent
                        )
                        .clickable { viewModel.setViewMode(mode) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        label,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (state.viewMode == mode) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        if (state.viewMode == CalendarViewMode.MONTH) {
            // ── En-têtes jours semaine ─────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                listOf("D", "L", "M", "M", "J", "V", "S").forEach { label ->
                    Text(
                        label,
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.60f),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            val cells = buildList {
                repeat(firstDayOfWeek) { add(0) }
                addAll(1..daysInMonth)
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(((cells.size / 7 + 1) * 48).dp)
                    .padding(horizontal = 12.dp),
                userScrollEnabled = false
            ) {
                items(cells) { day ->
                    if (day == 0) {
                        Box(Modifier.aspectRatio(1f))
                    } else {
                        val isSelected = day == selectedDay
                        val isToday = isCurrentMonth && day == today.dayOfMonth
                        val hasEntry = day in plannedDays

                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .clip(CircleShape)
                                .then(
                                    if (isSelected) Modifier.background(
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                                    ) else Modifier
                                )
                                .clickable {
                                    selectedDay = day
                                    val dateStr = "${state.currentMonth}-${day.toString().padStart(2, '0')}"
                                    viewModel.selectDate(dateStr)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    day.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = when {
                                        isToday -> MaterialTheme.colorScheme.primary
                                        isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
                                        else -> MaterialTheme.colorScheme.onSurface
                                    },
                                    textAlign = TextAlign.Center
                                )
                                if (hasEntry) {
                                    Box(
                                        modifier = Modifier
                                            .size(4.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // ── Vue Semaine ────────────────────────────────────────────────
            WeekView(
                weekStart = state.weekStart,
                monthEntries = state.monthEntries,
                selectedDate = state.selectedDate,
                onDayClick = { dateStr -> viewModel.selectDate(dateStr) }
            )
        }

        Spacer(Modifier.height(16.dp))

        // ── Panel jour sélectionné ─────────────────────────────────────────
        val activeDateStr = if (state.viewMode == CalendarViewMode.MONTH) {
            selectedDay?.let { day -> "${state.currentMonth}-${day.toString().padStart(2, '0')}" }
        } else {
            state.selectedDate
        }

        activeDateStr?.let { dateStr ->
            val entry = state.monthEntries[dateStr]
            SelectedDayPanel(
                dateLabel = formatDate(dateStr),
                entry = entry,
                onPlanClick = { viewModel.openSheet(dateStr) },
                onModifyClick = { viewModel.openSheet(dateStr) },
                onRemoveClick = { viewModel.removeTenue(dateStr) }
            )
        }

        Spacer(Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp)
        ) {
            Text(
                "CE MOIS-CI",
                style = MaterialTheme.typography.labelLarge.copy(
                    letterSpacing = 1.2.sp, fontSize = 11.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.70f)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "${state.monthEntries.size} tenues planifiées",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun WeekView(
    weekStart: String,
    monthEntries: Map<String, Pair<CalendarEntry, List<Article>>>,
    selectedDate: String?,
    onDayClick: (String) -> Unit
) {
    if (weekStart.isEmpty()) return

    val weekDates = remember(weekStart) {
        val start = LocalDate.parse(weekStart)
        (0 until 7).map { i -> start.plusDays(i.toLong()).toString() }
    }
    val dayNames = listOf("Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim")
    val todayStr = LocalDate.now().toString()

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        weekDates.forEachIndexed { index, dateStr ->
            item(key = dateStr) {
                val entry = monthEntries[dateStr]
                val isToday = dateStr == todayStr
                val isSelected = dateStr == selectedDate
                val dayNumber = dateStr.substring(8).toIntOrNull() ?: 0
                val firstArticle = entry?.second?.firstOrNull()

                Column(
                    modifier = Modifier
                        .width(72.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            when {
                                isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                        )
                        .clickable { onDayClick(dateStr) }
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        dayNames[index],
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        dayNumber.toString(),
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isToday) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(3f / 4f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        if (firstArticle != null) {
                            AsyncImage(
                                model = firstArticle.photoPath,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectedDayPanel(
    dateLabel: String,
    entry: Pair<CalendarEntry, List<Article>>?,
    onPlanClick: () -> Unit,
    onModifyClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            dateLabel,
            style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 0.5.sp, fontSize = 12.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.70f)
        )

        if (entry?.first?.tenueId != null) {
            PlannedOutfitMiniatures(articles = entry.second)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onModifyClick, modifier = Modifier.weight(1f)) {
                    Text("Modifier")
                }
                OutlinedButton(
                    onClick = onRemoveClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Retirer")
                }
            }
        } else {
            Button(onClick = onPlanClick, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Planifier une tenue")
            }
        }
    }
}

@Composable
private fun PlannedOutfitMiniatures(articles: List<Article>) {
    val slotLabels = listOf("Haut", "Bas", "Chaussure", "Bijou")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        slotLabels.forEachIndexed { index, label ->
            val article = articles.getOrNull(index)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(3f / 4f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    if (article != null) {
                        AsyncImage(
                            model = article.photoPath,
                            contentDescription = article.sousCategorie,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
                Text(
                    text = article?.sousCategorie ?: label,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TenuePickerContent(
    dateLabel: String,
    savedTenues: List<Pair<Tenue, List<Article>>>,
    isSavedTenuesLoading: Boolean,
    generatedOutfit: GeneratedOutfit?,
    isGenerating: Boolean,
    onSelectTenue: (String) -> Unit,
    onGenerate: () -> Unit,
    onUseGenerated: () -> Unit
) {
    var selectedTab by rememberSaveable { mutableStateOf(0) }
    val tabs = listOf("Tenues sauvegardées", "Générer une tenue")

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            "Choisir une tenue pour le $dateLabel",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
        )

        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                )
            }
        }

        when (selectedTab) {
            0 -> SavedTenuesTab(
                tenues = savedTenues,
                isLoading = isSavedTenuesLoading,
                onSelect = onSelectTenue
            )
            1 -> GenerateOutfitTab(
                generatedOutfit = generatedOutfit,
                isGenerating = isGenerating,
                onGenerate = onGenerate,
                onUseGenerated = onUseGenerated
            )
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun SavedTenuesTab(
    tenues: List<Pair<Tenue, List<Article>>>,
    isLoading: Boolean,
    onSelect: (String) -> Unit
) {
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (tenues.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Aucune tenue sauvegardée.\nCréez des tenues depuis l'onglet Tenues.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
        ) {
            items(tenues) { (tenue, articles) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(tenue.id) }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    articles.take(4).forEach { article ->
                        AsyncImage(
                            model = article.photoPath,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        )
                    }
                    repeat(maxOf(0, 4 - articles.size)) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        )
                    }
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = tenue.nom,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )
            }
        }
    }
}

@Composable
private fun GenerateOutfitTab(
    generatedOutfit: GeneratedOutfit?,
    isGenerating: Boolean,
    onGenerate: () -> Unit,
    onUseGenerated: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            onClick = onGenerate,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isGenerating
        ) {
            if (isGenerating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(Modifier.width(8.dp))
            }
            Text(if (generatedOutfit == null) "Générer selon la météo du jour" else "Régénérer")
        }

        if (generatedOutfit != null) {
            PlannedOutfitMiniatures(
                articles = listOfNotNull(
                    generatedOutfit.haut,
                    generatedOutfit.bas,
                    generatedOutfit.chaussure,
                    generatedOutfit.bijou
                )
            )
            Button(onClick = onUseGenerated, modifier = Modifier.fillMaxWidth()) {
                Text("Utiliser cette tenue")
            }
        }
    }
}

private fun weekRangeLabel(state: CalendarUiState): String {
    if (state.weekStart.isEmpty()) return ""
    return runCatching {
        val start = LocalDate.parse(state.weekStart)
        val end = start.plusDays(6)
        val startFmt = DateTimeFormatter.ofPattern("d MMM", Locale.FRENCH)
        val endFmt = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.FRENCH)
        "${start.format(startFmt)} – ${end.format(endFmt)}"
    }.getOrElse { state.weekStart }
}

private fun formatDate(dateStr: String): String =
    runCatching {
        LocalDate.parse(dateStr)
            .format(DateTimeFormatter.ofPattern("EEEE d MMMM", Locale.FRENCH))
            .replaceFirstChar { it.uppercase() }
    }.getOrElse { dateStr }
