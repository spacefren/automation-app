package com.jxd20072.automationapp.ui.userSettings.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jxd20072.automationapp.ui.theme.LocalTheme

@Composable
fun SettingsCategory(
	name: String,
	modifier: Modifier = Modifier,
	content: @Composable ColumnScope.() -> Unit
) {
	Column(
		verticalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel1)
	) {
		Text(
			text = name,
			color = LocalTheme.colorScheme.onSurface,
			style = LocalTheme.typography.header
		)
		content()
	}
}