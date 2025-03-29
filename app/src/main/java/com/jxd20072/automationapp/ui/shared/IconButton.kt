package com.jxd20072.automationapp.ui.shared

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.jxd20072.automationapp.ui.home.getAutomationIconPainter
import com.jxd20072.automationapp.ui.theme.LocalTheme

@Composable
fun Button(
	color: Color,
	cornerRadius: Dp,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	content: @Composable BoxScope.() -> Unit
) {
	var isPressed by remember { mutableStateOf(false) }
	val scale by animateFloatAsState(if (isPressed) .9f else 1f)

	Box(
		modifier = modifier
			.graphicsLayer(
				scaleX = scale,
				scaleY = scale
			)
			.shadow(LocalTheme.layoutSystem.shadows.secondary, RoundedCornerShape(cornerRadius))
			.pointerInput(Unit) {
				detectTapGestures(
					onTap = { onClick() },
					onPress = {
						isPressed = true
						try {
							awaitRelease()
						} finally {
							isPressed = false
						}
					}
				)
			}
			.background(color),
		content = content
	)
}

@Composable
fun IconButton(
	color: Color,
	iconColor: Color,
	drawableRes: Int,
	cornerRadius: Dp,
	padding: Dp,
	modifier: Modifier = Modifier,
	size: Dp,
	onClick: () -> Unit,
) {
	Button(
		color = color,
		cornerRadius = cornerRadius,
		onClick = onClick,
		modifier = modifier.size(size)
	) {
		Icon(
			painter = painterResource(drawableRes),
			contentDescription = "",
			tint = iconColor,
			modifier = Modifier
				.fillMaxSize()
				.padding(padding)
		)
	}
}

@Composable
fun IconButton(
	color: Color,
	iconColor: Color,
	iconName: String,
	cornerRadius: Dp,
	padding: Dp,
	size: Dp,
	modifier: Modifier = Modifier,
	onClick: () -> Unit,
) {
	Button(
		color = color,
		cornerRadius = cornerRadius,
		onClick = onClick,
		modifier = modifier.size(size)
	) {
		Image(
			painter = getAutomationIconPainter(iconName),
			contentDescription = "icon: $iconName",
			colorFilter = ColorFilter.tint(iconColor),
			modifier = Modifier
				.fillMaxSize()
				.padding(padding),
		)
	}
}