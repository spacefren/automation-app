package com.jxd20072.automationapp.core.data.di

import android.content.Context
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.jxd20072.automationapp.core.data.local.AutomationsDataSerializer
import com.jxd20072.automationapp.core.data.local.UserSettingsSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

	@Provides
	@Singleton
	fun providesUserPreferencesDataStore(
		@ApplicationContext context: Context,
		userPreferencesSerializer: UserSettingsSerializer
	) = DataStoreFactory.create(
		serializer = userPreferencesSerializer
	) {
		context.dataStoreFile("user_preferences.pb")
	}

	@Provides
	@Singleton
	fun providesAutomationsDataMapStore(
		@ApplicationContext context: Context,
		automationsDataSerializer: AutomationsDataSerializer
	) = DataStoreFactory.create(
		serializer = automationsDataSerializer
	) {
		context.dataStoreFile("automations_data_.pb")
	}
}