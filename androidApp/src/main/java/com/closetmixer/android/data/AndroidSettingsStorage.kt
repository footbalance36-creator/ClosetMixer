package com.closetmixer.android.data

import android.content.Context
import com.closetmixer.data.storage.SettingsStorage

class AndroidSettingsStorage(context: Context) : SettingsStorage {

    private val prefs = context.getSharedPreferences("closetmixer_prefs", Context.MODE_PRIVATE)

    override fun saveProfilePhoto(path: String) {
        prefs.edit().putString(KEY_PROFILE_PHOTO, path).apply()
    }

    override fun loadProfilePhoto(): String =
        prefs.getString(KEY_PROFILE_PHOTO, "") ?: ""

    override fun saveLanguage(code: String) {
        prefs.edit().putString(KEY_LANGUAGE, code).apply()
    }

    override fun loadLanguage(): String =
        prefs.getString(KEY_LANGUAGE, "") ?: ""

    override fun saveGender(key: String) {
        prefs.edit().putString(KEY_GENDER, key).apply()
    }

    override fun loadGender(): String =
        prefs.getString(KEY_GENDER, "") ?: ""

    override fun saveProfileName(name: String) {
        prefs.edit().putString(KEY_PROFILE_NAME, name).apply()
    }

    override fun loadProfileName(): String =
        prefs.getString(KEY_PROFILE_NAME, "") ?: ""

    override fun saveNotificationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIF_ENABLED, enabled).apply()
    }

    override fun loadNotificationEnabled(): Boolean =
        prefs.getBoolean(KEY_NOTIF_ENABLED, true)

    override fun saveNotificationHour(hour: Int) {
        prefs.edit().putInt(KEY_NOTIF_HOUR, hour).apply()
    }

    override fun loadNotificationHour(): Int =
        prefs.getInt(KEY_NOTIF_HOUR, 7)

    override fun saveNotificationMinute(minute: Int) {
        prefs.edit().putInt(KEY_NOTIF_MINUTE, minute).apply()
    }

    override fun loadNotificationMinute(): Int =
        prefs.getInt(KEY_NOTIF_MINUTE, 30)

    companion object {
        private const val KEY_PROFILE_PHOTO = "profile_photo_path"
        private const val KEY_LANGUAGE = "app_language"
        private const val KEY_GENDER = "app_gender"
        private const val KEY_PROFILE_NAME = "profile_name"
        private const val KEY_NOTIF_ENABLED = "notification_enabled"
        private const val KEY_NOTIF_HOUR = "notification_hour"
        private const val KEY_NOTIF_MINUTE = "notification_minute"
    }
}
