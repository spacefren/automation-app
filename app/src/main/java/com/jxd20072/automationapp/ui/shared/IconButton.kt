package com.jxd20072.automationapp.ui.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jxd20072.automationapp.ui.home.getAutomationIconPainter

@Composable
fun Button(
	color: Color,
	cornerRadius: Dp,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	content: @Composable BoxScope.() -> Unit
) {
	Box(
		modifier = modifier
			.shadow(5.dp, RoundedCornerShape(cornerRadius))
			.clickable(interactionSource = null, indication = ripple(), onClick = onClick)
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
	onClick: () -> Unit,
) {
	Button(
		color = color,
		cornerRadius = cornerRadius,
		onClick = onClick,
		modifier = modifier.aspectRatio(1f)
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
	modifier: Modifier = Modifier,
	onClick: () -> Unit,
) {
	Button(
		color = color,
		cornerRadius = cornerRadius,
		onClick = {
			onClick()
		},
		modifier = modifier.aspectRatio(1f)
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