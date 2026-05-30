package com.closetmixer.presentation.viewmodel

import com.closetmixer.domain.usecase.AppStats
import com.closetmixer.domain.usecase.GetStatsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StatsUiState(
    val stats: AppStats? = null,
    val isLoading: Boolean = false
)

class StatsViewModel(private val getStats: GetStatsUseCase) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init { load() }

    fun load() {
        scope.launch {
            _uiState.update { it.copy(isLoading = true) }
            runCatching { getStats.execute() }
                .onSuccess { stats -> _uiState.update { it.copy(stats = stats, isLoading = false) } }
                .onFailure { _uiState.update { it.copy(isLoading = false) } }
        }
    }
}
