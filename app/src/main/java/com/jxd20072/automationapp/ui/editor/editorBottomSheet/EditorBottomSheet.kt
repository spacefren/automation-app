package com.jxd20072.automationapp.ui.editor.editorBottomSheet

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Draggable2DState
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import com.jxd20072.automationapp.core.nodes.actions.ActionNodeData
import com.jxd20072.automationapp.ui.editor.EditorBottomSheetCollapsedScreen
import com.jxd20072.automationapp.ui.editor.EditorBottomSheetScreen
import com.jxd20072.automationapp.ui.editor.EditorBottomSheetState
import com.jxd20072.automationapp.ui.editor.EditorNodeDragData
import com.jxd20072.automationapp.ui.editor.EditorNodeDropData
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
	var lastScreen by remember { mutableStateOf<EditorBottomSheetScreen>(EditorBottomSheetScreen.Empty) }
	val state = remember { ModalBottomSheetState() }
	state.isOpen = bottomSheetState !is EditorBottomSheetState.Closed
	state.isCollapsed = bottomSheetState is EditorBottomSheetState.Open
		&& bottomSheetState.collapsedScreen != EditorBottomSheetCollapsedScreen.Empty

	if (bottomSheetState is EditorBottomSheetState.Open) {
		lastScreen = bottomSheetState.screen
	}

	ModalBottomSheet(
		state = state,
		onDismissRequest = {
			onDismiss()
			true
		},
		collapsedContent = {
			EditorBottomSheetCollapsedContent(
				dragData = dragData,
				onDropZoneEnter = onDropZoneEnter,
				onDropZoneExit = onDropZoneExit
			)
		}
	) {
		Layer {
			AnimatedContent(
				targetState = lastScreen,
				contentKey = { it::class }
			) { state ->
				when (state) {
					is EditorBottomSheetScreen.NodeSelect -> {
						EditorNodeSelectScreen(
							onNodeSearch = onNodeSearch,
							nodeSearchQuery = state.nodeSearchQuery,
							autoOpenKeyboard = autoOpenKeyboard,
							onStartDrag = onStartDrag,
							onStopDrag = onStopDrag,
							onClick = onClick,
							draggableState = draggableState,
							nodes = state.filteredNodes
						)
					}

					is EditorBottomSheetScreen.NodeEdit -> {
						Text("Edit node", fontSize = 20.sp, color = LocalTheme.colorScheme.onBackground)
						Spacer(modifier = Modifier.height(200.dp))
					}

					else -> {}
				}
			}
		}
	}
}