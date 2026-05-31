package com.closetmixer.data.storage

interface SettingsStorage {
    fun saveProfilePhoto(path: String)
    fun loadProfilePhoto(): String
}
