package com.closetmixer.android

import android.app.Application
import com.closetmixer.android.di.androidModule
import com.closetmixer.android.worker.scheduleOutfitReminder
import com.closetmixer.di.sharedModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ClosetMixerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ClosetMixerApp)
            modules(androidModule, sharedModule)
        }
        scheduleDailyReminderIfEnabled()
    }

    private fun scheduleDailyReminderIfEnabled() {
        val prefs = getSharedPreferences("closetmixer_prefs", MODE_PRIVATE)
        if (prefs.getBoolean("notification_enabled", true)) {
            val hour = prefs.getInt("notification_hour", 7)
            val minute = prefs.getInt("notification_minute", 30)
            scheduleOutfitReminder(this, hour, minute)
        }
    }
}
