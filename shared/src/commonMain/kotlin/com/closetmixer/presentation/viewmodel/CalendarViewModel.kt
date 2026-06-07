package com.closetmixer.presentation.viewmodel

import com.closetmixer.data.model.CalendarEntry
import com.closetmixer.data.model.Tenue
import com.closetmixer.data.repository.TenueRepository
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
    val tenues: List<Tenue> = emptyList(),
    val showTenuePicker: Boolean = false
)

class CalendarViewModel(
    private val planOutfitUseCase: PlanOutfitUseCase,
    private val getWeatherUseCase: GetWeatherUseCase,
    private val tenueRepo: TenueRepository
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
            val tenues = tenueRepo.getAllTenues()
            _uiState.update { it.copy(selectedDate = date, tenues = tenues, showTenuePicker = true) }
        }
    }

    fun closePicker() {
        _uiState.update { it.copy(showTenuePicker = false) }
    }

    fun savePlannedOutfit(date: String, tenueId: String) {
        scope.launch {
            planOutfitUseCase.execute(date, tenueId)
            _uiState.update { it.copy(showTenuePicker = false) }
            loadMonth(_uiState.value.currentMonth)
        }
    }
}
