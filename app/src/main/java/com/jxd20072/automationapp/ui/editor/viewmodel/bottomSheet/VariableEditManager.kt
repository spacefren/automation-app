package com.jxd20072.automationapp.ui.editor.viewmodel.bottomSheet

import androidx.compose.runtime.Immutable
import com.jxd20072.automationapp.core.data.model.VariableDefValue
import com.jxd20072.automationapp.ui.editor.viewmodel.AutomationManager
import com.jxd20072.automationapp.ui.editor.viewmodel.AutomationUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Immutable
data class VariableEditState(
	val isCreating: Boolean = false,
)

@Singleton
class VariableEditManager @Inject constructor(
	private val automationManager: AutomationManager
) {
	private val _variableEditState = MutableStateFlow(VariableEditState())
	val variableEditState = _variableEditState.asStateFlow()

	val editingVariableID = MutableStateFlow<String?>(null)
	val editingVariableDef = combine(
		automationManager.automationUiState.map { automationState ->
			if (automationState !is AutomationUiState.Success) null
			else automationState.automationDraft.variables
		},
		editingVariableID
	) { variables, editingVariableName ->
		variables?.find { it.id == editingVariableName }
	}

	fun startVariableEdit(variableId: String, isCreating: Boolean = false) {
		editingVariableID.value = variableId
		_variableEditState.update { state ->
			state.copy(
				isCreating = isCreating
			)
		}
	}

	fun changeName(newName: String) {
		val variableID = editingVariableID.value
		if (variableID == null) return
		automationManager.renameVariable(variableID, newName)
	}

	fun changeValue(newValue: VariableDefValue<*>) {
		val variableID = editingVariableID.value
		if (variableID == null) return

		automationManager.changeVariableValue(variableID, newValue)
	}
}