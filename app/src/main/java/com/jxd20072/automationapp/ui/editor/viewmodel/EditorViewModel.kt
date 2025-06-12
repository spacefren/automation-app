package com.jxd20072.automationapp.ui.editor.viewmodel

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jxd20072.automationapp.core.nodes.NodeData
import com.jxd20072.automationapp.core.nodes.NodeType
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
		when (nodeData.nodeType) {
			NodeType.ACTION -> {
				val actionDef = automationManager.createActionDef(nodeData)
				automationManager.addAction(actionDef)
				bottomSheetManager.openNodeEdit(actionDef.id)
			}

			NodeType.TRIGGER -> {
				val triggerDef = automationManager.createTriggerDef(nodeData)
				automationManager.addTrigger(triggerDef)
				bottomSheetManager.openNodeEdit(triggerDef.id)
			}

			NodeType.FUNCTION -> {
				val functionDef = automationManager.createFunctionDef(nodeData)
				automationManager.addFunction(functionDef)
			}
		}
	}

	fun onCreateNodeDrop() {
		bottomSheetManager.nodeSelectManager.setPageCollapsed(false)
		val actionID = dragManager.dropCreateNode()
		if (actionID != null) {
			bottomSheetManager.openNodeEdit(actionID)
		} else {
			bottomSheetManager.closeBottomSheet()
		}
	}
}