package com.jxd20072.automationapp.ui.shared

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.jxd20072.automationapp.ui.theme.LocalTheme

class ModalBottomSheetState {
	var isOpen by mutableStateOf(false)
}

@Composable
fun ModalBottomSheet(
	state: ModalBottomSheetState,
	onDismissRequest: () -> Boolean?,
	content: @Composable BoxScope.() -> Unit
) {
	val localFocusManager = LocalFocusManager.current

	Box(modifier = Modifier.zIndex(10f)) {
		AnimatedVisibility(
			visible = state.isOpen,
			enter = fadeIn(),
			exit = fadeOut()
		) {
			Box(
				modifier = Modifier
					.zIndex(10f)
					.fillMaxSize()
					.background(Color.hsv(0f, 0f, 0f, .6f))
					.clickable(interactionSource = null, indication = null) {
						if (onDismissRequest() == true) {
							localFocusManager.clearFocus()
							state.isOpen = false
						}
					}
			)
		}
		AnimatedVisibility(
			visible = state.isOpen,
			enter = slideInVertically(
				animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
				initialOffsetY = { it }
			),
			exit = slideOutVertically(
				animationSpec = spring(stiffness = Spring.StiffnessLow),
				targetOffsetY = { it }
			),
		) {
			Box(
				modifier = Modifier
					.fillMaxSize()
					.padding(top = 30.dp)
					.imePadding()
					.windowInsetsPadding(WindowInsets.statusBars)
			) {
				Box(
					modifier = Modifier
						.align(Alignment.BottomCenter)
						.clickable(interactionSource = null, indication = null) { }
						.fillMaxWidth()
						.clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
						.background(LocalTheme.colorScheme.background)
				) {
					Box(
						modifier = Modifier
							.padding(top = 10.dp)
							.align(Alignment.TopCenter)
							.clip(RoundedCornerShape(5.dp))
							.height(5.dp)
							.width(50.dp)
							.background(LocalTheme.colorScheme.surface)
					)
					Box(
						content = content,
						modifier = Modifier.padding(top = 45.dp)
					)
				}
			}
		}
	}
}