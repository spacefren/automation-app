package com.jxd20072.automationapp.ui.editor.editorBottomSheet

import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.jxd20072.automationapp.ui.editor.EditorNodeDragData
import com.jxd20072.automationapp.ui.theme.LocalTheme
import com.jxd20072.automationapp.ui.utils.dashedBorder
import kotlin.math.abs

@Composable
fun NodeDropDelete(
	dragData: EditorNodeDragData
) {
	val maxHeightDp = 50.dp

	var shouldShow by remember { mutableStateOf(false) }
	var positionInRoot by remember { mutableStateOf(Offset(0f, 0f)) }
	val height by animateDpAsState(if (shouldShow) maxHeightDp else 0.dp)

	val checkHovering = @Composable { position: Offset ->
		if (dragData is EditorNodeDragData.Dragging) {
			val pos = dragData.nodePosition.y
			val offset = dragData.inputOffset.y
			val maxDistanceToCenter = with(LocalDensity.current) { (maxHeightDp / 2).toPx() }
			val actualHeight = with(LocalDensity.current) { height.toPx() }
			Log.d("APP_A", "${pos + offset - (position.y + actualHeight / 2)}");
			abs(pos + offset - (position.y + actualHeight / 2)) < maxDistanceToCenter
		} else false
	}

	shouldShow = checkHovering(positionInRoot)

	Box(
		modifier = Modifier
			.fillMaxWidth()
			.height(height)
			.background(LocalTheme.colorScheme.errorContainer)
			.padding(4.dp)
			.dashedBorder(
				color = LocalTheme.colorScheme.error,
				shape = RoundedCornerShape(5.dp),
				strokeWidth = 2.dp,
				dashLength = 20.dp,
				gapLength = 12.dp
			)
			.onGloballyPositioned { layout -> positionInRoot = layout.positionInRoot() }
	) {

	}
}