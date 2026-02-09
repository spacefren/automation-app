package com.jxd20072.automationapp.ui.editor.viewmodel.managers

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jxd20072.automationapp.core.data.model.ConfigurationValue
import com.jxd20072.automationapp.core.data.model.VariableDef
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.xdrop.fuzzywuzzy.FuzzySearch

@Immutable
data class VariablesScreenState(
    val searchQuery: String = "",
    val isSelecting: Boolean = false,
    val filteredVariables: Set<VariableDef> = emptySet()
)

@HiltViewModel(assistedFactory = VariablesViewModel.Factory::class)
class VariablesViewModel @AssistedInject constructor(
    @Assisted private val onVariableSelected: ((variableId: String) -> Unit)? = null,
    private val automationManager: AutomationManager,
    private val bottomSheetManager: BottomSheetManager
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(onVariableSelected: ((variableId: String) -> Unit)? = null): VariablesViewModel
    }

    private val _variablesUiState = MutableStateFlow(
        VariablesScreenState()
    )
    val variablesUiState = _variablesUiState.asStateFlow()

    init {
        viewModelScope.launch {
            automationManager.automationUiState
                .map { it as? AutomationUiState.Success }
                .map { it?.automationDraft?.variables }
                .distinctUntilChanged()
                .collect { variables ->
                    if (variables == null) return@collect

                    _variablesUiState.update { uiState ->
                        uiState.copy(
                            filteredVariables = getFilteredVariables(
                                variables,
                                uiState.searchQuery
                            ),
                            isSelecting = onVariableSelected != null
                        )
                    }
                }
        }
    }

    fun getFilteredVariables(
        allVariables: Set<VariableDef>,
        searchQuery: String
    ): Set<VariableDef> {
        return if (searchQuery.isBlank()) {
            allVariables
        } else {
            allVariables
                .filter { FuzzySearch.partialRatio(searchQuery, it.name.lowercase()) > 50 }
                .toSet()
        } ?: emptySet()
    }

    fun changeSearchQuery(query: String) {
        val allVariables = (automationManager.automationUiState.value as? AutomationUiState.Success)
            ?.automationDraft?.variables ?: return

        _variablesUiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredVariables = getFilteredVariables(allVariables, query)
            )
        }
    }

    fun onCreateVariable() {
        val newVariable = VariableDef(
            name = "Unnamed variable",
            value = ConfigurationValue.NUMBER("0"),
        )
        automationManager.addVariable(newVariable)
        bottomSheetManager.navigateTo(
            EditorBottomSheetScreen.VariableEdit(newVariable.id, true)
        )
    }

    fun onVariableClick(variableId: String) {
        if (onVariableSelected != null) {
            onVariableSelected(variableId)
            return
        }
        bottomSheetManager.navigateTo(
            EditorBottomSheetScreen.VariableEdit(variableId, false)
        )
    }

    fun deleteVariable(variableId: String) {
        automationManager.deleteVariable(variableId)
    }
}