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
import com.jxd20072.automationapp.core.data.model.AutomationDef
import com.jxd20072.automationapp.ui.editor.viewmodel.EditorDragNode
import com.jxd20072.automationapp.ui.editor.viewmodel.EditorNodeDragState
import com.jxd20072.automationapp.ui.theme.LocalTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EditorNodeTree(
	automationDraft: AutomationDef,
	draggableState: Draggable2DState,
	draggingData: EditorNodeDragState,
	onNodeDrag: (
		id: String,
		startedPosition: Offset,
		startedOffset: Offset,
	) -> Unit,
	onStopDrag: () -> Unit,
	onDropLineEnter: (targetIndex: Int, parentId: String?) -> Unit,
	onDropLineExit: () -> Unit,
	modifier: Modifier = Modifier
) {
	LazyColumn(
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = modifier
			.padding(horizontal = LocalTheme.layoutSystem.spacing.paddingLevel1)
	) {

		item { Spacer(modifier = Modifier.height(20.dp)) }

		item {
			TopLevelNode(
				type = TopLevelNodeType.ENTRYPOINT,
				id = "_entrypoint",
				displayName = listOf("OnStart"),
				content = automationDraft.entryPoint,
				onDragStart = onNodeDrag,
				onStopDrag = onStopDrag,
				draggableState = draggableState,
				draggingData = draggingData,
				onDropZoneEnter = onDropLineEnter,
				onDropZoneExit = onDropLineExit
			)
		}

		itemsIndexed(
			automationDraft.functions,
			key = { _, item -> item.id }
		)
		{ index, function ->
			EditorNodeDropTarget(
				gap = 20.dp,
				draggingData = draggingData,
				dragNode = EditorDragNode.Function::class,
				onEnter = { onDropLineEnter(index, null) },
				onExit = onDropLineExit
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
				onDropZoneEnter = onDropLineEnter,
				onDropZoneExit = onDropLineExit
			)
		}

		item {
			EditorNodeDropTarget(
				gap = LocalTheme.layoutSystem.spacing.paddingLevel1,
				draggingData = draggingData,
				dragNode = EditorDragNode.Function::class,
				onEnter = { onDropLineEnter(automationDraft.functions.size, null) },
				onExit = onDropLineExit
			)
		}

		item { Spacer(modifier = Modifier.height(50.dp)) }
	}
}