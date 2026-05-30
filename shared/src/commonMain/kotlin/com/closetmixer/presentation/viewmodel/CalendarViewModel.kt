package com.closetmixer.presentation.viewmodel

import com.closetmixer.data.model.CalendarEntry
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

data class CalendarUiState(
    val entries: Map<String, CalendarEntry> = emptyMap(),
    val selectedDate: String? = null,
    val currentMonth: String = "",
    val isLoading: Boolean = false
)

class CalendarViewModel(
    private val planOutfitUseCase: PlanOutfitUseCase,
    private val getWeatherUseCase: GetWeatherUseCase
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    fun loadMonth(yearMonth: String) {
        scope.launch {
            _uiState.update { it.copy(isLoading = true, currentMonth = yearMonth) }
            val entries = planOutfitUseCase.getMonthEntries(yearMonth).associateBy { it.date }
            _uiState.update { it.copy(entries = entries, isLoading = false) }
        }
    }

    fun selectDate(date: String) {
        _uiState.update { it.copy(selectedDate = date) }
    }

    fun savePlannedOutfit(date: String, tenueId: String) {
        scope.launch {
            planOutfitUseCase.execute(date, tenueId)
            loadMonth(_uiState.value.currentMonth)
        }
    }
}
