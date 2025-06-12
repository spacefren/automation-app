package com.jxd20072.automationapp.ui.editor.viewmodel

import androidx.compose.runtime.Immutable
import com.jxd20072.automationapp.core.data.AutomationsMapEntry
import com.jxd20072.automationapp.core.data.model.ActionDef
import com.jxd20072.automationapp.core.data.model.AutomationDef
import com.jxd20072.automationapp.core.data.model.FunctionDef
import com.jxd20072.automationapp.core.data.model.TriggerDef
import com.jxd20072.automationapp.core.data.repository.AutomationsRepository
import com.jxd20072.automationapp.core.data.repository.UserSettingsRepository
import com.jxd20072.automationapp.core.executor.executeAutomation
import com.jxd20072.automationapp.core.nodes.NodeData
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

	fun runAutomation() {
		val uiState = automationUiState.value
		if (uiState is AutomationUiState.Success) {
			executeAutomation(uiState.automationDraft)
		}
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

	fun findAction(id: String): ActionDef? {
		val state = automationUiState.value
		if (state !is AutomationUiState.Success) return null

		return (
			state.automationDraft.entryPoint +
			state.automationDraft.functions
				.map { it.content }
				.flatten() +
				state.automationDraft.triggers
					.map { it.content }
					.flatten()
			)
			.find { it.id == id }
	}

	fun findFunction(id: String): FunctionDef? {
		val state = automationUiState.value
		if (state !is AutomationUiState.Success) return null

		return state.automationDraft.functions.find { it.id == id }
	}

	fun findTrigger(id: String): TriggerDef? {
		val state = automationUiState.value
		if (state !is AutomationUiState.Success) return null

		return state.automationDraft.triggers.find { it.id == id }
	}

	fun createActionDef(nodeData: NodeData): ActionDef {
		return ActionDef(
			name = nodeData.name,
			input = emptyMap(),
			output = emptyMap()
		)
	}

	fun createTriggerDef(nodeData: NodeData): TriggerDef {
		return TriggerDef(
			name = nodeData.name,
			input = emptyMap(),
			output = emptyMap(),
			content = emptyList()
		)
	}

	fun createFunctionDef(nodeData: NodeData): FunctionDef {
		return FunctionDef(
			name = nodeData.name,
			content = emptyList()
		)
	}

	fun addAction(action: ActionDef, parentID: String? = null, index: Int? = null) {
		_automationUiState.update { state ->
			if (state !is AutomationUiState.Success) return@update state

			state.copy(
				automationDraft = if (parentID == "entryPoint") {
					state.automationDraft.copy(
						entryPoint = state.automationDraft.entryPoint.insertAt(index, action)
					)
				} else {
					state.automationDraft.copy(
						functions = state.automationDraft.functions.findAndTransform({ it.id == parentID }) {
							it.copy(
								content = it.content.insertAt(index, action)
							)
						},
						triggers = state.automationDraft.triggers.findAndTransform({ it.id == parentID }) {
							it.copy(
								content = it.content.insertAt(index, action)
							)
						}
					)
				}
			)
		}
	}

	fun addFunction(function: FunctionDef, index: Int? = null) {
		_automationUiState.update { state ->
			if (state !is AutomationUiState.Success) return@update state

			state.copy(
				automationDraft = state.automationDraft.copy(
					functions = state.automationDraft.functions.insertAt(index, function)
				)
			)
		}
	}

	fun addTrigger(trigger: TriggerDef, index: Int? = null) {
		_automationUiState.update { state ->
			if (state !is AutomationUiState.Success) return@update state

			state.copy(
				automationDraft = state.automationDraft.copy(
					triggers = state.automationDraft.triggers.insertAt(index, trigger)
				)
			)
		}
	}

	fun deleteAction(actionID: String) {
		_automationUiState.update { state ->
			if (state !is AutomationUiState.Success) return@update state

			state.copy(
				automationDraft = state.automationDraft.copy(
					entryPoint = state.automationDraft.entryPoint.filter { it.id != actionID },
					functions = state.automationDraft.functions.map { function ->
						function.copy(
							content = function.content.filter { it.id != actionID }
						)
					},
					triggers = state.automationDraft.triggers.map { trigger ->
						trigger.copy(
							content = trigger.content.filter { it.id != actionID }
						)
					}
				)
			)
		}
	}

	fun deleteFunction(functionID: String) {
		_automationUiState.update { state ->
			if (state !is AutomationUiState.Success) return@update state

			state.copy(
				automationDraft = state.automationDraft.copy(
					functions = state.automationDraft.functions.filter { it.id != functionID }
				)
			)
		}
	}

	fun deleteTrigger(triggerID: String) {
		_automationUiState.update { state ->
			if (state !is AutomationUiState.Success) return@update state

			state.copy(
				automationDraft = state.automationDraft.copy(
					triggers = state.automationDraft.triggers.filter { it.id != triggerID }
				)
			)
		}
	}

	fun moveAction(actionID: String, parentID: String?, index: Int) {
		val state = automationUiState.value
		if (state !is AutomationUiState.Success) return

		val action = findAction(actionID)!!
		val previousParent = state.automationDraft.functions
			.find { it.content.any { it.id == actionID } }

		val previousIndex = (previousParent?.content ?: state.automationDraft.entryPoint)
			.indexOfFirst { it.id == actionID }

		val indexCorrection = if (parentID == previousParent?.id && previousIndex < index) -1 else 0

		deleteAction(actionID)
		addAction(action, parentID, index + indexCorrection)
	}

	fun moveFunction(functionID: String, index: Int) {
		val state = automationUiState.value
		if (state !is AutomationUiState.Success) return

		val function = findFunction(functionID)!!
		val previousIndex = state.automationDraft.functions.indexOfFirst { it.id == functionID }

		val indexCorrection = if (previousIndex < index) -1 else 0

		deleteFunction(functionID)
		addFunction(function, index + indexCorrection)
	}

	fun moveTrigger(triggerID: String, index: Int) {
		val state = automationUiState.value
		if (state !is AutomationUiState.Success) return

		val trigger = findTrigger(triggerID)!!
		val previousIndex = state.automationDraft.functions.indexOfFirst { it.id == triggerID }

		val indexCorrection = if (previousIndex < index) -1 else 0

		deleteFunction(triggerID)
		addTrigger(trigger, index + indexCorrection)
	}

	fun editAction(actionID: String, newData: ActionDef) {
		_automationUiState.update { data ->
			if (data !is AutomationUiState.Success) return@update data

			data.copy(
				automationDraft = data.automationDraft.copy(
					entryPoint = data.automationDraft.entryPoint.findAndReplace(
						{ it.id == actionID },
						newData
					),
					functions = data.automationDraft.functions.map { function ->
						function.copy(
							content = function.content.findAndReplace({ it.id == actionID }, newData)
						)
					}
				)
			)
		}
	}
}