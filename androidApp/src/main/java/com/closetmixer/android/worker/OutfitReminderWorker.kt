package com.closetmixer.android.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.closetmixer.android.MainActivity
import com.closetmixer.android.R
import com.closetmixer.data.db.DatabaseDriverFactory
import com.closetmixer.db.ClosetDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDate

class OutfitReminderWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        runCatching {
            val driver = DatabaseDriverFactory(applicationContext).createDriver()
            val db = ClosetDatabase(driver)
            val queries = db.closetDatabaseQueries

            val today = LocalDate.now().toString()
            val entry = queries.getCalendarEntry(today).executeAsOneOrNull()

            val channelId = "outfit_reminder"
            val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    "Rappel tenue du jour",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
            }

            val tenueId = entry?.tenueId
            val (text, photoPath) = if (tenueId != null) {
                val articles = queries.getArticlesForTenue(tenueId).executeAsList()
                val firstPhotoPath = articles.firstOrNull()?.photoPath
                "Votre tenue du jour est prête ✨" to firstPhotoPath
            } else {
                "Vous n'avez pas de tenue prévue aujourd'hui — Générer une tenue ?" to null
            }

            val intent = Intent(applicationContext, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                applicationContext, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val builder = NotificationCompat.Builder(applicationContext, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("ClosetMixer")
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            photoPath?.let { path ->
                val file = File(path)
                if (file.exists()) {
                    val bitmap = BitmapFactory.decodeFile(path)
                    if (bitmap != null) {
                        builder
                            .setLargeIcon(bitmap)
                            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
                    }
                }
            }

            val canNotify = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    applicationContext,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }

            if (canNotify) {
                notificationManager.notify(1001, builder.build())
            }
        }
        Result.success()
    }
}
