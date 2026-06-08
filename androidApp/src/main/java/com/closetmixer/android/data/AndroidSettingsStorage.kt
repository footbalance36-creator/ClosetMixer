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

    companion object {
        private const val KEY_PROFILE_PHOTO = "profile_photo_path"
        private const val KEY_LANGUAGE = "app_language"
        private const val KEY_GENDER = "app_gender"
    }
}
