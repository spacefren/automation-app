package com.jxd20072.automationapp.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Button(
	color: Color,
	size: Dp,
	elevation: Dp,
	cornerRadius: Dp,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	content: @Composable BoxScope.() -> Unit
) {
	Box(
		modifier = modifier
			.shadow(elevation, RoundedCornerShape(cornerRadius))
			.size(size)
			.clickable(enabled = true, onClick = onClick)
			.background(color),
		content = content
	)
}

@Composable
fun IconButton(
	color: Color,
	iconColor: Color,
	size: Dp,
	icon: Int,
	cornerRadius: Dp,
	elevation: Dp,
	modifier: Modifier = Modifier,
	onClick: () -> Unit,
) {
	Button(
		color = color,
		size = size,
		elevation = cornerRadius,
		cornerRadius = elevation,
		onClick = onClick,
		modifier = modifier
	) {
		Icon(
			painter = painterResource(icon),
			contentDescription = "",
			tint = iconColor,
			modifier = modifier
				.fillMaxSize()
				.padding(6.dp)
		)
	}
}