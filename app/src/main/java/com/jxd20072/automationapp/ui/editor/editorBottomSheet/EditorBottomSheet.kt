package com.jxd20072.automationapp.ui.editor.editorBottomSheet

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import com.jxd20072.automationapp.core.data.AutomationLogEntry
import com.jxd20072.automationapp.core.data.LogLevel
import com.jxd20072.automationapp.core.data.model.ConfigurationValue
import com.jxd20072.automationapp.core.data.model.VariableDef
import com.jxd20072.automationapp.core.data.model.VariableDefValue
import com.jxd20072.automationapp.core.nodes.NodeData
import com.jxd20072.automationapp.core.nodes.props.ValidationResult
import com.jxd20072.automationapp.ui.editor.viewmodel.EditorNodeDragState
import com.jxd20072.automationapp.ui.editor.viewmodel.EditorNodeDropData
import com.jxd20072.automationapp.ui.editor.viewmodel.bottomSheet.EditorBottomSheetScreen
import com.jxd20072.automationapp.ui.editor.viewmodel.bottomSheet.EditorBottomSheetState
import com.jxd20072.automationapp.ui.editor.viewmodel.bottomSheet.LogsScreenState
import com.jxd20072.automationapp.ui.editor.viewmodel.bottomSheet.NodeEditState
import com.jxd20072.automationapp.ui.editor.viewmodel.bottomSheet.NodeSelectState
import com.jxd20072.automationapp.ui.editor.viewmodel.bottomSheet.VariableEditState
import com.jxd20072.automationapp.ui.editor.viewmodel.bottomSheet.VariablesScreenState
import com.jxd20072.automationapp.ui.shared.Layer
import com.jxd20072.automationapp.ui.shared.ModalBottomSheet
import com.jxd20072.automationapp.ui.shared.ModalBottomSheetState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EditorBottomSheet(
    onDismiss: () -> Unit,
    bottomSheetState: EditorBottomSheetState,
    autoOpenKeyboard: Boolean,
    // Node select screen
    onNodeSearch: (String) -> Unit,
    nodeSelectState: NodeSelectState,
    onCreateClick: (nodeData: NodeData) -> Unit,
    // Node edit screen
    nodeEditScreenState: NodeEditState?,
    setNodeConfig: (name: String, config: ConfigurationValue<*>) -> Unit,
    resetDefaultNodeConfig: () -> Unit,
    // Variables screen
    variablesScreenState: VariablesScreenState,
    filteredVariables: Set<VariableDef>,
    searchVariable: (query: String) -> Unit,
    editVariable: (name: String) -> Unit,
    deleteVariable: (name: String) -> Unit,
    createVariable: () -> Unit,
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
    val isCollapsed = bottomSheetState is EditorBottomSheetState.Open
            && editorDragState is EditorNodeDragState.Dragging

    state.isOpen = bottomSheetState !is EditorBottomSheetState.Closed
    state.isCollapsed = isCollapsed

    ModalBottomSheet(
        state = state,
        onDismissRequest = {
            onDismiss()
            true
        },
    ) {
        Layer {
            AnimatedContent(
                targetState = bottomSheetState,
                transitionSpec = {
                    ContentTransform(
                        targetContentEnter = fadeIn(),
                        initialContentExit = fadeOut(),
                    )
                },
                contentKey = { it::class }
            ) { state ->
                when {
                    state !is EditorBottomSheetState.Open -> {}

                    state.screen == EditorBottomSheetScreen.NODE_DROP -> {
                        EditorNodeDropSheet(
                            dragData = editorDragState,
                            showDelete = isCollapsed && !editorDragState.isCreatingNode,
                            onDropZoneEnter = onDropZoneEnter,
                            onDropZoneExit = onDropZoneExit
                        )
                    }

                    state.screen == EditorBottomSheetScreen.NODE_SELECT -> {
                        EditorNodeSelectScreen(
                            onNodeSearch = onNodeSearch,
                            nodeSelectState = nodeSelectState,
                            autoOpenKeyboard = autoOpenKeyboard,
                            onStartDrag = onStartDrag,
                            onClick = onCreateClick,
                        )
                    }

                    state.screen == EditorBottomSheetScreen.NODE_EDIT -> {
                        EditorNodeEditScreen(
                            nodeEditState = nodeEditScreenState,
                            setNodeConfig = setNodeConfig,
                            resetDefaultConfig = resetDefaultNodeConfig,
                        )
                    }

                    state.screen == EditorBottomSheetScreen.VARIABLES -> {
                        EditorVariablesScreen(
                            variablesScreenState = variablesScreenState,
                            filteredVariables = filteredVariables,
                            searchVariable = searchVariable,
                            editVariable = editVariable,
                            deleteVariable = deleteVariable,
                            createVariable = createVariable
                        )
                    }

                    state.screen == EditorBottomSheetScreen.VARIABLE_EDIT -> {
                        VariableEditScreen(
                            editingVariable = editingVariableDef,
                            validateVariableName = validateVariableName,
                            changeVariableName = changeVariableName,
                            changeVariableValue = changeVariableValue,
                            variableEditState = variableEditState
                        )
                    }

                    state.screen == EditorBottomSheetScreen.LOGS -> {
                        EditorLogsScreen(
                            logsScreenState = logsScreenState,
                            filteredLogs = filteredLogs,
                            searchLogs = searchLogs,
                            filterLogs = filterLogs,
                            deleteLog = deleteLog,
                        )
                    }

                    state.screen == EditorBottomSheetScreen.EMPTY -> {}
                }
            }
        }
    }
}