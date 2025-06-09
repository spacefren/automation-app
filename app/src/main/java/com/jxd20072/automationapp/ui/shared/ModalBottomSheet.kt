package com.jxd20072.automationapp.ui.shared

import androidx.activity.BackEventCompat
import androidx.activity.compose.BackHandler
import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.zIndex
import com.jxd20072.automationapp.ui.theme.LocalTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.max

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
	val screenHeight = LocalWindowInfo.current.containerSize.height
	val localFocusManager = LocalFocusManager.current
	val localDensity = LocalDensity.current
	val coroutineScope = rememberCoroutineScope()
	val draggingPos = remember { Animatable(screenHeight.toFloat()) }
	var mainContentHeight by remember { mutableIntStateOf(0) }

	val gradientStart = remember { Animatable(0f) }
	val predictiveBackProgress = remember { Animatable(0f) }

	val overlayOpacity = .5f

	LaunchedEffect(state.isOpen, state.isCollapsed) {
		if (state.isOpen) {
			predictiveBackProgress.snapTo(0f)
			gradientStart.animateTo(if (state.isCollapsed) .92f else 0f)
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

	PredictiveBackHandler(
		enabled = state.isOpen
	) { progress: Flow<BackEventCompat> ->
		try {
			progress.collect { backEvent ->
				predictiveBackProgress.snapTo(backEvent.progress)
			}
			if (!tryCloseSheet()) {
				predictiveBackProgress.animateTo(0f)
			}
		} catch (e: CancellationException) {
			predictiveBackProgress.animateTo(0f)
		}
	}

	LaunchedEffect(state.isOpen) {
		if (!state.isOpen) {
			draggingPos.animateTo(screenHeight.toFloat(), spring(stiffness = Spring.StiffnessVeryLow))
		} else {
			draggingPos.animateTo(0f, spring(stiffness = Spring.StiffnessMediumLow))
		}
	}

	Box(
		modifier = Modifier
			.zIndex(10f)
			.fillMaxSize()
			.offset(y = draggingPos.value.dp)
	) {

		Box(
			modifier = Modifier
				.fillMaxSize()
				.offset(y = -with(localDensity) { mainContentHeight.toDp() })
				.clickable(interactionSource = null, indication = null) {
					tryCloseSheet()
				}
				.background(
					Brush.verticalGradient(
						gradientStart.value to Color(0f, 0f, 0f, 0f),
						1f to Color(0f, 0f, 0f, overlayOpacity),
					)
				)
		)
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.height(with(localDensity) { mainContentHeight.toDp() })
				.background(Color(0f, 0f, 0f, overlayOpacity))
				.align(Alignment.BottomCenter)
		)
		Box(
			modifier = Modifier
				.scale((1 - predictiveBackProgress.value / 10))
				.windowInsetsPadding(WindowInsets.statusBars)
				.padding(top = 10.dp)
				.fillMaxWidth()
				.align(Alignment.BottomCenter)
				.onPlaced { mainContentHeight = it.size.height }
				.clip(
					RoundedCornerShape(
						topStart = 35.dp,
						topEnd = 35.dp,
						bottomStart = lerp(
							0.dp,
							35.dp,
							kotlin.math.min(predictiveBackProgress.value * 10, 1f)
						),
						bottomEnd = lerp(0.dp, 35.dp, kotlin.math.min(predictiveBackProgress.value * 10, 1f))
					)
				)
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
									draggingPos.snapTo(max(0f, draggingPos.value + off.y.toDp().value))
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
				Box(
					content = content
				)
			}
		}
	}
}