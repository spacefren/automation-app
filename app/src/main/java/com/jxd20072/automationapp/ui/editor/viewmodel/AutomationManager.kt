package com.jxd20072.automationapp.ui.editor.viewmodel

import androidx.compose.runtime.Immutable
import com.jxd20072.automationapp.core.data.AutomationLogEntry
import com.jxd20072.automationapp.core.data.AutomationsMapEntry
import com.jxd20072.automationapp.core.data.model.AutomationDef
import com.jxd20072.automationapp.core.data.model.NodeDef
import com.jxd20072.automationapp.core.data.model.NodeType
import com.jxd20072.automationapp.core.data.model.VariableDef
import com.jxd20072.automationapp.core.data.model.VariableDefValue
import com.jxd20072.automationapp.core.data.repository.AutomationsRepository
import com.jxd20072.automationapp.core.data.repository.UserSettingsRepository
import com.jxd20072.automationapp.core.executor.AutomationExecutor
import com.jxd20072.automationapp.core.nodes.NodeData
import com.jxd20072.automationapp.core.nodes.props.ValidationResult
import com.jxd20072.automationapp.ui.utils.findAndReplace
import com.jxd20072.automationapp.ui.utils.findAndTransform
import com.jxd20072.automationapp.ui.utils.insertAt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Immutable
sealed interface AutomationUiState {
	data object Loading : AutomationUiState
	data class Success(
		val automationId: String,
		val automationData: AutomationsMapEntry,
		val automationDraft: AutomationDef,
	) : AutomationUiState
}

