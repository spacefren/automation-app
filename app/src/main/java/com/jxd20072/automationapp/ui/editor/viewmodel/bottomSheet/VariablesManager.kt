package com.jxd20072.automationapp.ui.editor.viewmodel.bottomSheet

import androidx.compose.runtime.Immutable
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

@Immutable
data class VariablesScreenState(
	val searchQuery: String = "",
)

@Singleton
class VariablesManager @Inject constructor(
	private val automationManager: AutomationManager
) {
	private val _variablesUiState = MutableStateFlow(
		VariablesScreenState()
	)
	val variablesUiState = _variablesUiState.asStateFlow()

	val filteredVariables = combine(
		automationManager.automationUiState
			.map { if (it is AutomationUiState.Success) it.automationDraft.variables else null }
			.distinctUntilChanged(),
		variablesUiState
	) { variables, state ->
		if (state.searchQuery.isBlank()) {
			variables
		} else {
			variables
				?.filter { FuzzySearch.partialRatio(state.searchQuery, it.name.lowercase()) > 50 }
				?.toSet()
		} ?: emptySet()
	}

	fun changeSearchQuery(query: String) {
		_variablesUiState.update { state ->
			state.copy(
				searchQuery = query
			)
		}
	}
}