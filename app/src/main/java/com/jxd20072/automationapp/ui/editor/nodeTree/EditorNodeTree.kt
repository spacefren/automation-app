package com.jxd20072.automationapp.ui.editor.nodeTree

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Draggable2DState
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.jxd20072.automationapp.core.data.repository.automations.AutomationDraft
import com.jxd20072.automationapp.ui.editor.EditorDraggableScope
import com.jxd20072.automationapp.ui.editor.EditorNodeDragData
import com.jxd20072.automationapp.ui.theme.LocalTheme

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
	modifier: Modifier = Modifier
) {
	LazyColumn(
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = modifier
			.padding(horizontal = LocalTheme.layoutSystem.spacing.paddingLevel1)
	) {

		itemsIndexed(
			automationDraft.functions,
			key = { _, item -> item.id }
		)
		{ index, function ->
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

		item {
			EditorNodeDropTarget(
				gap = LocalTheme.layoutSystem.spacing.paddingLevel1,
				draggingData = draggingData,
				scope = EditorDraggableScope.FUNCTION,
				onEnter = { onDropZoneEnter(automationDraft.functions.size, null) },
				onExit = onDropZoneExit
			)
		}

		item { Spacer(modifier = Modifier.height(50.dp)) }
	}
}