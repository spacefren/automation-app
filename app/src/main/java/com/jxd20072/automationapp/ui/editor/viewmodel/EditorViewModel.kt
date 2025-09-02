package com.jxd20072.automationapp.ui.editor.viewmodel

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jxd20072.automationapp.core.data.model.VariableDef
import com.jxd20072.automationapp.core.data.model.VariableDefValue
import com.jxd20072.automationapp.core.nodes.NodeData
import com.jxd20072.automationapp.ui.editor.viewmodel.bottomSheet.BottomSheetManager
import com.jxd20072.automationapp.ui.utils.TimeManager
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
		val executor = automationManager.executeAutomation()
		if (executor != null) {
			bottomSheetManager.logsManager
		}
	}

	fun onExistingNodeDrag(id: String, nodePosition: Offset, pressedPosition: Offset) {
		dragManager.dragNode(id, nodePosition, pressedPosition)
		bottomSheetManager.openEmpty()
	}

	fun onExistingNodeDrop() {
		dragManager.dropNode()
		bottomSheetManager.closeBottomSheet()
		bottomSheetManager.nodeSelectManager.setPageCollapsed(false)
	}

	fun onCreateNodeDrag(nodeData: NodeData, nodePosition: Offset, pressedPosition: Offset) {
		dragManager.dragCreateNode(nodeData, nodePosition, pressedPosition)
	}

	fun onCreateNodeClick(nodeData: NodeData) {
		val nodeDef = automationManager.createNodeDef(nodeData)
		automationManager.addNode(nodeDef)
		bottomSheetManager.openNodeEdit(nodeDef.id)
	}

	fun onCreateNodeDrop() {
		bottomSheetManager.nodeSelectManager.setPageCollapsed(false)
		val nodeID = dragManager.dropCreateNode()
		if (nodeID != null) {
			bottomSheetManager.openNodeEdit(nodeID)
		} else {
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