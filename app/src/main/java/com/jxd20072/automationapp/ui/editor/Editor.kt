package com.jxd20072.automationapp.ui.editor

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.jxd20072.automationapp.ui.editor.components.DraggingNode
import com.jxd20072.automationapp.ui.editor.components.EditorFloatingAddButton
import com.jxd20072.automationapp.ui.editor.components.EditorNodeTree
import com.jxd20072.automationapp.ui.editor.editorBottomSheet.EditorBottomSheet
import com.jxd20072.automationapp.ui.editor.viewmodel.EditorViewModel
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.AutomationUiState
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.EditorBottomSheetScreen
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.EditorNodeDragState
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.NodeEditState
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.NodeSelectScreenType
import com.jxd20072.automationapp.ui.settings.SettingsViewModel
import com.jxd20072.automationapp.ui.settings.UserSettingsUiState
import com.jxd20072.automationapp.ui.shared.components.AppScreen

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AutomationEditor(
    editorViewModel: EditorViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    returnHome: (saveChanges: Boolean) -> Unit
) {
    val currentTime by editorViewModel.currentTime.collectAsState()
    val settingsUiState by settingsViewModel.userSettingsUiState.collectAsState()

    val automationManager = editorViewModel.automationManager
    val bottomSheetManager = editorViewModel.bottomSheetManager
    val nodeEditManager = bottomSheetManager.nodeEditManager
    val variablesManager = bottomSheetManager.variablesManager
    val variableEditManager = bottomSheetManager.variableEditManager
    val logsManager = bottomSheetManager.logsManager
    val dragManager = editorViewModel.dragManager

    val automationState by automationManager.automationUiState.collectAsState(AutomationUiState.Loading)
    val draggingData by dragManager.editorNodeDragState.collectAsState()
    val bottomSheetState by bottomSheetManager.bottomSheetState.collectAsState()
    val nodeEditState by nodeEditManager.editingNodeState.collectAsState(NodeEditState.NotEditing)
    val variablesUiState by variablesManager.variablesUiState.collectAsState()
    val filteredVariables by variablesManager.filteredVariables.collectAsState(emptySet())
    val variableEditState by variableEditManager.variableEditState.collectAsState()
    val editingVariableDef by variableEditManager.editingVariableDef.collectAsState(null)
    val logScreenState by logsManager.logsScreenState.collectAsState()
    val filteredLogs by logsManager.filteredLogs.collectAsState(emptyList())

    AppScreen {
        if (automationState is AutomationUiState.Success) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                awaitPointerEvent(PointerEventPass.Initial).changes.forEach { change ->
                                    if (draggingData !is EditorNodeDragState.Dragging) return@forEach

                                    val delta = change.position - change.previousPosition

                                    if (delta.x != 0f || delta.y != 0f) {
                                        dragManager.dragNodeDelta(delta)
                                    }

                                    if (!change.pressed && change.previousPressed) {
                                        editorViewModel.onNodeDrop()
                                    }
                                }
                            }
                        }
                    }
            ) {
                Column {
                    EditorTopBar(
                        automationData = (automationState as AutomationUiState.Success).automationData,
                        currentTime = currentTime,
                        returnHome = returnHome,
                        openVariablesScreen = bottomSheetManager::navigateVariables,
                        openLogsScreen = bottomSheetManager::openLogs,
                        saveAutomation = { automationManager.trySaveAutomation(true) },
                        runAutomation = editorViewModel::runAutomation,
                    )
                    EditorNodeTree(
                        automationDraft = (automationState as AutomationUiState.Success).automationDraft,
                        onNodeDrag = editorViewModel::onExistingNodeDrag,
                        draggingData = draggingData,
                        onDropLineEnter = dragManager::enterDropLine,
                        onDropLineExit = dragManager::exitDropLine,
                        editNode = editorViewModel::startEditNode
                    )
                }
                DraggingNode(
                    draggingData = draggingData
                )
                EditorFloatingAddButton(
                    hidden = draggingData is EditorNodeDragState.Dragging,
                    onCreateNodePressed = {
                        bottomSheetManager.navigateTo(
                            EditorBottomSheetScreen.NodeSelect(NodeSelectScreenType.ACTION)
                        )
                    },
                    onCreateFunctionPressed = { },
                    onCreateTriggerPressed = {
                        bottomSheetManager.navigateNodeSelect(NodeSelectScreenType.TRIGGER)
                    }
                )
                EditorBottomSheet(
                    onDismiss = bottomSheetManager::closeBottomSheet,
                    navigateBack = bottomSheetManager::navigateBack,
                    bottomSheetState = bottomSheetState,
                    automationDraft = (automationState as AutomationUiState.Success).automationDraft,
                    nodeEditScreenState = nodeEditState,
                    setNodeConfig = nodeEditManager::setNodeConfig,
                    resetDefaultNodeConfig = nodeEditManager::resetDefaultConfig,
                    variablesScreenState = variablesUiState,
                    filteredVariables = filteredVariables,
                    searchVariable = variablesManager::changeSearchQuery,
                    onVariableClick = bottomSheetManager::onVariableClick,
                    deleteVariable = automationManager::deleteVariable,
                    createVariable = editorViewModel::onCreateVariable,
                    variableEditState = variableEditState,
                    editingVariableDef = editingVariableDef,
                    validateVariableName = automationManager::validateVariableName,
                    changeVariableName = variableEditManager::changeName,
                    changeVariableValue = variableEditManager::changeValue,
                    promptSelectVariable = editorViewModel::promptSelectVariable,
                    logsScreenState = logScreenState,
                    filteredLogs = filteredLogs,
                    searchLogs = logsManager::searchLogs,
                    filterLogs = logsManager::filterLogs,
                    deleteLog = automationManager::deleteLog,
                    editorDragState = draggingData,
                    onDropZoneEnter = dragManager::enterNodeDropZone,
                    onDropZoneExit = dragManager::exitNodeDropZone,
                    autoOpenKeyboard = settingsUiState is UserSettingsUiState.Success
                            && (settingsUiState as UserSettingsUiState.Success).settings.autoOpenKeyboard.value,
                )
            }
        }
    }
}