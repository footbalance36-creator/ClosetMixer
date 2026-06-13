package com.closetmixer.android.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

const val WORK_NAME = "outfit_daily_reminder"

fun scheduleOutfitReminder(context: Context, hour: Int, minute: Int, forceUpdate: Boolean = false) {
    val now = Calendar.getInstance()
    val target = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    if (target.before(now)) target.add(Calendar.DAY_OF_YEAR, 1)
    val initialDelay = target.timeInMillis - now.timeInMillis

    val work = PeriodicWorkRequestBuilder<OutfitReminderWorker>(1, TimeUnit.DAYS)
        .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        WORK_NAME,
        if (forceUpdate) ExistingPeriodicWorkPolicy.UPDATE else ExistingPeriodicWorkPolicy.KEEP,
        work
    )
}

fun cancelOutfitReminder(context: Context) {
    WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
}
