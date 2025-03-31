package com.jxd20072.automationapp.ui.editor.editorBottomSheet

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Draggable2DState
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import com.jxd20072.automationapp.core.nodes.actions.ActionNodeData
import com.jxd20072.automationapp.ui.editor.EditorBottomSheetState
import com.jxd20072.automationapp.ui.shared.Layer
import com.jxd20072.automationapp.ui.shared.ModalBottomSheet
import com.jxd20072.automationapp.ui.shared.ModalBottomSheetState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EditorBottomSheet(
	onDismiss: () -> Unit,
	onNodeSearch: (String) -> Unit,
	bottomSheetState: EditorBottomSheetState,
	isDragging: Boolean,
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

	Log.d("APP_A", "collapsed? ${state.isCollapsed} open? ${state.isOpen}");
	Log.d("APP_A", "${bottomSheetState}");

	ModalBottomSheet(
		state = state,
		onDismissRequest = {
			onDismiss()
			true
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
							draggableState = draggableState,
							nodes = state.filteredNodes
						)
					}

					else -> {
						Spacer(modifier = Modifier.fillMaxHeight())
					}
				}
			}
		}
	}
}