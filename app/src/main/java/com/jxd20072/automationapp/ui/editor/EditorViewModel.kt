package com.jxd20072.automationapp.ui.editor

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jxd20072.automationapp.core.data.AutomationsMapEntry
import com.jxd20072.automationapp.core.data.repository.automations.ActionNodeDraft
import com.jxd20072.automationapp.core.data.repository.automations.AutomationDraft
import com.jxd20072.automationapp.core.data.repository.automations.AutomationsRepository
import com.jxd20072.automationapp.core.data.repository.automations.FunctionNodeDraft
import com.jxd20072.automationapp.core.data.repository.automations.TriggerNodeDraft
import com.jxd20072.automationapp.ui.utils.TimeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
enum class EditorDraggableScope {
	FUNCTION, TRIGGER, ACTION
}

@Immutable
sealed interface EditorDraggingData {
	data object NotDragging : EditorDraggingData
	data class Dragging(
		val draggingAction: ActionNodeDraft? = null,
		val draggingFunction: FunctionNodeDraft? = null,
		val draggingTrigger: TriggerNodeDraft? = null,
		val nodePosition: Offset,
		val inputOffset: Offset,
		val scope: EditorDraggableScope
	) : EditorDraggingData
}

@Immutable
sealed interface EditorUiState {
	data object Loading : EditorUiState
	data class Success(
		val automationData: AutomationsMapEntry,
		val automationDraft: AutomationDraft,
		val draggingData: EditorDraggingData
	) : EditorUiState
}

@HiltViewModel
class EditorViewModel @Inject constructor(
	private val automationsRepository: AutomationsRepository,
) : ViewModel() {
	private val _editorUiState = MutableStateFlow<EditorUiState>(EditorUiState.Loading)
	val editorUiState = _editorUiState.asStateFlow()

	private val timeManager = TimeManager(viewModelScope, 10000L)
	val currentTime = timeManager.currentTime

	fun loadAutomation(id: String) {
		viewModelScope.launch {
			val automation = automationsRepository.getAutomation(id)
			val automationData = automationsRepository.getAutomationMapEntry(id)
			if (automation != null && automationData != null) {
				_editorUiState.update {
					EditorUiState.Success(
						automationData = automationData,
						automationDraft = automation,
						draggingData = EditorDraggingData.NotDragging
					)
				}
			}
		}
	}

	fun onNodeDrag(
		id: String,
		nodePosition: Offset,
		pressedPosition: Offset,
		scope: EditorDraggableScope
	) {
		_editorUiState.update { currentState ->
			currentState as EditorUiState.Success

			val actionNode = when (scope) {
				EditorDraggableScope.ACTION ->
					(currentState.automationDraft.functions.flatMap { it.content }
						+ currentState.automationDraft.entryPoint)
						.find { it.id == id }

				else -> null
			}

			val functionNode = when (scope) {
				EditorDraggableScope.FUNCTION ->
					currentState.automationDraft.functions.find { it.id == id }

				else -> null
			}

			val triggerNode = when (scope) {
				EditorDraggableScope.TRIGGER ->
					currentState.automationDraft.triggers.find { it.id == id }

				else -> null
			}

			(currentState).copy(
				draggingData = EditorDraggingData.Dragging(
					scope = scope,
					nodePosition = nodePosition,
					inputOffset = pressedPosition - nodePosition,
					draggingAction = actionNode,
					draggingFunction = functionNode,
					draggingTrigger = triggerNode
				)
			)
		}
	}

	fun changeDraggingPosition(delta: Offset) {
		_editorUiState.update { currentState ->
			currentState as EditorUiState.Success
			if (currentState.draggingData !is EditorDraggingData.Dragging) {
				return
			}

			currentState.copy(
				draggingData = currentState.draggingData.copy(
					nodePosition = currentState.draggingData.nodePosition + delta
				)
			)
		}
	}

	fun onStopDrag() {
		_editorUiState.update { currentState ->
			(currentState as EditorUiState.Success).copy(
				draggingData = EditorDraggingData.NotDragging
			)
		}
	}
}