package com.jxd20072.automationapp.ui.editor.viewmodel.bottomSheet

import androidx.compose.runtime.Immutable
import com.jxd20072.automationapp.core.data.model.VariableDef
import com.jxd20072.automationapp.ui.editor.viewmodel.AutomationManager
import com.jxd20072.automationapp.ui.editor.viewmodel.AutomationUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import me.xdrop.fuzzywuzzy.FuzzySearch
import javax.inject.Inject
import javax.inject.Singleton

@Immutable
data class VariablesScreenState(
	val searchQuery: String = "",
	val filteredVariables: Set<VariableDef<*>> = emptySet()
)

@Singleton
class VariablesManager @Inject constructor(
	private val automationManager: AutomationManager
) {
	private val _variablesUiState = MutableStateFlow(
		VariablesScreenState(
			filteredVariables = getVariables() ?: emptySet()
		)
	)
	val variablesUiState = _variablesUiState.asStateFlow()

	suspend fun watchVariableChanges() {
		automationManager.automationUiState
			.map { if (it is AutomationUiState.Success) it.automationDraft.variables else null }
			.distinctUntilChanged()
			.collect { if (it != null) updateVariableList() }
	}

	fun getVariables(): Set<VariableDef<*>>? {
		val automationUiState = automationManager.automationUiState.value
		if (automationUiState !is AutomationUiState.Success) return null

		val allVariables = automationUiState.automationDraft.variables
		val searchQuery = variablesUiState.value.searchQuery

		if (searchQuery.isBlank()) {
			return allVariables
		}

		return allVariables
			.filter { FuzzySearch.partialRatio(searchQuery, it.name.lowercase()) > 50 }
			.toSet()
	}

	fun updateVariableList() {
		_variablesUiState.update { state ->
			state.copy(
				filteredVariables = getVariables() ?: emptySet()
			)
		}
	}

	fun changeSearchQuery(query: String) {
		_variablesUiState.update { state ->
			state.copy(
				searchQuery = query
			)
		}
		updateVariableList()
	}
}