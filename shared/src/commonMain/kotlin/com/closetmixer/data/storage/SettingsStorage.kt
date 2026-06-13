package com.closetmixer.data.storage

interface SettingsStorage {
    fun saveProfilePhoto(path: String)
    fun loadProfilePhoto(): String
    fun saveLanguage(code: String)
    fun loadLanguage(): String
    fun saveGender(key: String)
    fun loadGender(): String
    fun saveProfileName(name: String)
    fun loadProfileName(): String
    fun saveNotificationEnabled(enabled: Boolean)
    fun loadNotificationEnabled(): Boolean
    fun saveNotificationHour(hour: Int)
    fun loadNotificationHour(): Int
    fun saveNotificationMinute(minute: Int)
    fun loadNotificationMinute(): Int
}
