package com.jxd20072.automationapp.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp

val baseTextStyle = TextStyle(
	fontFamily = FontFamily.Default,
)

data class Typography(
	val title: TextStyle,
	val subTitle: TextStyle,
	val header: TextStyle,
	val body: TextStyle
)

val defaultTypography = Typography(
	title = baseTextStyle.copy(
		fontSize = 32.sp,
	),
	subTitle = baseTextStyle.copy(
		fontSize = 22.sp,
	),
	header = baseTextStyle.copy(
		fontSize = 16.sp,
	),
	body = baseTextStyle.copy(
		fontSize = 14.sp,
	),
)