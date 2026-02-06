package com.seva.scripture.work

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.seva.scripture.R
import com.seva.scripture.data.local.ScriptureDatabase
import com.seva.scripture.data.repository.OfflineScriptureRepository
import com.seva.scripture.data.repository.SettingsRepository
import kotlinx.coroutines.flow.first

class DailyShlokaWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val settings = SettingsRepository(applicationContext).settings.first()
        if (!settings.dailyNotificationEnabled) return Result.success()

        val repository = OfflineScriptureRepository(
            context = applicationContext,
            database = ScriptureDatabase.getInstance(applicationContext)
        )
        repository.seedIfNeeded()
        val verse = repository.randomVerse(settings.languageCode) ?: return Result.success()

        createChannel()

        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.success()
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_lotus)
            .setContentTitle("Daily Shloka ${verse.chapterNumber}.${verse.verseNumber}")
            .setContentText(verse.simpleMeaning)
            .setStyle(NotificationCompat.BigTextStyle().bigText("${verse.sanskrit}\n\n${verse.simpleMeaning}"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(101, notification)
        return Result.success()
    }

    private fun createChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Daily Shloka",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    companion object {
        const val WORK_NAME = "daily_shloka"
        private const val CHANNEL_ID = "daily_shloka_channel"
    }
}
