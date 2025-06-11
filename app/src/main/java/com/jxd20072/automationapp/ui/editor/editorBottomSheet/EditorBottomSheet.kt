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
import com.jxd20072.automationapp.core.data.model.ActionNodeDef
import com.jxd20072.automationapp.core.data.model.ConfigurationEntry
import com.jxd20072.automationapp.core.nodes.NodeData
import com.jxd20072.automationapp.ui.editor.viewmodel.EditorNodeDragState
import com.jxd20072.automationapp.ui.editor.viewmodel.EditorNodeDropData
import com.jxd20072.automationapp.ui.editor.viewmodel.bottomSheet.EditorBottomSheetScreen
import com.jxd20072.automationapp.ui.editor.viewmodel.bottomSheet.EditorBottomSheetState
import com.jxd20072.automationapp.ui.editor.viewmodel.bottomSheet.NodeEditState
import com.jxd20072.automationapp.ui.editor.viewmodel.bottomSheet.NodeSelectState
import com.jxd20072.automationapp.ui.shared.Layer
import com.jxd20072.automationapp.ui.shared.ModalBottomSheet
import com.jxd20072.automationapp.ui.shared.ModalBottomSheetState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EditorBottomSheet(
	onDismiss: () -> Unit,
	onNodeSearch: (String) -> Unit,
	bottomSheetState: EditorBottomSheetState,
	nodeSelectState: NodeSelectState,
	nodeEditScreenState: NodeEditState,
	editingNode: ActionNodeDef?,
	editingNodeData: NodeData?,
	setNodeConfig: (name: String, config: ConfigurationEntry<*>) -> Unit,
	resetDefaultNodeConfig: () -> Unit,
	dragData: EditorNodeDragState,
	draggableState: Draggable2DState,
	onDropZoneEnter: (dropData: EditorNodeDropData) -> Unit,
	onDropZoneExit: () -> Unit,
	onStartDrag: (
		nodeData: NodeData,
		startedPosition: Offset,
		startedOffset: Offset,
	) -> Unit,
	onStopDrag: () -> Unit,
	onClick: (nodeData: NodeData) -> Unit,
	autoOpenKeyboard: Boolean,
) {
	val state = remember { ModalBottomSheetState() }
	val isCollapsed = bottomSheetState is EditorBottomSheetState.Open
		&& dragData is EditorNodeDragState.Dragging

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
					dragData = dragData,
					showDelete = !dragData.isCreatingNode,
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
							nodeSearchQuery = nodeSelectState.nodeSearchQuery,
							autoOpenKeyboard = autoOpenKeyboard,
							onStartDrag = onStartDrag,
							onStopDrag = onStopDrag,
							isCollapsed = isCollapsed,
							onClick = onClick,
							draggableState = draggableState,
							nodes = nodeSelectState.filteredNodes
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

					else -> {}
				}
			}
		}
	}
}