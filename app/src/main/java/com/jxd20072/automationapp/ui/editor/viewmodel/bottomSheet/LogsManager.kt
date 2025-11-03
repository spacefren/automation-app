package com.jxd20072.automationapp.ui.editor.viewmodel.bottomSheet

import androidx.compose.runtime.Immutable
import com.jxd20072.automationapp.core.data.LogLevel
import com.jxd20072.automationapp.ui.editor.viewmodel.AutomationManager
import com.jxd20072.automationapp.ui.editor.viewmodel.AutomationUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import me.xdrop.fuzzywuzzy.FuzzySearch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.log

@Immutable
data class LogsScreenState(
	val searchQuery: String = "",
	val filterLevel: LogLevel? = null,
)

@Singleton
class LogsManager @Inject constructor(
	private val automationManager: AutomationManager,
) {
	private val _logsScreenState = MutableStateFlow(LogsScreenState())
	val logsScreenState = _logsScreenState.asStateFlow()

	val filteredLogs = combine(
		automationManager.automationUiState
			.map { if (it is AutomationUiState.Success) it.automationData.logsList else null }
			.distinctUntilChanged(),
		logsScreenState) { logs, state ->
		if (logs == null) return@combine emptyList()

		(if (state.searchQuery.isBlank()) logs
		else logs
			.map { it to FuzzySearch.partialRatio(state.searchQuery, it.message.lowercase()) }
			.filter { it.second > 40 }
			.sortedByDescending { it.second }
			.map { it.first }
		).filter { state.filterLevel == null || it.level == state.filterLevel }
	}

	fun searchLogs(query: String) {
		_logsScreenState.update { it.copy(searchQuery = query) }
	}

	fun filterLogs(level: LogLevel) {
		_logsScreenState.update { it.copy(filterLevel = level) }
	}
}