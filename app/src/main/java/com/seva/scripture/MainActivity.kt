package com.seva.scripture

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.seva.scripture.domain.model.AppSettings
import com.seva.scripture.ui.navigation.ScriptureNavHost
import com.seva.scripture.ui.theme.ScriptureTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appContainer = (application as ScriptureApp).container

        setContent {
            val settings by appContainer.settingsRepository.settings.collectAsState(initial = AppSettings())
            ScriptureTheme(darkTheme = settings.darkMode, fontScale = settings.fontScale) {
                ScriptureNavHost(appContainer = appContainer)
            }
        }
    }
}
