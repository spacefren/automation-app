package com.jxd20072.automationapp.core.data.repository

import androidx.datastore.core.DataStore
import com.jxd20072.automationapp.core.data.AppSettings
import com.jxd20072.automationapp.core.data.copy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class AppSettingsRepository @Inject constructor(
    val appSettingsStore: DataStore<AppSettings>
) {
    val appSettings: Flow<AppSettings> = appSettingsStore.data

    fun initializeSettings() {
        CoroutineScope(Dispatchers.IO).launch {
            if (!getSettings().hasInitialized) {
                resetSettingsToDefault()
            }
        }
    }

    suspend fun getSettings(): AppSettings {
        return appSettings.first()
    }

    suspend fun resetSettingsToDefault() {
        appSettingsStore.updateData { appSettings ->
            appSettings
                .toBuilder()
                .setHasInitialized(true)
                .setAutoSave(true)
                .setAutoOpenKeyboard(false)
                .build()
        }
    }

    suspend fun setAutoSaveEnabled(enabled: Boolean) {
        appSettingsStore.updateData { appSettings ->
            appSettings.copy {
                autoSave = enabled
            }
        }
    }

    suspend fun setAutoOpenKeyboard(enabled: Boolean) {
        appSettingsStore.updateData { appSettings ->
            appSettings.copy {
                autoOpenKeyboard = enabled
            }
        }
    }
}