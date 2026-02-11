package com.jxd20072.automationapp.ui.editor.viewmodel.managers

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import com.jxd20072.automationapp.core.data.LogLevel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import me.xdrop.fuzzywuzzy.FuzzySearch

@Immutable
data class LogsScreenState(
    val searchQuery: String = "",
    val filterLevel: LogLevel? = null,
)

@HiltViewModel(assistedFactory = LogsViewModel.Factory::class)
class LogsViewModel @AssistedInject constructor(
    @Assisted private val editorSession: EditorSession,
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun create(editorSession: EditorSession): LogsViewModel
    }

    private val _logsScreenState = MutableStateFlow(LogsScreenState())
    val logsScreenState = _logsScreenState.asStateFlow()

    val filteredLogs = combine(
        editorSession.automationUiState
            .map { if (it is AutomationUiState.Success) it.automationData.logsList else null }
            .distinctUntilChanged(),
        logsScreenState) { logs, state ->
        if (logs == null) return@combine emptyList()

        if (state.searchQuery.isEmpty()) {
            logs
                .sortedByDescending { it.time }
        } else {
            logs
                .map { it to FuzzySearch.partialRatio(state.searchQuery, it.message.lowercase()) }
                .filter { it.second > 40 }
                .sortedByDescending { it.second }
                .map { it.first }
        }
            .filter { state.filterLevel == null || it.level == state.filterLevel }
    }

    fun searchLogs(query: String) {
        _logsScreenState.update { it.copy(searchQuery = query) }
    }

    fun filterLogs(level: LogLevel) {
        _logsScreenState.update { it.copy(filterLevel = level) }
    }

    fun deleteLog(id: String) {
        editorSession.deleteLog(id)
    }
}