package com.jxd20072.automationapp.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jxd20072.automationapp.ui.theme.LocalTheme

@Composable
fun IconLabelButton(
	label: String,
	fontSize: TextUnit,
	drawableRes: Int,
	color: Color,
	iconColor: Color,
	iconSize: Dp,
	cornerRadius: Dp,
	padding: Dp = LocalTheme.layoutSystem.spacing.paddingLevel3,
	modifier: Modifier = Modifier,
	onClick: () -> Unit
) {
	Button(
		color = color,
		cornerRadius = cornerRadius,
		onClick = onClick,
		modifier = modifier
	) {
		Row(
			modifier = Modifier
				.background(color)
				.padding(padding)
				.padding(end = 3.dp), // To account for the icon having some extra padding
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(5.dp)
		) {
			Icon(
				painter = painterResource(drawableRes),
				contentDescription = label,
				tint = iconColor,
				modifier = Modifier.size(iconSize)
			)

			Text(
				text = label,
				fontSize = fontSize,
				color = iconColor,
				lineHeight = 1.sp,
				modifier = Modifier
			)
		}
	}
}