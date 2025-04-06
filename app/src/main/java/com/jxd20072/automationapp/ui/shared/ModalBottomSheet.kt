package com.jxd20072.automationapp.ui.shared

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
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
	content: @Composable BoxScope.() -> Unit
) {
	val localConfiguration = LocalConfiguration.current
	val screenHeight = localConfiguration.screenHeightDp
	val localFocusManager = LocalFocusManager.current
	val coroutineScope = rememberCoroutineScope()
	val draggingPos = remember { Animatable(screenHeight.toFloat()) }

	val maxHeight by animateDpAsState(
		if (state.isCollapsed) 50.dp else screenHeight.dp
	)

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

	Box(modifier = Modifier.zIndex(10f)) {
		AnimatedVisibility(
			visible = state.isOpen && !state.isCollapsed,
			enter = fadeIn(),
			exit = fadeOut()
		) {
			Box(
				modifier = Modifier
					.fillMaxSize()
					.background(Color.hsv(0f, 0f, 0f, .6f))
					.clickable(interactionSource = null, indication = null) {
						tryCloseSheet()
					}
			)
		}
		Box(
			modifier = Modifier
				.fillMaxSize()
				.padding(top = 10.dp)
				.offset(y = max(0.dp, draggingPos.value.dp))
				.windowInsetsPadding(WindowInsets.statusBars)
		) {
			Box(
				modifier = Modifier
					.align(Alignment.BottomCenter)
					.fillMaxWidth()
					.heightIn(max = maxHeight)
					.clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
					.background(LocalTheme.colorScheme.background)
					.clickable(interactionSource = null, indication = null) {}
			) {
				Box(
					modifier = Modifier
						.height(15.dp)
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
							.padding(top = 10.dp)
							.align(Alignment.TopCenter)
							.clip(RoundedCornerShape(5.dp))
							.width(50.dp)
							.height(5.dp)
							.background(LocalTheme.colorScheme.surface)
					)
				}
				Box(
					content = content,
					modifier = Modifier
						.padding(top = 25.dp)
				)
			}
		}
	}
}