package com.seva.scripture

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.seva.scripture.data.local.ScriptureDatabase
import com.seva.scripture.data.repository.OfflineScriptureRepository
import com.seva.scripture.data.repository.SettingsRepository
import com.seva.scripture.work.DailyShlokaWorker
import java.util.concurrent.TimeUnit

class ScriptureApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        val database = ScriptureDatabase.getInstance(this)
        val settingsRepository = SettingsRepository(this)
        val scriptureRepository = OfflineScriptureRepository(this, database)
        container = AppContainer(scriptureRepository, settingsRepository)

        val dailyWork = PeriodicWorkRequestBuilder<DailyShlokaWorker>(24, TimeUnit.HOURS).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            DailyShlokaWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            dailyWork
        )
    }
}

data class AppContainer(
    val scriptureRepository: OfflineScriptureRepository,
    val settingsRepository: SettingsRepository
)
