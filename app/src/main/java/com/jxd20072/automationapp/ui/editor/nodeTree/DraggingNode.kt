package com.jxd20072.automationapp.ui.editor.nodeTree

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.core.nodes.actionNodes
import com.jxd20072.automationapp.ui.editor.EditorDraggableScope
import com.jxd20072.automationapp.ui.editor.EditorDraggingData

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DraggingNode(
	draggingData: EditorDraggingData,
	modifier: Modifier = Modifier
) {
	if (draggingData is EditorDraggingData.Dragging) {
		Box(
			modifier = modifier
				.zIndex(40f)
				.graphicsLayer {
					translationX = draggingData.inputPosition.x
					translationY = draggingData.inputPosition.y
				}
		) {
			NodeContainer(
				level = when (draggingData.scope) {
					EditorDraggableScope.FUNCTION, EditorDraggableScope.TRIGGER -> 1
					EditorDraggableScope.ACTION -> 2
				},
				enabledByDragging = true,
				onPositioned = { },
			) {
				NodeHeaderIcons(
					icon = when (draggingData.scope) {
						EditorDraggableScope.FUNCTION -> R.drawable.round_functions_24
						EditorDraggableScope.TRIGGER -> R.drawable.round_electric_bolt_24
						EditorDraggableScope.ACTION ->
							actionNodes[draggingData.draggingAction?.name]?.data?.icon
								?: R.drawable.round_play_arrow_24
					},
					forceShowDragHandle = true
				)
			}
		}
	}
}