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
import com.jxd20072.automationapp.ui.editor.EditorBottomSheetState
import com.jxd20072.automationapp.ui.editor.EditorNodeDragData
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
	dragData: EditorNodeDragData,
	draggableState: Draggable2DState,
	onStartDrag: (
		nodeData: ActionNodeData,
		startedPosition: Offset,
		startedOffset: Offset,
	) -> Unit,
	onStopDrag: () -> Unit,
	autoOpenKeyboard: Boolean,
) {
	val state = remember { ModalBottomSheetState() }
	state.isOpen = bottomSheetState !is EditorBottomSheetState.Closed
	state.isCollapsed = bottomSheetState is EditorBottomSheetState.NodeSelect
		&& bottomSheetState.isCollapsed

	ModalBottomSheet(
		state = state,
		onDismissRequest = {
			onDismiss()
			true
		},
		collapsedContent = {
			EditorBottomSheetCollapsedContent(
				dragData = dragData
			)
		}
	) {
		Layer {
			AnimatedContent(
				targetState = bottomSheetState,
				contentKey = { it::class },
			) { state ->
				when (state) {
					is EditorBottomSheetState.NodeSelect -> {
						EditorNodeSelectScreen(
							onNodeSearch = onNodeSearch,
							nodeSearchQuery = state.nodeSearchQuery,
							autoOpenKeyboard = autoOpenKeyboard,
							onStartDrag = onStartDrag,
							onStopDrag = onStopDrag,
							isDragging = dragData is EditorNodeDragData.Dragging,
							draggableState = draggableState,
							nodes = state.filteredNodes
						)
					}

					else -> {
						Text("Edit node", fontSize = 20.sp, color = LocalTheme.colorScheme.onBackground)
						Spacer(modifier = Modifier.height(200.dp))
					}
				}
			}
		}
	}
}