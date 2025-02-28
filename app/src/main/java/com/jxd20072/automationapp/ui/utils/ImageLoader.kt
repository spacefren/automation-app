package com.jxd20072.automationapp.ui.utils

import android.content.Context
import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache

fun createImageLoader(context: Context): ImageLoader {
	return ImageLoader.Builder(context)
		.memoryCache {
			MemoryCache.Builder()
				.maxSizePercent(context, 0.25)
				.build()
		}
		.diskCache {
			DiskCache.Builder()
				.directory(context.cacheDir.resolve("image_cache"))
				.maxSizePercent(0.02)
				.build()
		}
		.build()
}