package com.jxd20072.automationapp.ui.editor.viewmodel

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import com.jxd20072.automationapp.core.data.model.ActionNodeDef
import com.jxd20072.automationapp.core.data.model.FunctionNodeDef
import com.jxd20072.automationapp.core.nodes.NodeData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

sealed interface EditorDragNode {
	data class Action(val data: ActionNodeDef) : EditorDragNode
	data class Function(val data: FunctionNodeDef) : EditorDragNode
}

@Immutable
sealed interface EditorNodeDragState {
	data object NotDragging : EditorNodeDragState
	data class Dragging(
		val draggingNode: EditorDragNode,
		val isCreatingNode: Boolean = false,
		val nodePosition: Offset,
		val inputOffset: Offset,
		val dropData: EditorNodeDropData
	) : EditorNodeDragState
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

@Singleton
class DragManager @Inject constructor(
	private val automationManager: AutomationManager,
) {
	private val _editorNodeDragState =
		MutableStateFlow<EditorNodeDragState>(EditorNodeDragState.NotDragging)
	val editorNodeDragState = _editorNodeDragState.asStateFlow()

	fun dragCreateAction(nodeData: NodeData, nodePosition: Offset, pressedPosition: Offset) {
		_editorNodeDragState.update {
			EditorNodeDragState.Dragging(
				nodePosition = nodePosition,
				inputOffset = pressedPosition - nodePosition,
				isCreatingNode = true,
				draggingNode = EditorDragNode.Action(
					data = ActionNodeDef(
					name = nodeData.name,
						input = emptyMap(),
						output = emptyMap()
				)),
				dropData = EditorNodeDropData.NotDropping
			)
		}
	}

	fun dropCreateAction(): String? {
		var actionID: String? = null
		_editorNodeDragState.update { state ->
			if (
				state !is EditorNodeDragState.Dragging
				|| state.draggingNode !is EditorDragNode.Action
				|| state.dropData !is EditorNodeDropData.Moving
			) return@update EditorNodeDragState.NotDragging

			actionID = state.draggingNode.data.id

			automationManager.addAction(
				state.draggingNode.data,
				state.dropData.parentId,
				state.dropData.targetIndex
			)

			EditorNodeDragState.NotDragging
		}
		return actionID
	}

	fun dragNode(id: String, nodePosition: Offset, pressedPosition: Offset) {
		val actionNode = automationManager.findAction(id)
		val functionNode = automationManager.findFunction(id)

		val draggingNode = if (actionNode != null) {
			EditorDragNode.Action(actionNode)
		} else if (functionNode != null) {
			EditorDragNode.Function(functionNode)
		} else return

		_editorNodeDragState.update {
			EditorNodeDragState.Dragging(
				nodePosition = nodePosition,
				inputOffset = pressedPosition - nodePosition,
				draggingNode = draggingNode,
				dropData = EditorNodeDropData.NotDropping
			)
		}
	}

	fun dragNodeDelta(delta: Offset) {
		_editorNodeDragState.update { state ->
			if (state !is EditorNodeDragState.Dragging) return@update state

			state.copy(
				nodePosition = state.nodePosition + delta
			)
		}
	}

	fun dropNode() {
		val state = editorNodeDragState.value
		if (state !is EditorNodeDragState.Dragging) return
		when {
			state.dropData is EditorNodeDropData.Moving -> {
				if (state.draggingNode is EditorDragNode.Action) {
					automationManager.moveAction(
						state.draggingNode.data.id,
						state.dropData.parentId,
						state.dropData.targetIndex
					)
				} else if (state.draggingNode is EditorDragNode.Function) {
					automationManager.moveFunction(
						state.draggingNode.data.id,
						state.dropData.targetIndex
					)
				}
			}

			state.dropData is EditorNodeDropData.Deleting -> {
				if (state.draggingNode is EditorDragNode.Action) {
					automationManager.deleteAction(state.draggingNode.data.id)
				} else if (state.draggingNode is EditorDragNode.Function) {
					automationManager.deleteFunction(state.draggingNode.data.id)
				}
			}
		}
		_editorNodeDragState.update {
			EditorNodeDragState.NotDragging
		}
	}

	fun enterDropLine(index: Int, parentID: String?) {
		_editorNodeDragState.update { state ->
			if (
				state !is EditorNodeDragState.Dragging
				|| (
					state.dropData !is EditorNodeDropData.Moving
						&& state.dropData !is EditorNodeDropData.NotDropping
					)
			) return@update state

			state.copy(
				dropData = EditorNodeDropData.Moving(
					targetIndex = index,
					parentId = parentID
				)
			)
		}
	}

	fun exitDropLine() {
		_editorNodeDragState.update { state ->
			if (
				state !is EditorNodeDragState.Dragging
				|| (
					state.dropData !is EditorNodeDropData.Moving
						&& state.dropData !is EditorNodeDropData.NotDropping
					)
			) return@update state

			state.copy(
				dropData = EditorNodeDropData.NotDropping
			)
		}
	}

	fun enterNodeDropZone(dropData: EditorNodeDropData) {
		_editorNodeDragState.update { state ->
			if (state !is EditorNodeDragState.Dragging) return@update state

			state.copy(
				dropData = dropData
			)
		}
	}

	fun exitNodeDropZone() {
		_editorNodeDragState.update { state ->
			if (state !is EditorNodeDragState.Dragging) return@update state

			state.copy(
				dropData = EditorNodeDropData.NotDropping
			)
		}
	}
}