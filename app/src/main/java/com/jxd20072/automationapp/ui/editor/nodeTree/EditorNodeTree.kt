package com.jxd20072.automationapp.ui.editor.nodeTree

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Draggable2DState
import androidx.compose.foundation.layout.Arrangement
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
import com.jxd20072.automationapp.ui.editor.EditorDraggingData

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EditorNodeTree(
	automationDraft: AutomationDraft,
	draggingData: EditorDraggingData,
	onNodeDrag: (
		id: String,
		startedPosition: Offset,
		startedOffset: Offset,
		scope: EditorDraggableScope
	) -> Unit,
	onStopDrag: () -> Unit,
	draggableState: Draggable2DState
) {
	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(20.dp),
		modifier = Modifier
			.padding(horizontal = 20.dp)
			.verticalScroll(rememberScrollState(0))
	) {
		Spacer(modifier = Modifier.height(5.dp))

		if (automationDraft.entryPoint.isNotEmpty()) {
			TopLevelNode(
				type = TopLevelNodeType.ENTRYPOINT,
				id = "0",
				displayName = listOf("On Start"),
				content = automationDraft.entryPoint,
				onDragStart = onNodeDrag,
				onStopDrag = onStopDrag,
				draggableState = draggableState,
				draggingData = draggingData
			)
		}
		automationDraft.functions.forEach { function ->
			TopLevelNode(
				type = TopLevelNodeType.FUNCTION,
				id = "0",
				displayName = listOf(function.name),
				content = automationDraft.entryPoint,
				onDragStart = onNodeDrag,
				onStopDrag = onStopDrag,
				draggableState = draggableState,
				draggingData = draggingData
			)
		}
		Spacer(modifier = Modifier.height(50.dp))
	}
}