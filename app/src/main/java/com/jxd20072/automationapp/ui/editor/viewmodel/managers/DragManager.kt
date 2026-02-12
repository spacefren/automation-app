package com.jxd20072.automationapp.ui.editor.viewmodel.managers

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import com.jxd20072.automationapp.core.data.model.NodeDef
import com.jxd20072.automationapp.core.data.model.NodeType
import com.jxd20072.automationapp.core.nodes.NodeData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Immutable
sealed interface EditorNodeDragState {
    data object NotDragging : EditorNodeDragState
    data class Dragging(
        val draggingNode: NodeDef,
        val nodeDepth: Int,
        val isCreatingNode: Boolean = false,
        val nodePosition: Offset,
        val offset: Offset,
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

class DragManager(
    private val editorSession: EditorSession,
) {
    private val _editorNodeDragState =
        MutableStateFlow<EditorNodeDragState>(EditorNodeDragState.NotDragging)
    val editorNodeDragState = _editorNodeDragState.asStateFlow()

    fun dragCreateNode(nodeData: NodeData, nodePosition: Offset, offset: Offset) {
        _editorNodeDragState.update {
            EditorNodeDragState.Dragging(
                nodePosition = nodePosition,
                offset = offset,
                nodeDepth = when (nodeData.nodeType) {
                    NodeType.ACTION -> 1
                    else -> 0
                },
                isCreatingNode = true,
                draggingNode = editorSession.createNodeDef(nodeData),
                dropData = EditorNodeDropData.NotDropping
            )
        }
    }

    fun dragNode(id: String, nodePosition: Offset, offset: Offset) {
        val (node, depth) = editorSession.findNodeWithDepth(id) ?: return

        _editorNodeDragState.update {
            EditorNodeDragState.Dragging(
                nodePosition = nodePosition,
                offset = offset,
                draggingNode = node,
                nodeDepth = depth,
                dropData = EditorNodeDropData.NotDropping
            )
        }
    }

    fun dropCreateNode(): String? {
        var nodeID: String? = null
        _editorNodeDragState.update { state ->
            if (
                state !is EditorNodeDragState.Dragging
                || state.dropData !is EditorNodeDropData.Moving
            ) return@update EditorNodeDragState.NotDragging

            editorSession.addNode(
                state.draggingNode,
                state.dropData.parentId,
                state.dropData.targetIndex
            )

            nodeID = state.draggingNode.id

            EditorNodeDragState.NotDragging
        }
        return nodeID
    }

    fun dropNode() {
        val state = editorNodeDragState.value
        if (state !is EditorNodeDragState.Dragging) return

        when {
            state.dropData is EditorNodeDropData.Moving -> {
                editorSession.moveNode(
                    state.draggingNode.id,
                    state.dropData.parentId,
                    state.dropData.targetIndex
                )
            }

            state.dropData is EditorNodeDropData.Deleting -> {
                editorSession.deleteNode(state.draggingNode.id)
            }
        }
        _editorNodeDragState.update {
            EditorNodeDragState.NotDragging
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