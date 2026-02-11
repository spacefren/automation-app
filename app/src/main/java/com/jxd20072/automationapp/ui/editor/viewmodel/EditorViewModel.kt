package com.jxd20072.automationapp.ui.editor.viewmodel

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.EditorNodeDragState
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.EditorSession
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = EditorViewModel.Factory::class)
class EditorViewModel @AssistedInject constructor(
    @Assisted val automationId: String,
    private val sessionFactory: EditorSession.Factory
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun create(automationId: String): EditorViewModel
    }

    val editorSession = sessionFactory.create(automationId, viewModelScope)

    init {
        viewModelScope.launch {
            async { editorSession.observeFileChanges() }
            async { editorSession.observeDraftChanges() }
            async { editorSession.observeProblems() }
        }
    }

    fun runAutomation() {
        viewModelScope.launch {
            editorSession.executeAutomation()
        }
    }

    fun onExistingNodeDrag(id: String, nodePosition: Offset, pressedPosition: Offset) {
        editorSession.dragManager.dragNode(id, nodePosition, pressedPosition)
        editorSession.bottomSheetManager.navigateNodeDrop()
    }

    fun onNodeDrop() {
        editorSession.bottomSheetManager.closeBottomSheet()
        val dragData = editorSession.dragManager.editorNodeDragState.value

        if (dragData !is EditorNodeDragState.Dragging) return

        if (dragData.isCreatingNode) {
            val nodeID = editorSession.dragManager.dropCreateNode()
            if (nodeID != null) {
                editorSession.bottomSheetManager.navigateNodeEdit(nodeID)
            } else {
                editorSession.bottomSheetManager.closeBottomSheet()
            }
        } else {
            editorSession.dragManager.dropNode()
            editorSession.bottomSheetManager.closeBottomSheet()
        }
    }

    fun startEditNode(nodeId: String) {
        editorSession.bottomSheetManager.navigateNodeEdit(nodeId)
    }
}