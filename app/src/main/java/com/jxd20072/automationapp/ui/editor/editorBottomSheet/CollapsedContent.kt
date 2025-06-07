package com.jxd20072.automationapp.ui.editor.editorBottomSheet

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.ui.editor.viewmodel.EditorNodeDragState
import com.jxd20072.automationapp.ui.editor.viewmodel.EditorNodeDropData
import com.jxd20072.automationapp.ui.theme.LocalTheme

@Composable
fun EditorBottomSheetCollapsedContent(
	dragData: EditorNodeDragState,
	showDelete: Boolean,
	onDropZoneEnter: (dropData: EditorNodeDropData) -> Unit,
	onDropZoneExit: () -> Unit,
) {
	val screenHeight = LocalWindowInfo.current.containerSize.height
	val navBarsHeight = WindowInsets.navigationBars.getBottom(LocalDensity.current)
	var edgePosition by remember { mutableIntStateOf(0) }
	var expanded by remember { mutableStateOf(false) }
	val maxHeight by animateDpAsState(if (expanded) 75.dp else 0.dp)

	@Composable
	fun shouldExpand(): Boolean {
		return if (dragData is EditorNodeDragState.Dragging) {
			val dragPos = dragData.nodePosition + dragData.inputOffset
			val extraSpace = with(LocalDensity.current) { 25.dp.toPx() }
			dragPos.y > edgePosition - extraSpace
		} else false
	}

	expanded = shouldExpand()

	Row(
		horizontalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel1),
		modifier = Modifier
			.padding(horizontal = LocalTheme.layoutSystem.spacing.paddingLevel1)
			.windowInsetsPadding(WindowInsets.navigationBars)
			.clip(RoundedCornerShape(LocalTheme.layoutSystem.shapes.cornerRadiusLevel1))
			.onGloballyPositioned {
				edgePosition = screenHeight - navBarsHeight - it.size.height
			}
			.heightIn(max = maxHeight)
	) {
		NodeDropZone(
			dragData = dragData,
			onDropZoneEnter = { onDropZoneEnter(EditorNodeDropData.Cancelling) },
			onDropZoneExit = onDropZoneExit,
			mainColor = LocalTheme.colorScheme.surface,
			accentColor = LocalTheme.colorScheme.secondary,
			text = "Drop here to cancel",
			icon = R.drawable.round_close_24,
			modifier = Modifier
				.weight(1f)
		)
		if (showDelete) {
			NodeDropZone(
				dragData = dragData,
				onDropZoneEnter = { onDropZoneEnter(EditorNodeDropData.Deleting) },
				onDropZoneExit = onDropZoneExit,
				mainColor = LocalTheme.colorScheme.errorContainer,
				accentColor = LocalTheme.colorScheme.error,
				text = "Drop here to delete",
				icon = R.drawable.round_delete_24,
				modifier = Modifier
					.weight(1f)
			)
		}
	}
}