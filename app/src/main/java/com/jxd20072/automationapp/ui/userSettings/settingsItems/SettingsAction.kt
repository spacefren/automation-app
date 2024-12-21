package com.jxd20072.automationapp.ui.userSettings.settingsItems

import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.ui.shared.IconButton

@Composable
fun UserSettingsItem(
	displayName: String,
	description: String,
	modifier: Modifier = Modifier,
	onActivate: () -> Unit
) {
	UserSettingsItemContainer(
		displayName = displayName,
		description = description,
		modifier = modifier
	) {
		IconButton(
			color = MaterialTheme.colorScheme.surface,
			iconColor = MaterialTheme.colorScheme.onSurface,
			drawableRes = R.drawable.round_open_in_new_24,
			cornerRadius = 8.dp,
			padding = 6.dp,
			modifier = Modifier
				.size(42.dp),
			onClick = onActivate,
		)
	}
}
