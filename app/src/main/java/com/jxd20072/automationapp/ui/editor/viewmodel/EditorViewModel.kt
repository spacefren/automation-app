package com.jxd20072.automationapp.ui.editor.viewmodel

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jxd20072.automationapp.core.data.AutomationsMapEntry
import com.jxd20072.automationapp.core.data.model.ActionNodeDef
import com.jxd20072.automationapp.core.data.model.AutomationDef
import com.jxd20072.automationapp.core.data.model.FunctionNodeDef
import com.jxd20072.automationapp.core.data.repository.UserSettingsRepository
import com.jxd20072.automationapp.core.data.repository.automations.AutomationsRepository
import com.jxd20072.automationapp.core.executor.executeAutomation
import com.jxd20072.automationapp.core.nodes.actionNodes
import com.jxd20072.automationapp.core.nodes.actions.ActionNodeData
import com.jxd20072.automationapp.ui.utils.TimeManager
import com.jxd20072.automationapp.ui.utils.reorderElementInList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.xdrop.fuzzywuzzy.FuzzySearch
import javax.inject.Inject

@Immutable
enum class EditorDraggableScope {
	FUNCTION, ACTION
}

@Immutable
sealed interface EditorNodeDragData {
	data object NotDragging : EditorNodeDragData
	data class Dragging(
		val draggingAction: ActionNodeDef? = null,
		val draggingFunction: FunctionNodeDef? = null,
		val isCreatingNode: Boolean = false,
		val nodePosition: Offset,
		val inputOffset: Offset,
		val scope: EditorDraggableScope,
		val dropData: EditorNodeDropData
	) : EditorNodeDragData
}

@Immutable
sealed interface EditorNodeDropData {
	data object NotDropping : EditorNodeDropData
	data class Moving(
		val targetIndex: Int,
		val parentId: String? = null,
	) : EditorNodeDropData
	data object Deleting : EditorNodeDropData
	data object Cancelling : EditorNodeDropData
}

@Immutable
data class NodeListEntry(
	val data: ActionNodeData,
	val searchMatchRatio: Int,
)

@Immutable
data class CategoryListEntry(
	val categoryName: String,
	val nodes: List<NodeListEntry>,
	val searchMatchRatio: Int
)

@Immutable
sealed interface EditorBottomSheetState {
	data class Open(
		val collapsedScreen: EditorBottomSheetCollapsedScreen,
		val screen: EditorBottomSheetScreen,
	) : EditorBottomSheetState
	data object Closed : EditorBottomSheetState
}

val defaultNodeList = actionNodes.values
	.map { it.data.category }
	.distinct()
	.map { categoryName ->
		CategoryListEntry(
			categoryName = categoryName,
			nodes = actionNodes.values
				.filter { node -> node.data.category == categoryName }
				.map { node ->
					NodeListEntry(
						searchMatchRatio = 0,
						data = node.data
					)
				},
			searchMatchRatio = 0
		)
	}

@Immutable
sealed interface EditorBottomSheetScreen {
	data object Empty : EditorBottomSheetScreen
	data class NodeSelect(
		val isCollapsed: Boolean = false,
		val nodeSearchQuery: String = "",
		val nodes: List<CategoryListEntry> = defaultNodeList,
		val filteredNodes: List<CategoryListEntry> = nodes
	) : EditorBottomSheetScreen
	data class NodeEdit(
		val editingNodeId: String,
	) : EditorBottomSheetScreen
}

@Immutable
sealed interface EditorBottomSheetCollapsedScreen {
	data object Empty : EditorBottomSheetCollapsedScreen
	data class DropPanels(
		val showDelete: Boolean = false,
	) : EditorBottomSheetCollapsedScreen
}

