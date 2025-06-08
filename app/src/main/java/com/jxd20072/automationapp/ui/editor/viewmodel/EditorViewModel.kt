package com.jxd20072.automationapp.ui.editor.viewmodel

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jxd20072.automationapp.core.nodes.actions.ActionNodeData
import com.jxd20072.automationapp.ui.editor.viewmodel.bottomSheet.BottomSheetManager
import com.jxd20072.automationapp.ui.editor.viewmodel.bottomSheet.CollapsedSheetManager
import com.jxd20072.automationapp.ui.utils.TimeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
	val automationManager: AutomationManager,
	val bottomSheetManager: BottomSheetManager,
	val collapsedSheetManager: CollapsedSheetManager,
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
		collapsedSheetManager.showDropPanels(true)
	}

	fun onExistingNodeDrop() {
		dragManager.dropNode()
		bottomSheetManager.closeBottomSheet()
		collapsedSheetManager.hideCollapsedScreen()
	}

	fun onCreateNodeDrag(nodeData: ActionNodeData, nodePosition: Offset, pressedPosition: Offset) {
		dragManager.dragCreateAction(nodeData, nodePosition, pressedPosition)
		collapsedSheetManager.showDropPanels(false)
	}

	fun onCreateNodeDrop() {
		collapsedSheetManager.hideCollapsedScreen()
		val actionID = dragManager.dropCreateAction()
		if (actionID != null) {
			bottomSheetManager.openNodeEdit(actionID)
		} else {
			bottomSheetManager.closeBottomSheet()
		}
	}
}