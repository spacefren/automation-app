package com.jxd20072.automationapp.ui.theme

import androidx.compose.ui.graphics.Color

data class ColorScheme(
	val background: Color,
	val onBackground: Color,
	val surface: Color,
	val onSurface: Color,
	val primary: Color,
	val onPrimary: Color,
	val secondary: Color,
	val onSecondary: Color,
	val tertiary: Color,
	val onTertiary: Color,
	val error: Color,
	val errorContainer: Color,
	val errorSecondary: Color,
	val warning: Color,
	val warningContainer: Color,
	val warningSecondary: Color,
)

val darkColorScheme = ColorScheme(
	background = Color(0xFF121416),
	onBackground = Color.White,
	surface = Color(0xFF1D2329),
	onSurface = Color(0xFF95979a),
	primary = Color(0xFF4181B2),
	onPrimary = Color.White,
	secondary = Color(0xFF51728D),
	onSecondary = Color.White,
	tertiary = Color(0xFF2D3943),
	onTertiary = Color(0xFF95979a),
	error = Color(0xFFCF6161),
	errorContainer = Color(0xFF4A3434),
	errorSecondary = Color(0xFF775050),
	warning = Color(0xFFA4A666),
	warningContainer = Color(0xFF4A4834),
	warningSecondary = Color(0xFF757654),
)

val lightColorScheme = darkColorScheme