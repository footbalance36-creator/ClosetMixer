package com.closetmixer.presentation.viewmodel

import com.closetmixer.data.model.Article
import com.closetmixer.data.model.CalendarEntry
import com.closetmixer.domain.usecase.GetArticlesByCategoryUseCase
import com.closetmixer.domain.usecase.GetWeatherUseCase
import com.closetmixer.domain.usecase.PlanOutfitUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

data class CalendarUiState(
    val entries: Map<String, CalendarEntry> = emptyMap(),
    val selectedDate: String? = null,
    val currentMonth: String = "",
    val isLoading: Boolean = false,
    val articles: List<Article> = emptyList(),
    val showArticlePicker: Boolean = false
)

class CalendarViewModel(
    private val planOutfitUseCase: PlanOutfitUseCase,
    private val getWeatherUseCase: GetWeatherUseCase,
    private val getArticlesUseCase: GetArticlesByCategoryUseCase
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        val now = Clock.System.todayIn(TimeZone.currentSystemDefault())
        loadMonth("${now.year}-${now.monthNumber.toString().padStart(2, '0')}")
    }

    fun loadMonth(yearMonth: String) {
        scope.launch {
            _uiState.update { it.copy(isLoading = true, currentMonth = yearMonth) }
            val entries = planOutfitUseCase.getMonthEntries(yearMonth).associateBy { it.date }
            _uiState.update { it.copy(entries = entries, isLoading = false) }
        }
    }

    fun prevMonth() = shiftMonth(-1)
    fun nextMonth() = shiftMonth(1)

    private fun shiftMonth(delta: Int) {
        val current = _uiState.value.currentMonth
        if (current.isEmpty()) return
        val parts = current.split("-")
        var year = parts[0].toInt()
        var month = parts[1].toInt() + delta
        if (month > 12) { month = 1; year++ }
        if (month < 1) { month = 12; year-- }
        loadMonth("$year-${month.toString().padStart(2, '0')}")
    }

    fun selectDate(date: String) {
        _uiState.update { it.copy(selectedDate = date) }
    }

    fun openPicker(date: String) {
        scope.launch {
            val articles = getArticlesUseCase.execute()
            _uiState.update { it.copy(selectedDate = date, articles = articles, showArticlePicker = true) }
        }
    }

    fun closePicker() {
        _uiState.update { it.copy(showArticlePicker = false) }
    }

    fun savePlannedOutfit(date: String, articleId: String) {
        scope.launch {
            planOutfitUseCase.execute(date, articleId)
            _uiState.update { it.copy(showArticlePicker = false) }
            loadMonth(_uiState.value.currentMonth)
        }
    }
}
