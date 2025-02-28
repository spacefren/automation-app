package com.jxd20072.automationapp.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun IconLabelButton(
	label: String,
	fontSize: Int,
	drawableRes: Int,
	height: Int,
	color: Color,
	iconColor: Color,
	elevation: Int,
	cornerRadius: Int,
	padding: Int,
	modifier: Modifier = Modifier,
	onClick: () -> Unit
) {
	Row(
		modifier = modifier
			.shadow(elevation.dp, RoundedCornerShape(cornerRadius.dp))
			.clickable(interactionSource = null, indication = ripple(), onClick = onClick)
			.background(color)
			.padding(padding.dp)
			.height(height.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(5.dp)
	) {
		Icon(
			painter = painterResource(drawableRes),
			contentDescription = "new automation",
			tint = iconColor,
			modifier = Modifier
				.fillMaxHeight()
				.aspectRatio(1f)
		)

		Text(
			text = label,
			fontSize = fontSize.sp,
			color = iconColor,
			lineHeight = 1.sp
		)
	}
}