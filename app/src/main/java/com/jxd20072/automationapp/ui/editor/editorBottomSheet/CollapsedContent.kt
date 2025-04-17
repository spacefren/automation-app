package com.jxd20072.automationapp.ui.editor.editorBottomSheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.mandatorySystemGestures
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.ui.editor.EditorNodeDragData
import com.jxd20072.automationapp.ui.theme.LocalTheme

@Composable
fun EditorBottomSheetCollapsedContent(
	dragData: EditorNodeDragData,
	onDeleteZoneEnter: () -> Unit,
	onDeleteZoneExit: () -> Unit,
) {
	Row(
		horizontalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel1),
		modifier = Modifier
			.padding(horizontal = LocalTheme.layoutSystem.spacing.paddingLevel1)
			.windowInsetsPadding(WindowInsets.mandatorySystemGestures)
			.clip(RoundedCornerShape(LocalTheme.layoutSystem.shapes.cornerRadiusLevel1))
	) {
		NodeDropZone(
			dragData = dragData,
			onDeleteZoneEnter = onDeleteZoneEnter,
			onDeleteZoneExit = onDeleteZoneExit,
			mainColor = LocalTheme.colorScheme.surface,
			accentColor = LocalTheme.colorScheme.secondary,
			text = "Drop here to cancel",
			icon = R.drawable.round_close_24,
			modifier = Modifier
				.weight(1f)
		)
		NodeDropZone(
			dragData = dragData,
			onDeleteZoneEnter = onDeleteZoneEnter,
			onDeleteZoneExit = onDeleteZoneExit,
			mainColor = LocalTheme.colorScheme.errorContainer,
			accentColor = LocalTheme.colorScheme.error,
			text = "Drop here to delete",
			icon = R.drawable.round_delete_24,
			modifier = Modifier
				.weight(1f)
		)
	}
}