package com.jxd20072.automationapp.core.data.repository

import androidx.datastore.core.DataStore
import com.jxd20072.automationapp.core.data.UserSettings
import com.jxd20072.automationapp.core.data.copy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserSettingsRepository @Inject constructor(
	private val userSettingsStore: DataStore<UserSettings>,
) {
	val userSettings: Flow<UserSettings> = userSettingsStore.data

	fun initializeSettings() {
		CoroutineScope(Dispatchers.IO).launch {
			if (!getSettings().hasInitialized) {
				resetSettingsToDefault()
			}
		}
	}

	suspend fun getSettings(): UserSettings {
		return userSettings.first()
	}

	suspend fun resetSettingsToDefault() {
		userSettingsStore.updateData { userSettings ->
			userSettings
				.toBuilder()
				.setHasInitialized(true)
				.setAutoSave(true)
				.setAutoOpenKeyboard(true)
				.build()
		}
	}

	suspend fun setAutoSaveEnabled(enabled: Boolean) {
		userSettingsStore.updateData { userSettings ->
			userSettings.copy {
				autoSave = enabled
			}
		}
	}

	suspend fun setAutoOpenKeyboard(enabled: Boolean) {
		userSettingsStore.updateData { userSettings ->
			userSettings.copy {
				autoOpenKeyboard = enabled
			}
		}
	}
}