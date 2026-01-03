package com.jxd20072.automationapp.ui.editor.viewmodel

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jxd20072.automationapp.core.data.model.VariableDef
import com.jxd20072.automationapp.core.data.model.VariableDefValue
import com.jxd20072.automationapp.core.nodes.NodeData
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
            async {
                automationManager.observeFileChanges()
            }
            automationManager.observeDraftChanges()
        }
    }

    fun runAutomation() {
        viewModelScope.launch {
            automationManager.executeAutomation()
        }
    }

    fun onExistingNodeDrag(id: String, nodePosition: Offset, pressedPosition: Offset) {
        dragManager.dragNode(id, nodePosition, pressedPosition)
        bottomSheetManager.openNodeDrop()
    }

    fun onCreateNodeDrag(nodeData: NodeData, nodePosition: Offset, pressedPosition: Offset) {
        bottomSheetManager.openNodeDrop()
        dragManager.dragCreateNode(nodeData, nodePosition, pressedPosition)
    }

    fun onCreateNodeClick(nodeData: NodeData) {
        val nodeDef = automationManager.createNodeDef(nodeData)
        automationManager.addNode(nodeDef)
        bottomSheetManager.openNodeEdit(nodeDef.id)
    }

    fun onNodeDrop() {
        bottomSheetManager.nodeSelectManager.setPageCollapsed(false)
        val dragData = dragManager.editorNodeDragState.value

        if (dragData !is EditorNodeDragState.Dragging) return

        bottomSheetManager.nodeSelectManager.setPageCollapsed(false)

        if (dragData.isCreatingNode) {
            val nodeID = dragManager.dropCreateNode()
            if (nodeID != null) {
                bottomSheetManager.openNodeEdit(nodeID)
            } else {
                bottomSheetManager.closeBottomSheet()
            }
        } else {
            dragManager.dropNode()
            bottomSheetManager.closeBottomSheet()
        }
    }

    fun onCreateVariable() {
        val newVariable = VariableDef(
            name = "Unnamed variable",
            value = VariableDefValue.NUMBER("0"),
        )
        automationManager.addVariable(newVariable)
        bottomSheetManager.openVariableEdit(newVariable.id, true)
    }
}