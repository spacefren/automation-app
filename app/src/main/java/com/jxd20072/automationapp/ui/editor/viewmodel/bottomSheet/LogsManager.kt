package com.jxd20072.automationapp.ui.editor.viewmodel.bottomSheet

import androidx.compose.runtime.Immutable
import com.jxd20072.automationapp.core.executor.LogLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Immutable
data class LogsScreenState(
	val searchQuery: String = "",
	val filterLevel: LogLevel? = null,
)

@Singleton
class LogsManager @Inject constructor() {
	private val _logsScreenState = MutableStateFlow(LogsScreenState())
	val logsScreenState = _logsScreenState.asStateFlow()
}