@Immutable
sealed interface EditorUiState {
	data object Loading : EditorUiState
	data class Success(
		val automationId: String,
		val automationData: AutomationsMapEntry,
		val automationDraft: AutomationDef,
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
					val newData =
						automationData.automationsMap[uiState.automationId] ?: return@update uiState
					uiState.copy(automationData = newData)
				}
			}
		}
	}

	fun tryUpdateUiState(transform: (EditorUiState.Success) -> EditorUiState) {
		return _editorUiState.update { state ->
			state as EditorUiState.Success
			transform(state)
		}
	}

	fun loadAutomation(id: String) {
		viewModelScope.launch {
			val automation = automationsRepository.getAutomationDef(id)
			val automationData = automationsRepository.getAutomationMapEntry(id)
			if (automation != null && automationData != null) {
				_editorUiState.update {
					EditorUiState.Success(
						automationId = id,
						automationData = automationData,
						automationDraft = automation.copy(),
						draggingData = EditorNodeDragData.NotDragging,
						bottomSheetState = EditorBottomSheetState.Closed
					)
				}
			}
		}
	}

	fun runAutomation() {
		CoroutineScope(Dispatchers.Default).launch {
			val uiState = editorUiState.value
			if (uiState is EditorUiState.Success) {
				executeAutomation(uiState.automationDraft)
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
		tryUpdateUiState { uiState ->
			uiState.copy(
				bottomSheetState = EditorBottomSheetState.Closed
			)
		}
	}

	fun startNodeCreationFlow() {
		tryUpdateUiState { uiState ->
			uiState.copy(
				bottomSheetState = EditorBottomSheetState.Open(
					screen = EditorBottomSheetScreen.NodeSelect(),
					collapsedScreen = EditorBottomSheetCollapsedScreen.Empty
				)
			)
		}
		filterNodes()
	}

	fun filterNodes() {
		tryUpdateUiState { uiState ->
			uiState.bottomSheetState as EditorBottomSheetState.Open
			uiState.bottomSheetState.screen as EditorBottomSheetScreen.NodeSelect
			val searchQuery = uiState.bottomSheetState.screen.nodeSearchQuery.lowercase()

			if (searchQuery.isBlank()) {
				return@tryUpdateUiState uiState.copy(
					bottomSheetState = uiState.bottomSheetState.copy(
						screen = uiState.bottomSheetState.screen.copy(
							filteredNodes = uiState.bottomSheetState.screen.nodes
						),
					)
				)
			}

			val filteredNodes = uiState.bottomSheetState.screen.nodes

				// Search nodes
				.map { categoryListEntry ->
					categoryListEntry.copy(
						nodes = categoryListEntry.nodes.map { nodeListEntry ->
							nodeListEntry.copy(
								searchMatchRatio = FuzzySearch.partialRatio(
									searchQuery,
									nodeListEntry.data.displayName.lowercase()
								)
							)
						}
					)
				}

				// Search categories
				.map { categoryListEntry ->
					categoryListEntry.copy(
						searchMatchRatio = FuzzySearch.partialRatio(
							searchQuery,
							categoryListEntry.categoryName.lowercase()
						)
					)
				}

				// Filter
				.map { categoryListEntry ->
					categoryListEntry.copy(
						nodes = categoryListEntry.nodes.filter { it.searchMatchRatio > 75 }
					)
				}

				.filter { categoryListEntry ->
					categoryListEntry.searchMatchRatio > 75 || categoryListEntry.nodes.isNotEmpty()
				}

			uiState.copy(
				bottomSheetState = uiState.bottomSheetState.copy(
					screen = uiState.bottomSheetState.screen.copy(
						filteredNodes = filteredNodes
					),
				)
			)
		}
	}

	fun changeNodeSearchQuery(query: String) {
		tryUpdateUiState { uiState ->
			uiState.bottomSheetState as EditorBottomSheetState.Open
			uiState.bottomSheetState.screen as EditorBottomSheetScreen.NodeSelect
			uiState.copy(
				bottomSheetState = uiState.bottomSheetState.copy(
					screen = uiState.bottomSheetState.screen.copy(
						nodeSearchQuery = query
					),
				)
			)
		}
		filterNodes()
	}

	fun createActionNode(nodeData: ActionNodeData): ActionNodeDef {
		return ActionNodeDef(
			name = nodeData.name,
			input = nodeData.inputDefinitions.mapValues { it.value.defaultValue }
		)
	}

	fun onActionCreateDrag(nodeData: ActionNodeData, nodePosition: Offset, pressedPosition: Offset) {
		tryUpdateUiState { currentState ->
			currentState.bottomSheetState as EditorBottomSheetState.Open
			currentState.bottomSheetState.screen as EditorBottomSheetScreen.NodeSelect

			currentState.copy(
				draggingData = EditorNodeDragData.Dragging(
					scope = EditorDraggableScope.ACTION,
					nodePosition = nodePosition,
					inputOffset = pressedPosition - nodePosition,
					isCreatingNode = true,
					draggingAction = createActionNode(nodeData),
					draggingFunction = null,
					dropData = EditorNodeDropData.NotDropping
				),
				bottomSheetState = currentState.bottomSheetState.copy(
					collapsedScreen = EditorBottomSheetCollapsedScreen.DropPanels(
						showDelete = false
					),
				)
			)
		}
	}

	fun onActionCreateClick(nodeData: ActionNodeData) {
		tryUpdateUiState { currentState ->
			currentState.bottomSheetState as EditorBottomSheetState.Open
			currentState.bottomSheetState.screen as EditorBottomSheetScreen.NodeSelect

			currentState.copy(
				bottomSheetState = EditorBottomSheetState.Closed,
				automationDraft = currentState.automationDraft.copy(
					functions = currentState.automationDraft.functions
						.toMutableList()
						.also { functions ->
							val onStartFuncIndex = functions.indexOfFirst { it.name == "OnStart" }

							if (onStartFuncIndex != -1) {
								val onStartFunc = functions[onStartFuncIndex]
								functions[onStartFuncIndex] = onStartFunc.copy(
									content = onStartFunc.content + createActionNode(nodeData)
								)
							} else {
								functions.add(
									FunctionNodeDef(
										name = "OnStart",
										content = listOf(createActionNode(nodeData)),
										input = emptyMap(),
										output = emptyMap()
									)
								)
							}
						}
				)
			)
		}
	}

	fun onStopActionCreateDrag() {
		tryUpdateUiState { currentState ->
			val automationDraft = currentState.automationDraft
			currentState.bottomSheetState as EditorBottomSheetState.Open
			currentState.bottomSheetState.screen as EditorBottomSheetScreen.NodeSelect

			val draggingData = currentState.draggingData as EditorNodeDragData.Dragging
			draggingData.draggingAction!!

			val dropData = draggingData.dropData
			val newState = when {
				dropData is EditorNodeDropData.Moving -> {
					val parentIndex = automationDraft.functions.indexOfFirst { it.id == dropData.parentId }

					currentState.copy(
						automationDraft = automationDraft.copy(
							functions = automationDraft.functions.toMutableList().also { functions ->
								functions[parentIndex] = functions[parentIndex].copy(
									content = functions[parentIndex].content.toMutableList().also { content ->
										content.add(dropData.targetIndex, draggingData.draggingAction)
									}
								)
							}
						),
						bottomSheetState = EditorBottomSheetState.Open(
							collapsedScreen = EditorBottomSheetCollapsedScreen.Empty,
							screen = EditorBottomSheetScreen.NodeEdit(
								editingNodeId = draggingData.draggingAction.id
							),
						),
						draggingData = EditorNodeDragData.NotDragging,
					)
				}

				else -> {
					currentState.copy(
						bottomSheetState = EditorBottomSheetState.Closed,
						draggingData = EditorNodeDragData.NotDragging,
					)
				}
			}
			newState
		}
		trySaveAutomation()
	}

	fun onNodeDrag(
		id: String,
		nodePosition: Offset,
		pressedPosition: Offset,
		scope: EditorDraggableScope
	) {
		tryUpdateUiState { currentState ->
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
				),
				bottomSheetState = EditorBottomSheetState.Open(
					screen = EditorBottomSheetScreen.Empty,
					collapsedScreen = EditorBottomSheetCollapsedScreen.DropPanels(
						showDelete = functionNode?.name != "OnStart"
					)
				)
			)
		}
	}

	fun changeDraggingPosition(delta: Offset) {
		tryUpdateUiState { currentState ->
			if (currentState.draggingData !is EditorNodeDragData.Dragging) {
				return@tryUpdateUiState currentState
			}

			currentState.copy(
				draggingData = currentState.draggingData.copy(
					nodePosition = currentState.draggingData.nodePosition + delta
				)
			)
		}
	}

	fun onNodeMoveRequestTransform(currentState: EditorUiState.Success): EditorUiState {
		val automationDraft = currentState.automationDraft
		val draggingData = currentState.draggingData
		if (draggingData !is EditorNodeDragData.Dragging) return currentState

		var newState = currentState
		val dropData = draggingData.dropData as EditorNodeDropData.Moving

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
		}

		return newState.copy(
			bottomSheetState = EditorBottomSheetState.Closed
		)
	}

	fun onNodeDeleteRequestTransform(currentState: EditorUiState.Success): EditorUiState {
		val automationDraft = currentState.automationDraft
		val draggingData = currentState.draggingData
		if (draggingData !is EditorNodeDragData.Dragging) return currentState

		var newState = currentState

		if (draggingData.draggingAction != null) {
			newState = newState.copy(
				automationDraft = automationDraft.copy(
					functions = automationDraft.functions
						.toMutableList()
						.map { function ->
							function.copy(
								content = function.content.filter { it.id != draggingData.draggingAction.id }
							)
						}
				)
			)
		} else if (draggingData.draggingFunction != null) {
			newState = newState.copy(
				automationDraft = automationDraft.copy(
					functions = automationDraft.functions.filter { it.id != draggingData.draggingFunction.id }
				)
			)
		}

		return newState.copy(
			bottomSheetState = EditorBottomSheetState.Closed
		)
	}

	fun onStopNodeDrag() {
		tryUpdateUiState { currentState ->
			val newState = when {
				currentState.draggingData !is EditorNodeDragData.Dragging -> {
					currentState
				}

				currentState.draggingData.dropData is EditorNodeDropData.Moving -> {
					onNodeMoveRequestTransform(currentState)
				}

				currentState.draggingData.dropData is EditorNodeDropData.Deleting -> {
					onNodeDeleteRequestTransform(currentState)
				}

				else -> {
					currentState.copy(
						bottomSheetState = EditorBottomSheetState.Closed
					)
				}
			} as EditorUiState.Success

			newState.copy(
				draggingData = EditorNodeDragData.NotDragging
			)
		}
		trySaveAutomation()
	}

	fun onNodeDropLineEnter(targetIndex: Int, parentId: String?) {
		tryUpdateUiState { currentState ->

			if (currentState.draggingData !is EditorNodeDragData.Dragging
				|| currentState.draggingData.dropData is EditorNodeDropData.Deleting
				|| currentState.draggingData.dropData is EditorNodeDropData.Cancelling
			) {
				return@tryUpdateUiState currentState
			}

			currentState.copy(
				draggingData = currentState.draggingData.copy(
					dropData = EditorNodeDropData.Moving(
						targetIndex = targetIndex,
						parentId = parentId
					)
				)
			)
		}
	}

	fun onNodeDropLineExit() {
		tryUpdateUiState { currentState ->
			if (currentState.draggingData !is EditorNodeDragData.Dragging
				|| currentState.draggingData.dropData is EditorNodeDropData.Deleting
				|| currentState.draggingData.dropData is EditorNodeDropData.Cancelling
			) {
				return@tryUpdateUiState currentState
			}

			currentState.copy(
				draggingData = currentState.draggingData.copy(
					dropData = EditorNodeDropData.NotDropping
				)
			)
		}
	}

	fun onNodeDropZoneEnter(dropData: EditorNodeDropData) {
		tryUpdateUiState { currentState ->
			if (currentState.draggingData !is EditorNodeDragData.Dragging) {
				return@tryUpdateUiState currentState
			}

			currentState.copy(
				draggingData = currentState.draggingData.copy(
					dropData = dropData
				)
			)
		}
	}

	fun onNodeDropZoneExit() {
		tryUpdateUiState { currentState ->
			if (currentState.draggingData !is EditorNodeDragData.Dragging) {
				return@tryUpdateUiState currentState
			}

			currentState.copy(
				draggingData = currentState.draggingData.copy(
					dropData = EditorNodeDropData.NotDropping
				)
			)
		}
	}
}