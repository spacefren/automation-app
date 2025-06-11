package com.jxd20072.automationapp.ui.editor

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberDraggable2DState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.jxd20072.automationapp.core.data.model.ActionDef
import com.jxd20072.automationapp.core.nodes.NodeData
import com.jxd20072.automationapp.core.nodes.actionNodes
import com.jxd20072.automationapp.ui.editor.editorBottomSheet.EditorBottomSheet
import com.jxd20072.automationapp.ui.editor.nodeTree.DraggingNode
import com.jxd20072.automationapp.ui.editor.nodeTree.EditorNodeTree
import com.jxd20072.automationapp.ui.editor.viewmodel.AutomationUiState
import com.jxd20072.automationapp.ui.editor.viewmodel.EditorNodeDragState
import com.jxd20072.automationapp.ui.editor.viewmodel.EditorViewModel
import com.jxd20072.automationapp.ui.editor.viewmodel.bottomSheet.NodeEditState
import com.jxd20072.automationapp.ui.shared.Layer
import com.jxd20072.automationapp.ui.theme.LocalTheme
import com.jxd20072.automationapp.ui.userSettings.UserSettingsUiState
import com.jxd20072.automationapp.ui.userSettings.UserSettingsViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AutomationEditor(
	editorViewModel: EditorViewModel = hiltViewModel(),
	settingsViewModel: UserSettingsViewModel = hiltViewModel(),
	returnHome: (saveChanges: Boolean) -> Unit
) {
	val currentTime by editorViewModel.currentTime.collectAsState()
	val settingsUiState by settingsViewModel.userSettingsUiState.collectAsState()

	val automationManager = editorViewModel.automationManager
	val bottomSheetManager = editorViewModel.bottomSheetManager
	val nodeSearchManager = bottomSheetManager.nodeSearchManager
	val nodeEditManager = bottomSheetManager.nodeEditManager
	val dragManager = editorViewModel.dragManager

	val automationState by automationManager.automationUiState.collectAsState(AutomationUiState.Loading)
	val draggingData by dragManager.editorNodeDragState.collectAsState()
	val bottomSheetState by bottomSheetManager.bottomSheetState.collectAsState()
	val nodeSelectState by nodeSearchManager.nodeSelectState.collectAsState()
	val nodeEditState by nodeEditManager.editingNodeState.collectAsState()

	val draggableState = rememberDraggable2DState { delta ->
		editorViewModel.dragManager.dragNodeDelta(delta)
	}

	var editingActionNode: ActionDef? = null
	var editingNodeData: NodeData? = null
	if (nodeEditState is NodeEditState.Editing) {
		editingActionNode = automationManager.findAction((nodeEditState as NodeEditState.Editing).nodeID)
		if (editingActionNode != null) {
			editingNodeData = actionNodes[editingActionNode.name]?.data
		}
	}

	if (automationState is AutomationUiState.Success) {

		Layer(
			modifier = Modifier
				.fillMaxSize()
				.background(LocalTheme.colorScheme.background)
		) {
			Column {
				EditorTopBar(
					automationData = (automationState as AutomationUiState.Success).automationData,
					currentTime = currentTime,
					returnHome = returnHome,
					saveAutomation = { automationManager.trySaveAutomation(true) },
					runAutomation = automationManager::runAutomation,
				)
				EditorNodeTree(
					automationDraft = (automationState as AutomationUiState.Success).automationDraft,
					onNodeDrag = editorViewModel::onExistingNodeDrag,
					onStopDrag = editorViewModel::onExistingNodeDrop,
					draggableState = draggableState,
					draggingData = draggingData,
					onDropLineEnter = dragManager::enterDropLine,
					onDropLineExit = dragManager::exitDropLine
				)
			}
		}
		DraggingNode(
			draggingData = draggingData
		)
		EditorFloatingAddButton(
			hidden = draggingData is EditorNodeDragState.Dragging,
			onCreateNodePressed = bottomSheetManager::openNodeSelect,
			onCreateFunctionPressed = { }
		)
		EditorBottomSheet(
			onDismiss = bottomSheetManager::closeBottomSheet,
			bottomSheetState = bottomSheetState,
			nodeSelectState = nodeSelectState,
			nodeEditScreenState = nodeEditState,
			editingNode = editingActionNode,
			editingNodeData = editingNodeData,
			setNodeConfig = nodeEditManager::setConfig,
			resetDefaultNodeConfig = nodeEditManager::resetDefaultConfig,
			onNodeSearch = bottomSheetManager.nodeSearchManager::changeNodeSearchQuery,
			dragData = draggingData,
			onStartDrag = editorViewModel::onCreateNodeDrag,
			onStopDrag = editorViewModel::onCreateNodeDrop,
			onClick = nodeSearchManager::addActionClick,
			draggableState = draggableState,
			onDropZoneEnter = dragManager::enterNodeDropZone,
			onDropZoneExit = dragManager::exitNodeDropZone,
			autoOpenKeyboard = settingsUiState is UserSettingsUiState.Success
				&& (settingsUiState as UserSettingsUiState.Success).settings.autoOpenKeyboard.value,
		)
	}
}