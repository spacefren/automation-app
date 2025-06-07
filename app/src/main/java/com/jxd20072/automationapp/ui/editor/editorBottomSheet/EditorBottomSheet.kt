package com.jxd20072.automationapp.ui.editor.editorBottomSheet

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Draggable2DState
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import com.jxd20072.automationapp.core.nodes.actions.ActionNodeData
import com.jxd20072.automationapp.ui.editor.viewmodel.EditorNodeDragState
import com.jxd20072.automationapp.ui.editor.viewmodel.EditorNodeDropData
import com.jxd20072.automationapp.ui.editor.viewmodel.bottomSheet.EditorBottomSheetCollapsedState
import com.jxd20072.automationapp.ui.editor.viewmodel.bottomSheet.EditorBottomSheetScreen
import com.jxd20072.automationapp.ui.editor.viewmodel.bottomSheet.EditorBottomSheetState
import com.jxd20072.automationapp.ui.editor.viewmodel.bottomSheet.NodeEditState
import com.jxd20072.automationapp.ui.editor.viewmodel.bottomSheet.NodeSelectState
import com.jxd20072.automationapp.ui.shared.Layer
import com.jxd20072.automationapp.ui.shared.ModalBottomSheet
import com.jxd20072.automationapp.ui.shared.ModalBottomSheetState
import com.jxd20072.automationapp.ui.theme.LocalTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EditorBottomSheet(
	onDismiss: () -> Unit,
	onNodeSearch: (String) -> Unit,
	bottomSheetState: EditorBottomSheetState,
	collapsedSheetState: EditorBottomSheetCollapsedState,
	nodeSelectState: NodeSelectState,
	nodeEditScreenState: NodeEditState,
	dragData: EditorNodeDragState,
	draggableState: Draggable2DState,
	onDropZoneEnter: (dropData: EditorNodeDropData) -> Unit,
	onDropZoneExit: () -> Unit,
	onStartDrag: (
		nodeData: ActionNodeData,
		startedPosition: Offset,
		startedOffset: Offset,
	) -> Unit,
	onStopDrag: () -> Unit,
	onClick: (nodeData: ActionNodeData) -> Unit,
	autoOpenKeyboard: Boolean,
) {
	val state = remember { ModalBottomSheetState() }
	state.isOpen = bottomSheetState !is EditorBottomSheetState.Closed
	state.isCollapsed = bottomSheetState is EditorBottomSheetState.Open
		&& collapsedSheetState != EditorBottomSheetCollapsedState.Empty

	ModalBottomSheet(
		state = state,
		onDismissRequest = {
			onDismiss()
			true
		},
		collapsedContent = {
			EditorBottomSheetCollapsedContent(
				dragData = dragData,
				showDelete = bottomSheetState is EditorBottomSheetState.Open
					&& collapsedSheetState is EditorBottomSheetCollapsedState.DropPanels
					&& collapsedSheetState.showDelete,
				onDropZoneEnter = onDropZoneEnter,
				onDropZoneExit = onDropZoneExit
			)
		}
	) {
		Layer {
			AnimatedContent(
				targetState = bottomSheetState,
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
							onClick = onClick,
							draggableState = draggableState,
							nodes = nodeSelectState.filteredNodes
						)
					}

					state.screen == EditorBottomSheetScreen.NODE_EDIT -> {
						Text("Edit node", fontSize = 20.sp, color = LocalTheme.colorScheme.onBackground)
						Spacer(modifier = Modifier.height(200.dp))
					}

					else -> {}
				}
			}
		}
	}
}