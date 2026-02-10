package com.jxd20072.automationapp.ui.editor.viewmodel.managers

import android.util.Log
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
import kotlin.reflect.KClass

@Immutable
data class VariablesScreenState(
    val searchQuery: String = "",
    val isSelecting: Boolean = false,
    val variableTypeFilters: Set<KClass<ConfigurationValue<*>>> = emptySet(),
    val filteredVariables: Set<VariableDef> = emptySet()
)

@HiltViewModel(assistedFactory = VariablesViewModel.Factory::class)
class VariablesViewModel @AssistedInject constructor(
    @Assisted private val initialTypeFilters: Set<KClass<ConfigurationValue<*>>> = emptySet(),
    @Assisted private val onVariableSelected: ((variableId: String) -> Unit)? = null,
    private val automationManager: AutomationManager,
    private val bottomSheetManager: BottomSheetManager
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(
            initialTypeFilters: Set<KClass<ConfigurationValue<*>>> = emptySet(),
            onVariableSelected: ((variableId: String) -> Unit)? = null
        ): VariablesViewModel
    }

    private val _variablesUiState = MutableStateFlow(
        VariablesScreenState(
            variableTypeFilters = initialTypeFilters
        )
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
                                uiState.searchQuery,
                                uiState.variableTypeFilters
                            ),
                            isSelecting = onVariableSelected != null
                        )
                    }
                }
        }
    }

    fun getFilteredVariables(
        allVariables: Set<VariableDef>,
        searchQuery: String,
        typeFilters: Set<KClass<ConfigurationValue<*>>>,
    ): Set<VariableDef> {
        return allVariables
            .filter { variable ->
                if (searchQuery.isBlank()) true
                else FuzzySearch.partialRatio(searchQuery, variable.name.lowercase()) > 50
            }
            .filter { variable ->
                if (typeFilters.isEmpty()) return@filter true
                else return@filter typeFilters.any {
                    Log.d("APP_A", "getFilteredVariables: ${it}, ${variable.value::class}")
                    it == variable.value::class
                }
            }
            .toSet()
    }

    fun changeSearchQuery(query: String) {
        val allVariables = automationManager.getVariables() ?: return

        _variablesUiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredVariables = getFilteredVariables(
                    allVariables,
                    query,
                    state.variableTypeFilters
                )
            )
        }
    }

    fun addVariableTypeFilter(configType: KClass<ConfigurationValue<*>>) {
        val allVariables = automationManager.getVariables() ?: return

        _variablesUiState.update { state ->
            val newFilters = state.variableTypeFilters + configType

            state.copy(
                variableTypeFilters = newFilters,
                filteredVariables = getFilteredVariables(
                    allVariables,
                    state.searchQuery,
                    newFilters
                )
            )
        }
    }

    fun removeVariableTypeFilter(configType: KClass<ConfigurationValue<*>>) {
        val allVariables = automationManager.getVariables() ?: return

        _variablesUiState.update { state ->
            val newFilters = state.variableTypeFilters.filter { it != configType }.toSet()

            state.copy(
                variableTypeFilters = newFilters,
                filteredVariables = getFilteredVariables(
                    allVariables,
                    state.searchQuery,
                    newFilters
                )
            )
        }
    }

    fun setVariableTypeFilters(filters: Set<KClass<ConfigurationValue<*>>>) {
        val allVariables = automationManager.getVariables() ?: return

        _variablesUiState.update { state ->
            state.copy(
                variableTypeFilters = filters,
                filteredVariables = getFilteredVariables(allVariables, state.searchQuery, filters)
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