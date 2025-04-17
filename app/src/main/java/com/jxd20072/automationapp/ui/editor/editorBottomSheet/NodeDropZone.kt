package com.jxd20072.automationapp.ui.editor.editorBottomSheet

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jxd20072.automationapp.ui.editor.EditorNodeDragData
import com.jxd20072.automationapp.ui.theme.LocalTheme
import com.jxd20072.automationapp.ui.utils.conditional
import com.jxd20072.automationapp.ui.utils.dashedBorder
import kotlin.math.abs

@Composable
fun NodeDropZone(
	dragData: EditorNodeDragData,
	onDeleteZoneEnter: () -> Unit,
	onDeleteZoneExit: () -> Unit,
	mainColor: Color,
	accentColor: Color,
	text: String,
	icon: Int,
	modifier: Modifier = Modifier
) {
	val maxHeightDp = 75.dp

	var isInHeight by remember { mutableStateOf(false) }
	var isInWidth by remember { mutableStateOf(false) }

	val scale by animateFloatAsState(if (isInWidth) 1f else .95f)
	var positionInRoot by remember { mutableStateOf(Offset(0f, 0f)) } // Measures from baseline
	var width by remember { mutableIntStateOf(0) }
	val height by animateDpAsState(if (isInHeight) maxHeightDp else 0.dp)
	var textWidth by remember { mutableFloatStateOf(0f) }

	val checkIsInHeight = @Composable { position: Offset ->
		if (dragData is EditorNodeDragData.Dragging) {
			val dragPos = dragData.nodePosition
			val offset = dragData.inputOffset
			val maxHeight = with(LocalDensity.current) { maxHeightDp.toPx() }
			abs(dragPos.y + offset.y - position.y) < maxHeight / (if (isInHeight) 1f else 2f)
		} else false
	}

	val checkIsInWidth = @Composable { position: Offset ->
		if (dragData is EditorNodeDragData.Dragging) {
			val dragPos = dragData.nodePosition
			val offset = dragData.inputOffset
			abs(dragPos.x + offset.x - (position.x + width / 2)) < width / 2
		} else false
	}

	isInWidth = checkIsInWidth(positionInRoot)
	isInHeight = checkIsInHeight(positionInRoot)

	LaunchedEffect(isInWidth && isInHeight) {
		if (isInWidth && isInHeight) {
			onDeleteZoneEnter()
		} else {
			onDeleteZoneExit()
		}
	}

	Row(
		horizontalArrangement = Arrangement.Center,
		verticalAlignment = Alignment.CenterVertically,
		modifier = modifier
			.graphicsLayer(scaleX = scale, scaleY = scale)
			.fillMaxWidth()
			.height(height)
			.onGloballyPositioned { layout ->
				val rootPos = layout.positionInRoot()
				positionInRoot = Offset(rootPos.x, rootPos.y + layout.size.height)
				width = layout.size.width
			}
			.shadow(
				LocalTheme.layoutSystem.shadows.primary,
				RoundedCornerShape(LocalTheme.layoutSystem.shapes.cornerRadiusLevel1)
			)
			.background(mainColor)
			.padding(LocalTheme.layoutSystem.spacing.paddingLevel3)
			.dashedBorder(
				color = accentColor,
				shape = RoundedCornerShape(LocalTheme.layoutSystem.shapes.cornerRadiusLevel3),
				strokeWidth = 2.dp,
				dashLength = 20.dp,
				gapLength = 8.dp
			)
			.padding(LocalTheme.layoutSystem.spacing.paddingLevel3)
	) {
		Image(
			painter = painterResource(icon),
			contentDescription = "$text icon",
			colorFilter = ColorFilter.tint(LocalTheme.colorScheme.onBackground),
			modifier = Modifier
				.size(24.dp)
		)
		Spacer(modifier = Modifier.width(LocalTheme.layoutSystem.spacing.paddingLevel2))
		Text(
			text = text,
			color = LocalTheme.colorScheme.onBackground,
			fontSize = 17.sp,
			onTextLayout = { // I know I'm genius
				val lineEnds = mutableListOf<Float>()
				for (i in 0..it.lineCount - 1) {
					lineEnds.add(it.getLineRight(i) - it.getLineLeft(i))
				}
				textWidth = lineEnds.max()
			},
			modifier = Modifier
				.conditional(textWidth != 0f, ifTrue = {
					width(with(LocalDensity.current) { textWidth.toDp() })
				})
		)
	}
}