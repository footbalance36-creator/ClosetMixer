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
    val profilePhotoPath: String = "",
    val profileName: String = "",
    val notificationEnabled: Boolean = true,
    val notificationHour: Int = 7,
    val notificationMinute: Int = 30
)

class SettingsViewModel(private val storage: SettingsStorage) {

    private val _uiState = MutableStateFlow(
        SettingsUiState(
            profilePhotoPath = storage.loadProfilePhoto(),
            profileName = storage.loadProfileName(),
            language = AppLanguage.entries
                .firstOrNull { it.code == storage.loadLanguage() }
                ?: AppLanguage.FRENCH,
            gender = Gender.entries.firstOrNull { it.key == storage.loadGender() },
            notificationEnabled = storage.loadNotificationEnabled(),
            notificationHour = storage.loadNotificationHour(),
            notificationMinute = storage.loadNotificationMinute()
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

    fun setProfileName(name: String) {
        storage.saveProfileName(name)
        _uiState.update { it.copy(profileName = name) }
    }

    fun setNotificationEnabled(enabled: Boolean) {
        storage.saveNotificationEnabled(enabled)
        _uiState.update { it.copy(notificationEnabled = enabled) }
    }

    fun setNotificationTime(hour: Int, minute: Int) {
        storage.saveNotificationHour(hour)
        storage.saveNotificationMinute(minute)
        _uiState.update { it.copy(notificationHour = hour, notificationMinute = minute) }
    }
}
