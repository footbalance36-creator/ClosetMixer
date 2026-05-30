package com.closetmixer.presentation.viewmodel

import com.closetmixer.domain.model.AppLanguage
import com.closetmixer.domain.model.CulturalStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SettingsUiState(
    val language: AppLanguage = AppLanguage.FRENCH,
    val culturalStyle: CulturalStyle = CulturalStyle.NEUTRAL,
    val isDarkMode: Boolean = false
)

class SettingsViewModel {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun setLanguage(language: AppLanguage) {
        _uiState.update { it.copy(language = language) }
    }

    fun setCulturalStyle(style: CulturalStyle) {
        _uiState.update { it.copy(culturalStyle = style) }
    }

    fun toggleDarkMode() {
        _uiState.update { it.copy(isDarkMode = !it.isDarkMode) }
    }
}
