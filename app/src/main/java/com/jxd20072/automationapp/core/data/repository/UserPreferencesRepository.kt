package com.jxd20072.automationapp.core.data.repository

import androidx.datastore.core.DataStore
import com.jxd20072.automationapp.core.data.UserPreferences
import com.jxd20072.automationapp.core.data.copy
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserPreferencesRepository @Inject constructor(
	private val userPreferencesStore: DataStore<UserPreferences>
) {
	val userPreferences: Flow<UserPreferences> = userPreferencesStore.data

	suspend fun setAutoSaveEnabled(enabled: Boolean) {
		userPreferencesStore.updateData { userPreferences ->
			userPreferences.copy {
				autoSave = enabled
			}
		}
	}
}