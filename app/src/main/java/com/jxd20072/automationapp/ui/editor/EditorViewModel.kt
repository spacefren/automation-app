package com.jxd20072.automationapp.ui.editor

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jxd20072.automationapp.core.data.AutomationsMapEntry
import com.jxd20072.automationapp.core.data.repository.UserSettingsRepository
import com.jxd20072.automationapp.core.data.repository.automations.ActionNodeDraft
import com.jxd20072.automationapp.core.data.repository.automations.AutomationDraft
import com.jxd20072.automationapp.core.data.repository.automations.AutomationsRepository
import com.jxd20072.automationapp.core.data.repository.automations.FunctionNodeDraft
import com.jxd20072.automationapp.core.nodes.actions.ActionNodeData
import com.jxd20072.automationapp.ui.utils.TimeManager
import com.jxd20072.automationapp.ui.utils.reorderElementInList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
enum class EditorDraggableScope {
	FUNCTION, ACTION
}

@Immutable
sealed interface EditorNodeDragData {
	data object NotDragging : EditorNodeDragData
	data class Dragging(
		val draggingAction: ActionNodeDraft? = null,
		val draggingFunction: FunctionNodeDraft? = null,
		val nodePosition: Offset,
		val inputOffset: Offset,
		val scope: EditorDraggableScope,
		val dropData: EditorNodeDropData
	) : EditorNodeDragData
}

@Immutable
sealed interface EditorNodeDropData {
	data object NotDropping : EditorNodeDropData
	data class Dropping(
		val targetIndex: Int,
		val parentId: String? = null,
	) : EditorNodeDropData
}

@Immutable
enum class EditorBottomSheetScreen {
	NODE_SELECT, NODE_EDIT
}

@Immutable
data class NodeListEntry(
	val matchesSearch: Boolean,
	val data: ActionNodeData,
)

@Immutable
data class CategoryListEntry(
	val categoryName: String,
	val nodes: List<NodeListEntry>,
)

@Immutable
data class EditorBottomSheetState(
	val isOpen: Boolean = false,
	val currentScreen: EditorBottomSheetScreen = EditorBottomSheetScreen.NODE_SELECT,

	val nodeSearchQuery: String = "",
	val nodes: List<CategoryListEntry> = emptyList(),
	val filteredNodes: List<CategoryListEntry> = nodes
)

@Immutable
sealed interface EditorUiState {
	data object Loading : EditorUiState
	data class Success(
		val automationId: String,
		val automationData: AutomationsMapEntry,
		val automationDraft: AutomationDraft,
		val draggingData: EditorNodeDragData,
		val bottomSheetState: EditorBottomSheetState
	) : EditorUiState
}

