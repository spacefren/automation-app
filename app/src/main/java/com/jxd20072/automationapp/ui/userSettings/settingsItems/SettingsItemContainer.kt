package com.jxd20072.automationapp.ui.userSettings.settingsItems

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jxd20072.automationapp.ui.theme.LocalTheme

@Composable
fun UserSettingsItemContainer(
	displayName: String,
	description: String = "",
	modifier: Modifier = Modifier,
	content: @Composable BoxScope.() -> Unit
) {
	Row(
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically,
		modifier = modifier
			.fillMaxWidth()
	) {
		Column(
			verticalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel3),
			modifier = Modifier
				.weight(1f)
		) {
			Text(
				text = displayName,
				color = LocalTheme.colorScheme.onBackground,
				style = LocalTheme.typography.header
			)
			if (description.isNotEmpty()) {
				Text(
					text = description,
					color = LocalTheme.colorScheme.onSurface,
					style = LocalTheme.typography.body
				)
			}
		}
		Spacer(modifier = Modifier.width(LocalTheme.layoutSystem.spacing.paddingLevel3))
		Box(
			content = content,
			modifier = modifier.height(35.dp)
		)
	}
}