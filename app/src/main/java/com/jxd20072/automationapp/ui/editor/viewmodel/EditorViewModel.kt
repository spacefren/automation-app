package com.jxd20072.automationapp.ui.editor.viewmodel

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.AutomationManager
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.BottomSheetManager
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.DragManager
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.EditorNodeDragState
import com.jxd20072.automationapp.utils.TimeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
    val automationManager: AutomationManager,
    val bottomSheetManager: BottomSheetManager,
    val dragManager: DragManager,
) : ViewModel() {

    private val timeManager = TimeManager(viewModelScope, 10000L)
    val currentTime = timeManager.currentTime

    init {
        viewModelScope.launch {
            async { automationManager.observeFileChanges() }
            async { automationManager.observeDraftChanges() }
            async { automationManager.observeProblems() }
        }
    }

    fun runAutomation() {
        viewModelScope.launch {
            automationManager.executeAutomation()
        }
    }

    fun onExistingNodeDrag(id: String, nodePosition: Offset, pressedPosition: Offset) {
        dragManager.dragNode(id, nodePosition, pressedPosition)
        bottomSheetManager.navigateNodeDrop()
    }

    fun onNodeDrop() {
        bottomSheetManager.closeBottomSheet()
        val dragData = dragManager.editorNodeDragState.value

        if (dragData !is EditorNodeDragState.Dragging) return

        if (dragData.isCreatingNode) {
            val nodeID = dragManager.dropCreateNode()
            if (nodeID != null) {
                bottomSheetManager.navigateNodeEdit(nodeID)
            } else {
                bottomSheetManager.closeBottomSheet()
            }
        } else {
            dragManager.dropNode()
            bottomSheetManager.closeBottomSheet()
        }
    }

    fun startEditNode(nodeId: String) {
        bottomSheetManager.navigateNodeEdit(nodeId)
    }
}