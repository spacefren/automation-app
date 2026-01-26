package com.jxd20072.automationapp.core.data.di

import android.content.Context
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.jxd20072.automationapp.core.data.local.AppSettingsSerializer
import com.jxd20072.automationapp.core.data.local.AutomationsMapSerializer
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
    fun providesAppPreferencesDataStore(
        @ApplicationContext context: Context,
    ) = DataStoreFactory.create(
        serializer = AppSettingsSerializer
    ) {
        context.dataStoreFile("settings.pb")
    }

    @Provides
    @Singleton
    fun providesAutomationsDataMapStore(
        @ApplicationContext context: Context,
    ) = DataStoreFactory.create(
        serializer = AutomationsMapSerializer
    ) {
        context.dataStoreFile("automations_map.pb")
    }
}