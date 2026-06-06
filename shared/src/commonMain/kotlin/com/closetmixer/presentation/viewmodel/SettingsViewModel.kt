package com.closetmixer.presentation.viewmodel

import com.closetmixer.data.storage.SettingsStorage
import com.closetmixer.domain.model.AppLanguage
import com.closetmixer.domain.model.CulturalStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SettingsUiState(
    val language: AppLanguage = AppLanguage.FRENCH,
    val culturalStyle: CulturalStyle = CulturalStyle.NEUTRAL,
    val isDarkMode: Boolean = false,
    val profilePhotoPath: String = ""
)

class SettingsViewModel(private val storage: SettingsStorage) {

    private val _uiState = MutableStateFlow(
        SettingsUiState(profilePhotoPath = storage.loadProfilePhoto())
    )
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

    fun setProfilePhoto(path: String) {
        storage.saveProfilePhoto(path)
        _uiState.update { it.copy(profilePhotoPath = path) }
    }
}
