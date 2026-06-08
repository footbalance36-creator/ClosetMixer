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
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.closetmixer.data.model.Article
import com.closetmixer.presentation.viewmodel.CalendarViewModel
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
    val firstDayOfWeek = yearMonth.atDay(1).dayOfWeek.value % 7 // 0=Sun

    val plannedDays = state.entries.keys.mapNotNull {
        runCatching { LocalDate.parse(it).dayOfMonth }.getOrNull()
    }.toSet()

    // ── Article picker bottom sheet ───────────────────────────────────────
    if (state.showArticlePicker) {
        ModalBottomSheet(onDismissRequest = { viewModel.closePicker() }) {
            ArticlePickerContent(
                articles = state.articles,
                onSelect = { articleId ->
                    state.selectedDate?.let { date -> viewModel.savePlannedOutfit(date, articleId) }
                }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // ── Header with month navigation ───────────────────────────────────
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
                IconButton(onClick = { viewModel.prevMonth() }) {
                    Icon(
                        Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Mois précédent",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    yearMonth.month.getDisplayName(TextStyle.FULL, Locale.FRENCH)
                        .replaceFirstChar { it.uppercase() } + " ${yearMonth.year}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = { viewModel.nextMonth() }) {
                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        contentDescription = "Mois suivant",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // ── Day of week labels ─────────────────────────────────────────────
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

        // ── Calendar grid ──────────────────────────────────────────────────
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

        Spacer(Modifier.height(16.dp))

        // ── Selected day panel ─────────────────────────────────────────────
        selectedDay?.let { day ->
            val dateStr = "${state.currentMonth}-${day.toString().padStart(2, '0')}"
            val entry = state.entries[dateStr]
            val dateLabel = runCatching {
                LocalDate.parse(dateStr)
                    .format(DateTimeFormatter.ofPattern("EEEE d MMMM", Locale.FRENCH))
                    .replaceFirstChar { it.uppercase() }
            }.getOrElse { dateStr }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(16.dp)
            ) {
                Text(
                    dateLabel,
                    style = MaterialTheme.typography.labelLarge.copy(
                        letterSpacing = 0.5.sp, fontSize = 12.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.70f)
                )
                Spacer(Modifier.height(12.dp))

                if (entry != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Tenue planifiée",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    TextButton(
                        onClick = { viewModel.openPicker(dateStr) },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Modifier")
                    }
                } else {
                    Button(
                        onClick = { viewModel.openPicker(dateStr) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Planifier une tenue")
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Month summary ──────────────────────────────────────────────────
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
                "${state.entries.size} tenues planifiées",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun ArticlePickerContent(
    articles: List<Article>,
    onSelect: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            "Choisir un article",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
        )

        if (articles.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Aucun article dans votre garde-robe.\nAjoutez des vêtements depuis l'onglet Garde-robe.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(articles) { article ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(article.id) }
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                article.sousCategorie.replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.bodyLarge
                            )
                            val detail = listOfNotNull(
                                article.categorie.replaceFirstChar { it.uppercase() },
                                article.couleur
                            ).joinToString(" · ")
                            Text(
                                detail,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (article.isFavori == 1L) {
                            Icon(
                                Icons.Outlined.FavoriteBorder,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                    )
                }
                item { Spacer(Modifier.height(32.dp)) }
            }
        }
    }
}
