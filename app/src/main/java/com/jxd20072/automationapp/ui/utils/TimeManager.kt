package com.jxd20072.automationapp.ui.utils

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant

class TimeManager(private val scope: CoroutineScope, private val interval: Long) {
	private val _currentTime = MutableStateFlow(Instant.now())
	val currentTime: StateFlow<Instant> = _currentTime.asStateFlow()

	init {
		scope.launch {
			while (true) {
				delay(interval)
				_currentTime.value = Instant.now()
				Log.d("APP_TimeManager", "emitting at interval $interval");
			}
		}
	}

	// Cleanup method to cancel the coroutine when no longer needed
	fun cleanup() {
		scope.cancel()
	}
}