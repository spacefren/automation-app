package com.jxd20072.automationapp

import android.app.Application
import coil3.ImageLoader
import com.jxd20072.automationapp.ui.shared.createImageLoader
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AutomationApplication : Application() {
    lateinit var imageLoader: ImageLoader

    override fun onCreate() {
        super.onCreate()

        imageLoader = createImageLoader(this)
    }
}