package com.jxd20072.automationapp.ui.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

class TimeManager @Inject constructor() {
	private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
	private val _currentTime = MutableStateFlow(Instant.now())
	val currentTime: StateFlow<Instant> = _currentTime.asStateFlow()

	init {
		scope.launch {
			while (true) {
				delay(5000) // 5 seconds
				_currentTime.value = Instant.now()
			}
		}
	}

	// Cleanup method to cancel the coroutine when no longer needed
	fun cleanup() {
		scope.cancel()
	}

	companion object {
		@Volatile
		private var instance: TimeManager? = null

		fun getInstance(): TimeManager {
			return instance ?: synchronized(this) {
				instance ?: TimeManager().also { instance = it }
			}
		}
	}
}