package com.jxd20072.automationapp.ui.userSettings.settingsItems

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import com.jxd20072.automationapp.core.data.model.ConfigurationEntry
import com.jxd20072.automationapp.ui.shared.BaseTextInput

@Composable
fun UserSettingsItem(
	item: ConfigurationEntry.TEXT,
	displayName: String,
	description: String = "",
	validate: ((ConfigurationEntry.TEXT) -> Boolean)?,
	modifier: Modifier = Modifier,
	onValueChanged: (ConfigurationEntry.TEXT) -> Unit
) {
	VerticalUserSettingsItemContainer(
		displayName = displayName,
		description = description,
		modifier = modifier
	) {
		BaseTextInput(
			placeholderText = "",
			text = item.value,
			modifier = Modifier.fillMaxWidth(),
			imeAction = ImeAction.Done,
			onTextChanged = { onValueChanged(ConfigurationEntry.TEXT(it)) }
		)
	}
}
