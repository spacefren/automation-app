package com.jxd20072.automationapp.ui.editor.editorBottomSheet

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.dp
import com.jxd20072.automationapp.ui.editor.EditorNodeDragData
import com.jxd20072.automationapp.ui.theme.LocalTheme
import kotlin.math.abs

@Composable
fun NodeDropDelete(
	dragData: EditorNodeDragData
) {
	val checkHovering = { position: Offset ->
		if (dragData is EditorNodeDragData.Dragging) {
			val pos = dragData.nodePosition.y
			val offset = dragData.inputOffset.y
			abs(pos + offset - position.y) < 100
		} else false
	}

	var positionInRoot by remember { mutableStateOf(Offset(0f, 0f)) }
	val height by animateDpAsState(if (checkHovering(positionInRoot)) 80.dp else 0.dp)

	Box(
		modifier = Modifier
			.fillMaxWidth()
			.height(height)
			.background(LocalTheme.colorScheme.errorContainer)
			.onGloballyPositioned { layout -> positionInRoot = layout.positionInRoot() }
	) {

	}
}