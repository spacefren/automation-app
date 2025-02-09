package com.jxd20072.automationapp.ui.shared

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager

@Composable
fun Layer(
	modifier: Modifier = Modifier,
	content: @Composable BoxScope.() -> Unit
) {
	val focusManager = LocalFocusManager.current
	Box(
		content = content,
		modifier = modifier
			.pointerInput(Unit) {
				detectTapGestures(
					onTap = {
						focusManager.clearFocus()
					}
				)
			},
	)
}