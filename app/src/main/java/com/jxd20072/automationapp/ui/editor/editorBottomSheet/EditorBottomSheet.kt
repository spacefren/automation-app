package com.jxd20072.automationapp.ui.editor.editorBottomSheet

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.jxd20072.automationapp.core.data.AutomationLogEntry
import com.jxd20072.automationapp.core.data.LogLevel
import com.jxd20072.automationapp.core.data.model.AutomationDef
import com.jxd20072.automationapp.core.data.model.ConfigurationValue
import com.jxd20072.automationapp.core.data.model.VariableDef
import com.jxd20072.automationapp.core.data.model.VariableDefValue
import com.jxd20072.automationapp.core.nodes.NodeData
import com.jxd20072.automationapp.core.nodes.props.ValidationResult
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.Closed
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.EditorBottomSheetState
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.EditorNodeDragState
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.EditorNodeDropData
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.Logs
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.LogsScreenState
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.NodeDrop
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.NodeEdit
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.NodeEditState
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.NodeSelect
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.VariableEdit
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.VariableEditState
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.Variables
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.VariablesScreenState
import com.jxd20072.automationapp.ui.shared.components.ModalBottomSheet
import com.jxd20072.automationapp.ui.shared.components.ModalBottomSheetState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EditorBottomSheet(
    onDismiss: () -> Unit,
    navigateBack: () -> Unit,
    bottomSheetState: EditorBottomSheetState,
    autoOpenKeyboard: Boolean,
    // General
    automationDraft: AutomationDef,
    // Node select screen
    onCreateClick: (nodeData: NodeData) -> Unit,
    // Node edit screen
    nodeEditScreenState: NodeEditState,
    setNodeConfig: (name: String, config: ConfigurationValue<*>) -> Unit,
    resetDefaultNodeConfig: () -> Unit,
    // Variables screen
    variablesScreenState: VariablesScreenState,
    filteredVariables: Set<VariableDef>,
    searchVariable: (query: String) -> Unit,
    onVariableClick: (name: String) -> Unit,
    deleteVariable: (name: String) -> Unit,
    createVariable: () -> Unit,
    promptSelectVariable: (onSelect: (variableId: String) -> Unit) -> Unit,
    // Variable edit
    variableEditState: VariableEditState,
    editingVariableDef: VariableDef?,
    validateVariableName: (name: String) -> ValidationResult,
    changeVariableName: (newName: String) -> Unit,
    changeVariableValue: (newValue: VariableDefValue<*>) -> Unit,
    // Logs
    logsScreenState: LogsScreenState,
    filteredLogs: List<AutomationLogEntry>,
    searchLogs: (query: String) -> Unit,
    filterLogs: (level: LogLevel) -> Unit,
    deleteLog: (logID: String) -> Unit,
    // Dragging
    editorDragState: EditorNodeDragState,
    onDropZoneEnter: (dropData: EditorNodeDropData) -> Unit,
    onDropZoneExit: () -> Unit,
    onStartDrag: (
        nodeData: NodeData,
        startedPosition: Offset,
        startedOffset: Offset,
    ) -> Unit,
) {
    val state = remember { ModalBottomSheetState() }
    val isCollapsed = bottomSheetState.backStack.last() != Closed
            && editorDragState is EditorNodeDragState.Dragging

    state.isOpen = bottomSheetState.backStack.last() != Closed
    state.isCollapsed = isCollapsed

    ModalBottomSheet(
        state = state,
        onDismissRequest = {
            onDismiss()
            true
        },
    ) {
        NavDisplay(
            backStack = bottomSheetState.backStack,
            onBack = navigateBack,
            entryProvider = entryProvider {
                entry<Closed> { }
                entry<NodeDrop> {
                    EditorNodeDropSheet(
                        dragData = editorDragState,
                        showDelete = isCollapsed && !editorDragState.isCreatingNode,
                        onDropZoneEnter = onDropZoneEnter,
                        onDropZoneExit = onDropZoneExit
                    )
                }
                entry<NodeSelect> {
                    EditorNodeSelectScreen(
                        screenType = it.screenType,
                        autoOpenKeyboard = autoOpenKeyboard,
                        onStartDrag = onStartDrag,
                        onClick = onCreateClick,
                    )
                }
                entry<NodeEdit> {
                    EditorNodeEditScreen(
                        nodeEditState = nodeEditScreenState,
                        automationDraft = automationDraft,
                        setNodeConfig = setNodeConfig,
                        resetDefaultConfig = resetDefaultNodeConfig,
                        promptVariableSelect = promptSelectVariable
                    )
                }
                entry<Variables> {
                    EditorVariablesScreen(
                        variablesScreenState = variablesScreenState,
                        filteredVariables = filteredVariables,
                        searchVariable = searchVariable,
                        onVariableClick = onVariableClick,
                        deleteVariable = deleteVariable,
                        createVariable = createVariable
                    )
                }
                entry<VariableEdit> {
                    VariableEditScreen(
                        editingVariable = editingVariableDef,
                        validateVariableName = validateVariableName,
                        changeVariableName = changeVariableName,
                        changeVariableValue = changeVariableValue,
                        variableEditState = variableEditState
                    )
                }
                entry<Logs> {
                    EditorLogsScreen(
                        logsScreenState = logsScreenState,
                        filteredLogs = filteredLogs,
                        searchLogs = searchLogs,
                        filterLogs = filterLogs,
                        deleteLog = deleteLog,
                    )
                }
            }
        )
    }
}