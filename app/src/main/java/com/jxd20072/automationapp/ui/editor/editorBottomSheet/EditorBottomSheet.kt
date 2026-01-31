package com.jxd20072.automationapp.ui.editor.editorBottomSheet

import androidx.compose.animation.SizeTransform
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.jxd20072.automationapp.core.data.model.VariableDef
import com.jxd20072.automationapp.core.data.model.VariableDefValue
import com.jxd20072.automationapp.core.nodes.props.ValidationResult
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.EditorBottomSheetScreen
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.EditorBottomSheetState
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.EditorNodeDragState
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.EditorNodeDropData
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.VariableEditState
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.VariablesScreenState
import com.jxd20072.automationapp.ui.shared.components.ModalBottomSheet
import com.jxd20072.automationapp.ui.shared.components.ModalBottomSheetState
import com.jxd20072.automationapp.ui.shared.navPopPredictiveTransitionSpec
import com.jxd20072.automationapp.ui.shared.navPopTransitionSpec
import com.jxd20072.automationapp.ui.shared.navTransitionSpec
import com.jxd20072.automationapp.ui.shared.sizeAnimSpec

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EditorBottomSheet(
    onDismiss: () -> Unit,
    navigateBack: () -> Unit,
    bottomSheetState: EditorBottomSheetState,
    autoOpenKeyboard: Boolean,
    // General
    // Node select screen
    // Node edit screen
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
    // Dragging
    editorDragState: EditorNodeDragState,
    onDropZoneEnter: (dropData: EditorNodeDropData) -> Unit,
    onDropZoneExit: () -> Unit,
) {
    val state = remember { ModalBottomSheetState() }
    val isCollapsed = bottomSheetState.backStack.last() != EditorBottomSheetScreen.Closed
            && editorDragState is EditorNodeDragState.Dragging

    state.isOpen = bottomSheetState.backStack.last() != EditorBottomSheetScreen.Closed
    state.isCollapsed = isCollapsed

    ModalBottomSheet(
        state = state,
        onDismissRequest = {
            onDismiss()
            true
        },
    ) {
        NavDisplay(
            sizeTransform = SizeTransform(sizeAnimationSpec = { _, _ -> sizeAnimSpec }),
            backStack = bottomSheetState.backStack,
            onBack = navigateBack,
            transitionSpec = navTransitionSpec,
            popTransitionSpec = navPopTransitionSpec,
            predictivePopTransitionSpec = navPopPredictiveTransitionSpec,
            entryProvider = entryProvider {
                entry<EditorBottomSheetScreen.Closed> { }
                entry<EditorBottomSheetScreen.NodeDrop> {
                    EditorNodeDropSheet(
                        dragData = editorDragState,
                        showDelete = isCollapsed && !editorDragState.isCreatingNode,
                        onDropZoneEnter = onDropZoneEnter,
                        onDropZoneExit = onDropZoneExit
                    )
                }
                entry<EditorBottomSheetScreen.NodeSelect> {
                    EditorNodeSelectScreen(
                        screenType = it.screenType,
                        autoOpenKeyboard = autoOpenKeyboard,
                    )
                }
                entry<EditorBottomSheetScreen.NodeEdit> {
                    EditorNodeEditScreen(
                        nodeId = it.nodeID
                    )
                }
                entry<EditorBottomSheetScreen.Variables> {
                    EditorVariablesScreen(
                        variablesScreenState = variablesScreenState,
                        filteredVariables = filteredVariables,
                        searchVariable = searchVariable,
                        onVariableClick = onVariableClick,
                        deleteVariable = deleteVariable,
                        createVariable = createVariable
                    )
                }
                entry<EditorBottomSheetScreen.VariableEdit> {
                    VariableEditScreen(
                        editingVariable = editingVariableDef,
                        validateVariableName = validateVariableName,
                        changeVariableName = changeVariableName,
                        changeVariableValue = changeVariableValue,
                        variableEditState = variableEditState
                    )
                }
                entry<EditorBottomSheetScreen.Logs> {
                    EditorLogsScreen()
                }
            }
        )
    }
}