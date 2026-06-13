package com.closetmixer.android.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val prefs = context.getSharedPreferences("closetmixer_prefs", Context.MODE_PRIVATE)
        val enabled = prefs.getBoolean("notification_enabled", true)
        if (enabled) {
            val hour = prefs.getInt("notification_hour", 7)
            val minute = prefs.getInt("notification_minute", 30)
            scheduleOutfitReminder(context, hour, minute, forceUpdate = true)
        }
    }
}
