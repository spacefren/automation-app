package com.jxd20072.automationapp.ui.editor.editorBottomSheet

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Draggable2DState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import com.jxd20072.automationapp.core.data.model.ConfigurationValue
import com.jxd20072.automationapp.core.data.model.NodeDef
import com.jxd20072.automationapp.core.nodes.NodeData
import com.jxd20072.automationapp.ui.editor.viewmodel.EditorNodeDragState
import com.jxd20072.automationapp.ui.editor.viewmodel.EditorNodeDropData
import com.jxd20072.automationapp.ui.editor.viewmodel.bottomSheet.EditorBottomSheetScreen
import com.jxd20072.automationapp.ui.editor.viewmodel.bottomSheet.EditorBottomSheetState
import com.jxd20072.automationapp.ui.editor.viewmodel.bottomSheet.NodeEditState
import com.jxd20072.automationapp.ui.editor.viewmodel.bottomSheet.NodeSelectState
import com.jxd20072.automationapp.ui.editor.viewmodel.bottomSheet.VariableEditScreenState
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
	nodeEditScreenState: NodeEditState,
	editingNode: NodeDef?,
	editingNodeData: NodeData?,
	setNodeConfig: (name: String, config: ConfigurationValue<*>) -> Unit,
	resetDefaultNodeConfig: () -> Unit,
	// Variables screen
	variablesScreenState: VariablesScreenState,
	searchVariable: (query: String) -> Unit,
	editVariable: (name: String) -> Unit,
	deleteVariable: (name: String) -> Unit,
	// Variable edit
	variableEditScreenState: VariableEditScreenState,
	// Dragging
	editorDragState: EditorNodeDragState,
	draggableState: Draggable2DState,
	onDropZoneEnter: (dropData: EditorNodeDropData) -> Unit,
	onDropZoneExit: () -> Unit,
	onStartDrag: (
		nodeData: NodeData,
		startedPosition: Offset,
		startedOffset: Offset,
	) -> Unit,
	onStopDrag: () -> Unit,
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
			if (isCollapsed) {
				EditorBottomSheetCollapsedContent(
					dragData = editorDragState,
					showDelete = !editorDragState.isCreatingNode,
					onDropZoneEnter = onDropZoneEnter,
					onDropZoneExit = onDropZoneExit
				)
			}

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

					state.screen == EditorBottomSheetScreen.NODE_SELECT -> {
						EditorNodeSelectScreen(
							onNodeSearch = onNodeSearch,
							nodeSelectState = nodeSelectState,
							autoOpenKeyboard = autoOpenKeyboard,
							onStartDrag = onStartDrag,
							onStopDrag = onStopDrag,
							isCollapsed = isCollapsed,
							onClick = onCreateClick,
							draggableState = draggableState,
						)
					}

					state.screen == EditorBottomSheetScreen.NODE_EDIT -> {
						EditorNodeEditScreen(
							nodeEditState = nodeEditScreenState,
							editingNode = editingNode,
							editingNodeData = editingNodeData,
							setNodeConfig = setNodeConfig,
							resetDefaultConfig = resetDefaultNodeConfig,
						)
					}

					state.screen == EditorBottomSheetScreen.VARIABLES -> {
						EditorVariablesScreen(
							variablesScreenState = variablesScreenState,
							searchVariable = searchVariable,
							editVariable = editVariable,
							deleteVariable = deleteVariable
						)
					}

					state.screen == EditorBottomSheetScreen.VARIABLE_EDIT -> {

					}

					state.screen == EditorBottomSheetScreen.LOGS -> {
						EditorLogsScreen()
					}

					state.screen == EditorBottomSheetScreen.EMPTY -> {}
				}
			}
		}
	}
}