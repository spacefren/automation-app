package com.jxd20072.automationapp.ui.shared

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.jxd20072.automationapp.ui.theme.LocalTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

fun exponentialDecay(value: Float, target: Float, reverse: Boolean? = false): Float {
	val newValue = value * if (reverse == true) -1f else 1f
	return newValue / (1 + newValue / target)
}

@Composable
fun SwipeableContainer(
	icon1: Int,
	icon2: Int = icon1,
	backgroundColor1: Color,
	backgroundColor2: Color = backgroundColor1,
	onAction1: () -> Unit,
	onAction2: () -> Unit = onAction1,
	containerCornerRadius: Dp,
	modifier: Modifier = Modifier,
	content: @Composable BoxScope.() -> Unit
) {
	val haptic = LocalHapticFeedback.current
	val coroutineScope = rememberCoroutineScope()

	val actionThreshold = 280f
	val maxDisplacement = 180f
	val maxIconSize = 24f
	val maxIconDisplacement = 13f
	val totalDelta = remember { Animatable(0f) }
	var isDraggingStartToEnd by remember { mutableStateOf(true) }
	var canDoHaptics by remember { mutableStateOf(true) }

	val draggableState = rememberDraggableState {
		isDraggingStartToEnd = totalDelta.targetValue + it > 0f
		coroutineScope.launch {
			totalDelta.snapTo(totalDelta.targetValue + it)

			val isAboveThreshold = totalDelta.targetValue.absoluteValue >= actionThreshold
			if (canDoHaptics && isAboveThreshold) {
				canDoHaptics = false
				haptic.performHapticFeedback(HapticFeedbackType.LongPress)
			} else if (!isAboveThreshold) {
				canDoHaptics = true
			}
		}
	}

	Box(
		modifier = modifier
			.draggable(
				state = draggableState,
				orientation = Orientation.Horizontal,
				onDragStopped = {
					delay(5) // In some cases it fires before the last delta
					if (totalDelta.targetValue.absoluteValue >= actionThreshold) {
						if (isDraggingStartToEnd) {
							onAction1()
						} else {
							onAction2()
						}
					}
					totalDelta.animateTo(0f)
				}
			)
	) {
		Box(
			content = content,
			modifier = Modifier
				.offset {
					val newOffset = exponentialDecay(
						totalDelta.value,
						maxDisplacement,
						!isDraggingStartToEnd
					) * if (isDraggingStartToEnd) 1f else -1f
					IntOffset(newOffset.toInt(), 0)
				}
				.zIndex(1f)
		)

		Box(
			modifier = Modifier
				.background(
					if (isDraggingStartToEnd) backgroundColor1 else backgroundColor2,
					RoundedCornerShape(containerCornerRadius)
				)
				.matchParentSize()
				.padding(
					start = (exponentialDecay(
						totalDelta.value,
						maxIconDisplacement,
						!isDraggingStartToEnd
					)).dp,
					end = (exponentialDecay(
						totalDelta.value,
						maxIconDisplacement,
						!isDraggingStartToEnd
					)).dp
				)
				.zIndex(0f)
		) {
			Icon(
				painter = painterResource(icon1),
				contentDescription = "",
				tint = LocalTheme.colorScheme.onBackground,
				modifier = Modifier
					.size(exponentialDecay(totalDelta.value, maxIconSize, !isDraggingStartToEnd).dp)
					.align(Alignment.CenterStart)
			)
			Icon(
				painter = painterResource(icon2),
				contentDescription = "",
				tint = LocalTheme.colorScheme.onBackground,
				modifier = Modifier
					.size(exponentialDecay(totalDelta.value, maxIconSize, !isDraggingStartToEnd).dp)
					.align(Alignment.CenterEnd)
			)
		}
	}
}