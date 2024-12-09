package com.jxd20072.automationapp.core.data.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ContextProviderModule {
	@Provides
	@Singleton
	fun provideContext(application: Application): Context {
		return application.applicationContext
	}
}