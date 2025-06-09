package com.jxd20072.automationapp.ui.userSettings.settingsItems

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import com.jxd20072.automationapp.core.data.model.NodeInputEntry
import com.jxd20072.automationapp.ui.shared.BaseTextInput

@Composable
fun UserSettingsItem(
	item: NodeInputEntry.TEXT,
	displayName: String,
	description: String = "",
	validate: ((NodeInputEntry.TEXT) -> Boolean)?,
	modifier: Modifier = Modifier,
	onValueChanged: (NodeInputEntry.TEXT) -> Unit
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
			onTextChanged = { onValueChanged(NodeInputEntry.TEXT(it)) }
		)
	}
}
