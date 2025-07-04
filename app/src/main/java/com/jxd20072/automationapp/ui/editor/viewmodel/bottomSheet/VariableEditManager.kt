package com.jxd20072.automationapp.ui.editor.viewmodel.bottomSheet

import com.jxd20072.automationapp.core.data.model.VariableDefValue
import com.jxd20072.automationapp.ui.editor.viewmodel.AutomationManager
import com.jxd20072.automationapp.ui.editor.viewmodel.AutomationUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VariableEditManager @Inject constructor(
	private val automationManager: AutomationManager
) {
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

	fun startVariableEdit(variableId: String) {
		editingVariableID.value = variableId
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