package com.jxd20072.automationapp.ui.home

import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.ui.shared.IconLabelButton
import com.jxd20072.automationapp.ui.theme.LocalTheme

@Composable
fun FloatingNewButton(
	modifier: Modifier = Modifier,
	onClick: () -> Unit
) {
	IconLabelButton(
		label = "New",
		fontSize = 22.sp,
		drawableRes = R.drawable.round_add_24,
		color = LocalTheme.colorScheme.primary,
		iconColor = LocalTheme.colorScheme.onPrimary,
		cornerRadius = LocalTheme.layoutSystem.shapes.cornerRadiusLevel1,
		padding = 15.dp,
		modifier = modifier.offset(-(20).dp, -(30).dp),
		iconSize = 25.dp,
		onClick = onClick
	)
}