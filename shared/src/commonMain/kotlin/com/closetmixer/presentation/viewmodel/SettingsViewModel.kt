package com.closetmixer.presentation.viewmodel

import com.closetmixer.data.storage.SettingsStorage
import com.closetmixer.domain.model.AppLanguage
import com.closetmixer.domain.model.CulturalStyle
import com.closetmixer.domain.model.Gender
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SettingsUiState(
    val language: AppLanguage = AppLanguage.FRENCH,
    val culturalStyle: CulturalStyle = CulturalStyle.NEUTRAL,
    val gender: Gender? = null,
    val isDarkMode: Boolean = false,
    val profilePhotoPath: String = ""
)

class SettingsViewModel(private val storage: SettingsStorage) {

    private val _uiState = MutableStateFlow(
        SettingsUiState(
            profilePhotoPath = storage.loadProfilePhoto(),
            language = AppLanguage.entries
                .firstOrNull { it.code == storage.loadLanguage() }
                ?: AppLanguage.FRENCH,
            gender = Gender.entries.firstOrNull { it.key == storage.loadGender() }
        )
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun setLanguage(language: AppLanguage) {
        storage.saveLanguage(language.code)
        _uiState.update { it.copy(language = language) }
    }

    fun setCulturalStyle(style: CulturalStyle) {
        _uiState.update { it.copy(culturalStyle = style) }
    }

    fun setGender(gender: Gender) {
        storage.saveGender(gender.key)
        _uiState.update { it.copy(gender = gender) }
    }

    fun toggleDarkMode() {
        _uiState.update { it.copy(isDarkMode = !it.isDarkMode) }
    }

    fun setProfilePhoto(path: String) {
        storage.saveProfilePhoto(path)
        _uiState.update { it.copy(profilePhotoPath = path) }
    }
}
