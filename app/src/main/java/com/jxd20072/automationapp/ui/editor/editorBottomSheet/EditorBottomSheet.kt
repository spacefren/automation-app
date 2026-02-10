package com.jxd20072.automationapp.ui.editor.editorBottomSheet

import androidx.compose.animation.SizeTransform
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.EditorBottomSheetScreen
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.EditorBottomSheetState
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.EditorNodeDragState
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.EditorNodeDropData
import com.jxd20072.automationapp.ui.shared.components.ModalBottomSheet
import com.jxd20072.automationapp.ui.shared.components.ModalBottomSheetState
import com.jxd20072.automationapp.ui.shared.navSheetPopPredictiveTransitionSpec
import com.jxd20072.automationapp.ui.shared.navSheetPopTransitionSpec
import com.jxd20072.automationapp.ui.shared.navSheetTransitionSpec
import com.jxd20072.automationapp.ui.shared.sizeAnimSpec

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EditorBottomSheet(
    onDismiss: () -> Unit,
    navigateBack: () -> Unit,
    bottomSheetState: EditorBottomSheetState,
    autoOpenKeyboard: Boolean,
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
            transitionSpec = navSheetTransitionSpec,
            popTransitionSpec = navSheetPopTransitionSpec,
            predictivePopTransitionSpec = navSheetPopPredictiveTransitionSpec,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                entry<EditorBottomSheetScreen.Closed> {
                    Box(modifier = Modifier.fillMaxWidth())
                }
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
                        initialTypeFilters = it.initialTypeFilters,
                        onVariableSelect = it.onVariableSelect
                    )
                }
                entry<EditorBottomSheetScreen.VariableEdit> {
                    VariableEditScreen(
                        variableId = it.variableId,
                        isCreating = it.isCreating
                    )
                }
                entry<EditorBottomSheetScreen.Logs> {
                    EditorLogsScreen()
                }
            }
        )
    }
}