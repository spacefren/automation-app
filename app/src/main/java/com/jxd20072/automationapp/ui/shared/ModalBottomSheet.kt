package com.jxd20072.automationapp.ui.shared

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.zIndex
import com.jxd20072.automationapp.ui.theme.LocalTheme
import kotlinx.coroutines.launch

class ModalBottomSheetState {
	var isOpen by mutableStateOf(false)
	var isCollapsed by mutableStateOf(false)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ModalBottomSheet(
	state: ModalBottomSheetState,
	onDismissRequest: () -> Boolean?,
	collapsedContent: (@Composable () -> Unit)? = null,
	content: @Composable BoxScope.() -> Unit
) {
	val localConfiguration = LocalConfiguration.current
	val screenHeight = localConfiguration.screenHeightDp
	val localFocusManager = LocalFocusManager.current
	val coroutineScope = rememberCoroutineScope()
	val draggingPos = remember { Animatable(screenHeight.toFloat()) }
	var mainContentHeight by remember { mutableIntStateOf(0) }
	var collapsedContentHeight by remember { mutableIntStateOf(0) }

	Log.d("APP_A", "${state.isOpen}, ${state.isCollapsed}");

	val maxHeight = remember { Animatable(screenHeight.toFloat()) }
	val gradientSize = remember { Animatable(0f) }

	LaunchedEffect(state.isCollapsed, state.isOpen) {
		if (state.isOpen) {
			maxHeight.animateTo(if (state.isCollapsed) 0f else screenHeight.toFloat())
			gradientSize.animateTo(if (state.isCollapsed) .92f else 0f)
		}
	}


	fun tryCloseSheet(): Boolean {
		if (onDismissRequest() == true) {
			localFocusManager.clearFocus()
			state.isOpen = false
			return true
		}
		return false
	}

	BackHandler(enabled = state.isOpen) {
		tryCloseSheet()
	}

	LaunchedEffect(state.isOpen) {
		if (!state.isOpen) {
			draggingPos.animateTo(screenHeight.toFloat(), spring(stiffness = Spring.StiffnessMediumLow))
		} else {
			draggingPos.animateTo(0f, spring(stiffness = Spring.StiffnessMediumLow))
		}
	}

	Box(
		modifier = Modifier
			.zIndex(10f)
			.fillMaxSize()
			.padding(top = 10.dp)
			.offset(y = max(0.dp, draggingPos.value.dp))
			.windowInsetsPadding(WindowInsets.statusBars)
	) {
		AnimatedVisibility(
			visible = state.isOpen,
			enter = fadeIn(),
			exit = fadeOut()
		) {
			Box(
				modifier = Modifier
					.fillMaxSize()
					.clickable(interactionSource = null, indication = null) {
						tryCloseSheet()
					}
					.offset { IntOffset(0, -mainContentHeight + 50) }
					.background(
						Brush.verticalGradient(
							Pair(gradientSize.value, Color(0f, 0f, 0f, 0f)),
							Pair(1f, Color(0f, 0f, 0f, .4f))
						)
					)
			)
		}
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.layout { measurable, contraints ->
					val placeable = measurable.measure(contraints)
					mainContentHeight = placeable.height
					layout(
						width = placeable.width,
						height = placeable.height
					) {
						placeable.placeRelative(0, 0)
					}
				}
				.align(Alignment.BottomCenter)
				.clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
				.background(LocalTheme.colorScheme.background)
				.clickable(interactionSource = null, indication = null) {}
		) {
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.pointerInput(Unit) {
						detectDragGestures(
							onDrag = { inp, off ->
								inp.consume()
								coroutineScope.launch {
									draggingPos.snapTo(draggingPos.value + off.y.toDp().value)
								}
							},
							onDragEnd = {
								if (draggingPos.value < 75 || !tryCloseSheet()) {
									coroutineScope.launch {
										draggingPos.animateTo(0f)
									}
								}
							}
						)
					}
			) {
				Box(
					modifier = Modifier
						.padding(vertical = 7.5.dp)
						.align(Alignment.TopCenter)
						.clip(RoundedCornerShape(5.dp))
						.width(50.dp)
						.height(5.dp)
						.background(LocalTheme.colorScheme.surface)
				)
			}
			Column(
				modifier = Modifier
					.padding(top = LocalTheme.layoutSystem.spacing.paddingLevel1)
			) {
				AnimatedVisibility(state.isCollapsed) {
					SubcomposeLayout { constraints ->
						val contentPlaceable = subcompose(null) {
							collapsedContent?.invoke() ?: Box { }
						}.map { it.measure(constraints) }.first()

						collapsedContentHeight = contentPlaceable.height

						layout(contentPlaceable.width, contentPlaceable.height) {
							contentPlaceable.placeRelative(0, 0)
						}
					}
				}
				Box(
					content = content,
					modifier = Modifier
						.heightIn(max = maxHeight.value.dp)
				)
			}
		}
	}
}