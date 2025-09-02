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
import com.jxd20072.automationapp.core.data.model.NodeType
import com.jxd20072.automationapp.ui.editor.editorBottomSheet.EditorBottomSheet
import com.jxd20072.automationapp.ui.editor.nodeTree.DraggingNode
import com.jxd20072.automationapp.ui.editor.nodeTree.EditorNodeTree
import com.jxd20072.automationapp.ui.editor.viewmodel.AutomationUiState
import com.jxd20072.automationapp.ui.editor.viewmodel.EditorNodeDragState
import com.jxd20072.automationapp.ui.editor.viewmodel.EditorViewModel
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
	val nodeSelectManager = bottomSheetManager.nodeSelectManager
	val nodeEditManager = bottomSheetManager.nodeEditManager
	val variablesManager = bottomSheetManager.variablesManager
	val variableEditManager = bottomSheetManager.variableEditManager
	val dragManager = editorViewModel.dragManager

	val automationState by automationManager.automationUiState.collectAsState(AutomationUiState.Loading)
	val draggingData by dragManager.editorNodeDragState.collectAsState()
	val bottomSheetState by bottomSheetManager.bottomSheetState.collectAsState()
	val nodeSelectState by nodeSelectManager.nodeSelectState.collectAsState()
	val nodeEditState by nodeEditManager.editingNodeState.collectAsState(null)
	val variablesUiState by variablesManager.variablesUiState.collectAsState()
	val filteredVariables by variablesManager.filteredVariables.collectAsState(emptySet())
	val variableEditState by variableEditManager.variableEditState.collectAsState()
	val editingVariableDef by variableEditManager.editingVariableDef.collectAsState(null)

	val draggableState = rememberDraggable2DState { delta ->
		editorViewModel.dragManager.dragNodeDelta(delta)
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
					openVariablesScreen = bottomSheetManager::openVariables,
					openLogsScreen = bottomSheetManager::openLogs,
					saveAutomation = { automationManager.trySaveAutomation(true) },
					runAutomation = automationManager::executeAutomation,
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
			onCreateNodePressed = { bottomSheetManager.openNodeSelect(NodeType.ACTION) },
			onCreateFunctionPressed = { },
			onCreateTriggerPressed = { bottomSheetManager.openNodeSelect(NodeType.TRIGGER) }
		)
		EditorBottomSheet(
			onDismiss = bottomSheetManager::closeBottomSheet,
			bottomSheetState = bottomSheetState,
			nodeSelectState = nodeSelectState,
			nodeEditScreenState = nodeEditState,
			setNodeConfig = nodeEditManager::setConfig,
			resetDefaultNodeConfig = nodeEditManager::resetDefaultConfig,
			onNodeSearch = nodeSelectManager::changeNodeSearchQuery,
			variablesScreenState = variablesUiState,
			filteredVariables = filteredVariables,
			searchVariable = variablesManager::changeSearchQuery,
			editVariable = bottomSheetManager::openVariableEdit,
			deleteVariable = automationManager::deleteVariable,
			createVariable = editorViewModel::onCreateVariable,
			variableEditState = variableEditState,
			editingVariableDef = editingVariableDef,
			validateVariableName = automationManager::validateVariableName,
			changeVariableName = variableEditManager::changeName,
			changeVariableValue = variableEditManager::changeValue,
			editorDragState = draggingData,
			onStartDrag = editorViewModel::onCreateNodeDrag,
			onStopDrag = editorViewModel::onCreateNodeDrop,
			onCreateClick = editorViewModel::onCreateNodeClick,
			draggableState = draggableState,
			onDropZoneEnter = dragManager::enterNodeDropZone,
			onDropZoneExit = dragManager::exitNodeDropZone,
			autoOpenKeyboard = settingsUiState is UserSettingsUiState.Success
				&& (settingsUiState as UserSettingsUiState.Success).settings.autoOpenKeyboard.value,
		)
	}
}