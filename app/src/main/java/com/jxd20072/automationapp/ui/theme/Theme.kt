package com.jxd20072.automationapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

data class AppTheme(
	val colorScheme: ColorScheme
)

private val LocalAppTheme = staticCompositionLocalOf {
	AppTheme(
		colorScheme = darkColorScheme
	)
}

val LocalTheme: AppTheme
	@Composable
	get() = LocalAppTheme.current

@Composable
fun AutomationAppTheme(
	darkTheme: Boolean = isSystemInDarkTheme(),
	dynamicColor: Boolean = true,
	content: @Composable () -> Unit
) {
	val theme = AppTheme(
		colorScheme = if (darkTheme) darkColorScheme else lightColorScheme
	)

	CompositionLocalProvider(
		value = LocalAppTheme provides theme,
		content = content
	)
}