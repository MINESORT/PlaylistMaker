package com.practicum.playlistmakerhomework1


import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmakerhomework1.settings.THEME
import com.practicum.playlistmakerhomework1.settings.THEME_KEY

class App: Application() {

    private var darkTheme = false
    override fun onCreate() {
        super.onCreate()
        val sharedPrefs = getSharedPreferences(THEME, MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(THEME_KEY, false)
        switchTheme(darkTheme)

    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            when {
                darkThemeEnabled -> {
                    AppCompatDelegate.MODE_NIGHT_YES
                }
                else -> {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
            }
        )
    }
}