@HiltViewModel
class EditorViewModel @Inject constructor(
	private val automationsRepository: AutomationsRepository,
	private val settingsRepository: UserSettingsRepository
) : ViewModel() {
	private val _editorUiState = MutableStateFlow<EditorUiState>(EditorUiState.Loading)
	val editorUiState = _editorUiState.asStateFlow()

	private val timeManager = TimeManager(viewModelScope, 10000L)
	val currentTime = timeManager.currentTime

	init {
		viewModelScope.launch {
			automationsRepository.automationsDataFlow.collect { automationData ->
				_editorUiState.update { uiState ->
					if (uiState !is EditorUiState.Success) return@update uiState
					val newData = automationData.automationsMap[uiState.automationId] ?: return@update uiState
					uiState.copy(automationData = newData)
				}
			}
		}
	}

	fun loadAutomation(id: String) {
		viewModelScope.launch {
			val automation = automationsRepository.getAutomationDraft(id)
			val automationData = automationsRepository.getAutomationMapEntry(id)
			if (automation != null && automationData != null) {
				_editorUiState.update {
					EditorUiState.Success(
						automationId = id,
						automationData = automationData,
						automationDraft = automation,
						draggingData = EditorNodeDragData.NotDragging,
						bottomSheetState = EditorBottomSheetState()
					)
				}
			}
		}
	}

	fun trySaveAutomation(force: Boolean = false) {
		viewModelScope.launch {
			if (force || settingsRepository.getSettings().autoSave) {
				val uiState = editorUiState.value
				if (uiState is EditorUiState.Success) {
					automationsRepository.writeAutomation(uiState.automationId, uiState.automationDraft)
				}
			}
		}
	}

	fun onBottomSheetDismissed() {
		_editorUiState.update { uiState ->
			uiState as EditorUiState.Success
			uiState.copy(
				bottomSheetState = EditorBottomSheetState()
			)
		}
	}

	fun startNodeCreationFlow() {
		_editorUiState.update { uiState ->
			uiState as EditorUiState.Success
			uiState.copy(
				bottomSheetState = uiState.bottomSheetState.copy(
					isOpen = true,
					currentScreen = EditorBottomSheetScreen.NODE_SELECT
				)
			)
		}
	}

	fun filterNodes() {
		_editorUiState.update { uiState ->
			uiState as EditorUiState.Success
			val searchQuery = uiState.bottomSheetState.nodeSearchQuery

			uiState.copy(
				bottomSheetState = uiState.bottomSheetState.copy(
					filteredNodes = uiState.bottomSheetState.nodes
						.map { categoryListEntry ->
							categoryListEntry.copy(
								nodes = categoryListEntry.nodes.map { nodeListEntry ->
									nodeListEntry.copy(
										matchesSearch = nodeListEntry.data.name
											.lowercase()
											.contains(searchQuery.lowercase())
									)
								}
							)
						}
						.filter { categoryListEntry ->
							categoryListEntry.categoryName.lowercase().contains(searchQuery.lowercase())
								|| categoryListEntry.nodes.any { it.matchesSearch }
						}
				)
			)
		}
	}

	fun changeNodeSearchQuery(query: String) {
		_editorUiState.update { uiState ->
			uiState as EditorUiState.Success
			uiState.copy(
				bottomSheetState = uiState.bottomSheetState.copy(
					nodeSearchQuery = query
				)
			)
		}
		filterNodes()
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
					currentState.automationDraft.functions
						.flatMap { it.content }
						.find { it.id == id }

				else -> null
			}

			val functionNode = when (scope) {
				EditorDraggableScope.FUNCTION ->
					currentState.automationDraft.functions.find { it.id == id }

				else -> null
			}

			currentState.copy(
				draggingData = EditorNodeDragData.Dragging(
					scope = scope,
					nodePosition = nodePosition,
					inputOffset = pressedPosition - nodePosition,
					draggingAction = actionNode,
					draggingFunction = functionNode,
					dropData = EditorNodeDropData.NotDropping
				)
			)
		}
	}

	fun changeDraggingPosition(delta: Offset) {
		_editorUiState.update { currentState ->
			currentState as EditorUiState.Success
			if (currentState.draggingData !is EditorNodeDragData.Dragging) {
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
			val automationDraft = (currentState as EditorUiState.Success).automationDraft
			val draggingData = currentState.draggingData
			if (draggingData !is EditorNodeDragData.Dragging) return

			var newState = currentState.copy(draggingData = EditorNodeDragData.NotDragging)

			val dropData = draggingData.dropData
			if (dropData !is EditorNodeDropData.Dropping) return@update newState

			if (draggingData.draggingAction != null) {
				val previousParent = automationDraft.functions
					.find { it.content.contains(draggingData.draggingAction) }!!
				val previousParentIndex = automationDraft.functions.indexOf(previousParent)
				val newParent = automationDraft.functions.find { it.id == dropData.parentId }!!
				val newParentIndex = automationDraft.functions.indexOf(newParent)
				val previousItemIndex =
					previousParent.content.indexOf(draggingData.draggingAction)

				newState = newState.copy(
					automationDraft = automationDraft.copy(
						functions = automationDraft.functions
							.toMutableList().also { functions ->
								functions[previousParentIndex] = functions[previousParentIndex].copy(
									content = functions[previousParentIndex].content
										.toMutableList().also { previousParentContent ->
											previousParentContent.removeAt(previousItemIndex)
										}
								)
								functions[newParentIndex] = functions[newParentIndex].copy(
									content = functions[newParentIndex].content
										.toMutableList().also { newParentContent ->
											newParentContent.add(
												dropData.targetIndex - if (previousParent == newParent
														&& previousItemIndex < dropData.targetIndex
												) 1 else 0,
												draggingData.draggingAction
											)
										}
								)
							}
					)
				)

			} else if (draggingData.draggingFunction != null) {

				val previousIndex = automationDraft.functions.indexOf(draggingData.draggingFunction)

				newState = newState.copy(
					automationDraft = automationDraft.copy(
						functions = reorderElementInList(
							automationDraft.functions,
							previousIndex,
							dropData.targetIndex
						)
					)
				)
			} else return@update newState

			newState
		}
		trySaveAutomation()
	}

	fun onNodeDropZoneEnter(targetIndex: Int, parentId: String?) {
		_editorUiState.update { currentState ->
			currentState as EditorUiState.Success
			if (currentState.draggingData !is EditorNodeDragData.Dragging) {
				return
			}

			currentState.copy(
				draggingData = currentState.draggingData.copy(
					dropData = EditorNodeDropData.Dropping(
						targetIndex = targetIndex,
						parentId = parentId
					)
				)
			)
		}
	}

	fun onNodeDropZoneExit() {
		_editorUiState.update { currentState ->
			currentState as EditorUiState.Success
			if (currentState.draggingData !is EditorNodeDragData.Dragging) {
				return
			}

			currentState.copy(
				draggingData = currentState.draggingData.copy(
					dropData = EditorNodeDropData.NotDropping
				)
			)
		}
	}
}