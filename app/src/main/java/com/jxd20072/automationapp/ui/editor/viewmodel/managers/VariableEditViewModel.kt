package com.jxd20072.automationapp.ui.editor.viewmodel.managers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jxd20072.automationapp.core.data.model.ConfigurationValue
import com.jxd20072.automationapp.core.data.model.VariableDef
import com.jxd20072.automationapp.core.nodes.props.ValidationResult
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

sealed interface VariableEditUiState {
    data object Loading : VariableEditUiState
    data class Success(
        val variableDef: VariableDef,
        val isCreating: Boolean,
    ) : VariableEditUiState
}

@HiltViewModel(assistedFactory = VariableEditViewModel.Factory::class)
class VariableEditViewModel @AssistedInject constructor(
    @Assisted private val variableId: String,
    @Assisted private val isCreating: Boolean,
    private val automationManager: AutomationManager
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(variableId: String, isCreating: Boolean): VariableEditViewModel
    }

    private val _variableEditUiState =
        MutableStateFlow<VariableEditUiState>(VariableEditUiState.Loading)
    val variableUiState = _variableEditUiState.asStateFlow()

    init {
        viewModelScope.launch {
            automationManager.automationUiState
                .map { it as? AutomationUiState.Success }
                .map { it?.automationDraft?.variables }
                .distinctUntilChanged()
                .collect { variables ->
                    if (variables == null) return@collect
                    val editingVariable = variables.find { it.id == variableId } ?: return@collect

                    _variableEditUiState.update {
                        VariableEditUiState.Success(
                            variableDef = editingVariable,
                            isCreating = isCreating
                        )
                    }
                }
        }
    }

    fun validateVariableName(name: String): ValidationResult {
        // TODO
        return ValidationResult.Success
    }

    fun changeName(newName: String) {
        automationManager.renameVariable(variableId, newName)
    }

    fun changeValue(newValue: ConfigurationValue<*>) {
        automationManager.changeVariableValue(variableId, newValue)
    }
}