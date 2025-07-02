package com.jxd20072.automationapp.ui.editor.viewmodel.bottomSheet

import androidx.compose.runtime.Immutable
import com.jxd20072.automationapp.core.data.model.VariableDef
import com.jxd20072.automationapp.ui.editor.viewmodel.AutomationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Immutable
sealed interface VariableEditScreenState {
	data object NotEditing : VariableEditScreenState
	data class Editing(
		val variableDef: VariableDef<*>
	) : VariableEditScreenState
}

@Singleton
class VariableEditManager @Inject constructor(
	private val automationManager: AutomationManager
) {
	private val _variableEditUiState = MutableStateFlow<VariableEditScreenState>(
		VariableEditScreenState.NotEditing
	)
	val variableEditUiState = _variableEditUiState.asStateFlow()

	fun startVariableEdit(variableName: String) {
		val variableDef = automationManager.findVariable(variableName)
		if (variableDef == null) return
		_variableEditUiState.update { VariableEditScreenState.Editing(variableDef) }
	}

	fun changeName(newName: String) {
		val state = variableEditUiState.value
		if (state !is VariableEditScreenState.Editing) return
		automationManager.editVariable(
			state.variableDef.name,
			when (state.variableDef) { // Kotlin type system moment
				is VariableDef.BOOL -> state.variableDef.copy(name = newName)
				is VariableDef.TEXT -> state.variableDef.copy(name = newName)
				is VariableDef.NUMBER -> state.variableDef.copy(name = newName)
			}
		)
		// TODO: Should also rename any existing references
	}

	fun <T> changeValue(id: String, newValue: T) {
		val state = variableEditUiState.value
		if (state !is VariableEditScreenState.Editing) return
		if (!state.variableDef.value::class.isInstance(newValue)) return

		automationManager.editVariable(
			state.variableDef.name,
			when (state.variableDef) { // Kotlin type system moment
				is VariableDef.BOOL -> state.variableDef.copy(value = newValue as Boolean)
				is VariableDef.TEXT -> state.variableDef.copy(value = newValue as String)
				is VariableDef.NUMBER -> state.variableDef.copy(value = newValue as Int)
			}
		)
	}

	fun swapVariable(id: String, newVariable: VariableDef<*>) {
		val state = variableEditUiState.value
		if (state !is VariableEditScreenState.Editing) return
		automationManager.deleteVariable(id)
		automationManager.editVariable(id, newVariable)
	}
}