@Singleton
class AutomationManager @Inject constructor(
	private val automationsRepository: AutomationsRepository,
	private val settingsRepository: UserSettingsRepository,
) {
	private val _automationUiState = MutableStateFlow<AutomationUiState>(AutomationUiState.Loading)
	val automationUiState = _automationUiState.asStateFlow()

	suspend fun observeFileChanges() {
		automationsRepository.automationsDataFlow.collect { automationData ->
			_automationUiState.update { uiState ->
				if (uiState !is AutomationUiState.Success) return@update uiState
				val newData =
					automationData.automationsMap[uiState.automationId] ?: return@update uiState
				uiState.copy(automationData = newData)
			}
		}
	}

	@OptIn(FlowPreview::class)
	suspend fun observeDraftChanges() {
		automationUiState
			.map { if (it is AutomationUiState.Success) it.automationDraft else null }
			.distinctUntilChanged()
			.debounce(1000)
			.collect { trySaveAutomation() }
	}

	suspend fun executeAutomation(): AutomationExecutor? {
		val uiState = automationUiState.value
		return if (uiState is AutomationUiState.Success) {
			automationsRepository.executeAutomation(uiState.automationId)
		} else null
	}

	fun loadAutomation(id: String) {
		CoroutineScope(Dispatchers.Main).launch {
			val automation = automationsRepository.getAutomationDef(id)
			val automationData = automationsRepository.getAutomationMapEntry(id)
			if (automation != null && automationData != null) {
				_automationUiState.update {
					AutomationUiState.Success(
						automationId = id,
						automationData = automationData,
						automationDraft = automation.copy(),
					)
				}
			}
		}
	}

	fun trySaveAutomation(force: Boolean = false) {
		CoroutineScope(Dispatchers.Main).launch {
			if (force || settingsRepository.getSettings().autoSave) {
				val uiState = automationUiState.value
				if (uiState is AutomationUiState.Success) {
					automationsRepository.writeAutomation(uiState.automationId, uiState.automationDraft)
				}
			}
		}
	}

	fun findNode(id: String): NodeDef? {
		val state = automationUiState.value
		if (state !is AutomationUiState.Success) return null

		return (
			state.automationDraft.topLevel
				+ state.automationDraft.topLevel.map { it.content }.flatten()
			).find { it.id == id }
	}

	fun createNodeDef(nodeData: NodeData): NodeDef {
		return NodeDef(
			name = nodeData.name,
			nodeType = nodeData.nodeType,
			input = emptyMap(),
			output = emptyMap(),
			content = emptyList()
		)
	}

	fun addNode(node: NodeDef, parentID: String? = "0", index: Int? = null) {
		_automationUiState.update { state ->
			if (state !is AutomationUiState.Success) return@update state

			when (node.nodeType) {
				NodeType.ACTION -> state.copy(
					automationDraft = state.automationDraft.copy(
						topLevel = state.automationDraft.topLevel.findAndTransform({ it.id == parentID }) {
							it.copy(
								content = it.content.insertAt(index, node)
							)
						},
					)
				)

				NodeType.FUNCTION,
				NodeType.TRIGGER -> state.copy(
					automationDraft = state.automationDraft.copy(
						topLevel = state.automationDraft.topLevel.insertAt(index, node)
					)
				)
			}
		}
	}

	fun deleteNode(nodeID: String) {
		_automationUiState.update { state ->
			if (state !is AutomationUiState.Success) return@update state

			state.copy(
				automationDraft = state.automationDraft.copy(
					topLevel = state.automationDraft.topLevel
						.filter { it.id != nodeID }
						.map { trigger ->
						trigger.copy(
							content = trigger.content.filter { it.id != nodeID }
						)
						}
				)
			)
		}
	}

	fun moveNode(nodeID: String, parentID: String? = "0", index: Int) {
		val state = automationUiState.value
		if (state !is AutomationUiState.Success) return

		val nodeDef = findNode(nodeID)!!
		val previousParent = state.automationDraft.topLevel.find { it.content.any { it.id == nodeID } }

		val previousIndex = previousParent?.content?.indexOfFirst { it.id == nodeID }
			?: state.automationDraft.topLevel.indexOfFirst { it.id == nodeID }

		val indexCorrection = if (parentID == previousParent?.id && previousIndex < index) -1 else 0

		deleteNode(nodeID)
		addNode(nodeDef, parentID, index + indexCorrection)
	}

	fun editNode(nodeID: String, newData: NodeDef) {
		_automationUiState.update { data ->
			if (data !is AutomationUiState.Success) return@update data

			data.copy(
				automationDraft = data.automationDraft.copy(
					topLevel = data.automationDraft.topLevel
						.findAndReplace({ it.id == nodeID }, newData)
						.map { function ->
						function.copy(
							content = function.content.findAndReplace({ it.id == nodeID }, newData)
						)
					}
				)
			)
		}
	}

	fun findVariable(id: String): VariableDef? {
		val state = automationUiState.value
		if (state !is AutomationUiState.Success) return null

		return state.automationDraft.variables.find { it.id == id }
	}

	fun addVariable(variable: VariableDef) {
		if (findVariable(variable.id) != null) return

		_automationUiState.update { data ->
			if (data !is AutomationUiState.Success) return@update data

			data.copy(
				automationDraft = data.automationDraft.copy(
					variables = data.automationDraft.variables.toSet() + variable
				)
			)
		}
	}

	fun deleteVariable(id: String) {
		_automationUiState.update { data ->
			if (data !is AutomationUiState.Success) return@update data

			data.copy(
				automationDraft = data.automationDraft.copy(
					variables = data.automationDraft.variables
						.filter { it.id != id }
						.toSet()
				)
			)
		}
	}

	fun renameVariable(id: String, newName: String) {
		val data = automationUiState.value
		if (data !is AutomationUiState.Success) return
		val variable = findVariable(id) ?: return

		deleteVariable(id)
		addVariable(variable.copy(name = newName))
	}

	fun changeVariableValue(id: String, newValue: VariableDefValue<*>) {
		val data = automationUiState.value
		if (data !is AutomationUiState.Success) return
		val variable = findVariable(id) ?: return

		deleteVariable(id)
		addVariable(variable.copy(value = newValue))
	}

	fun validateVariableName(name: String): ValidationResult {
		// TODO
		return ValidationResult.Success
	}

    fun addLog(log: AutomationLogEntry) {
        val data = automationUiState.value
        if (data !is AutomationUiState.Success) return

        CoroutineScope(Dispatchers.Main).launch {
            automationsRepository.addLog(data.automationId, log)
        }
    }

    fun deleteLog(logID: String) {
        val data = automationUiState.value
        if (data !is AutomationUiState.Success) return

        CoroutineScope(Dispatchers.Main).launch {
            automationsRepository.removeLog(data.automationId, logID)
        }
    }
}