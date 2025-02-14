package com.jxd20072.automationapp.ui.editor.nodeTree

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.core.nodes.actionNodes
import com.jxd20072.automationapp.ui.editor.EditorDraggableScope
import com.jxd20072.automationapp.ui.editor.EditorDraggingData
import kotlin.math.abs

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
					translationX = draggingData.nodePosition.x
					translationY = draggingData.nodePosition.y
				}
		) {
			NodeContainer(
				level = when (draggingData.scope) {
					EditorDraggableScope.FUNCTION, EditorDraggableScope.TRIGGER -> 1
					EditorDraggableScope.ACTION -> 2
				},
				enabledByDragging = true,
				onLayout = { _ -> },
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

@Composable
fun ActionNodeDropTarget(
	parentId: String,
	draggingData: EditorDraggingData.Dragging
) {
	val checkHovering = { position: Offset ->
		val pos = draggingData.nodePosition.y
		val offset = draggingData.inputOffset.y
		abs(pos + offset - position.y) < 30
	}

	var positionInRoot by remember { mutableStateOf(Offset(0f, 0f)) }
	val shouldShow = checkHovering(positionInRoot)

	val height by animateDpAsState(if (shouldShow) 4.dp else 0.dp)
	Box(
		modifier = Modifier
			.height(height)
			.width(300.dp)
			.background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(10.dp))
			.onGloballyPositioned { layout -> positionInRoot = layout.positionInRoot() }
	) {

	}
}
