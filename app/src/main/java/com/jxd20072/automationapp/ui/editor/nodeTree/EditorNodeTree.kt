package com.jxd20072.automationapp.ui.editor.nodeTree

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Draggable2DState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.jxd20072.automationapp.core.data.repository.automations.AutomationDraft
import com.jxd20072.automationapp.ui.editor.EditorDraggableScope
import com.jxd20072.automationapp.ui.editor.EditorNodeDragData

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EditorNodeTree(
	automationDraft: AutomationDraft,
	draggableState: Draggable2DState,
	draggingData: EditorNodeDragData,
	onNodeDrag: (
		id: String,
		startedPosition: Offset,
		startedOffset: Offset,
		scope: EditorDraggableScope
	) -> Unit,
	onStopDrag: () -> Unit,
	onDropZoneEnter: (targetIndex: Int, parentId: String?) -> Unit,
	onDropZoneExit: () -> Unit,
) {
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = Modifier
			.padding(horizontal = 20.dp)
			.verticalScroll(rememberScrollState(0))
	) {
		Spacer(modifier = Modifier.height(10.dp))

		automationDraft.functions.forEachIndexed { index, function ->
			EditorNodeDropTarget(
				gap = 20.dp,
				draggingData = draggingData,
				scope = EditorDraggableScope.FUNCTION,
				onEnter = { onDropZoneEnter(index, null) },
				onExit = onDropZoneExit
			)

			TopLevelNode(
				type = TopLevelNodeType.FUNCTION,
				id = function.id,
				displayName = listOf(function.name),
				content = function.content,
				onDragStart = onNodeDrag,
				onStopDrag = onStopDrag,
				draggableState = draggableState,
				draggingData = draggingData,
				onDropZoneEnter = onDropZoneEnter,
				onDropZoneExit = onDropZoneExit
			)
		}

		EditorNodeDropTarget(
			gap = 20.dp,
			draggingData = draggingData,
			scope = EditorDraggableScope.FUNCTION,
			onEnter = { onDropZoneEnter(automationDraft.functions.size, null) },
			onExit = onDropZoneExit
		)

		Spacer(modifier = Modifier.height(50.dp))
	}
}