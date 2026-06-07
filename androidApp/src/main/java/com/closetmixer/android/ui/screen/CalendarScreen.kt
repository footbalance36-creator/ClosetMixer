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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.closetmixer.presentation.viewmodel.CalendarViewModel
import org.koin.compose.koinInject
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarScreen(viewModel: CalendarViewModel = koinInject()) {
    val state by viewModel.uiState.collectAsState()
    val now = LocalDate.now()
    val currentMonth = "${now.year}-${now.monthValue.toString().padStart(2, '0')}"

    LaunchedEffect(Unit) { viewModel.loadMonth(currentMonth) }

    var selectedDay by remember { mutableStateOf(now.dayOfMonth) }
    val yearMonth = YearMonth.of(now.year, now.monthValue)
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfWeek = yearMonth.atDay(1).dayOfWeek.value % 7 // 0=Sun

    // Set of days with planned outfits (entries is Map<date, CalendarEntry>)
    val plannedDays = state.entries.keys.mapNotNull {
        runCatching { LocalDate.parse(it).dayOfMonth }.getOrNull()
    }.toSet()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
            // ── Header ─────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Calendrier", style = MaterialTheme.typography.headlineSmall)
                Text(
                    now.month.getDisplayName(TextStyle.FULL, Locale.FRENCH)
                        .replaceFirstChar { it.uppercase() } + " ${now.year}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // ── Day of week labels ─────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                listOf("D", "L", "M", "M", "J", "V", "S").forEach { day ->
                    Text(
                        day,
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.60f),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // ── Calendar grid ──────────────────────────────────────────────
            val cells = buildList {
                repeat(firstDayOfWeek) { add(0) } // empty leading cells
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
                        val isToday = day == now.dayOfMonth
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
                                .clickable { selectedDay = day },
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

            Spacer(Modifier.height(24.dp))

            // ── Planned count for month ────────────────────────────────────
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
                    style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 1.2.sp, fontSize = 11.sp),
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
