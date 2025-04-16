package com.jxd20072.automationapp.ui.editor.editorBottomSheet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.jxd20072.automationapp.ui.editor.EditorNodeDragData
import com.jxd20072.automationapp.ui.theme.LocalTheme

@Composable
fun EditorBottomSheetCollapsedContent(
	dragData: EditorNodeDragData
) {
	Box(
		modifier = Modifier
			.padding(horizontal = LocalTheme.layoutSystem.spacing.paddingLevel1)
			.padding(bottom = LocalTheme.layoutSystem.spacing.paddingLevel1)
			.clip(RoundedCornerShape(LocalTheme.layoutSystem.shapes.cornerRadiusLevel1))
	) {
		NodeDropDelete(
			dragData = dragData
		)
	}
}