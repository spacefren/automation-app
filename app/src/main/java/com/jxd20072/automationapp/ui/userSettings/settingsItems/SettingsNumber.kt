package com.jxd20072.automationapp.ui.userSettings.settingsItems

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import com.jxd20072.automationapp.core.data.model.ConfigurationEntry
import com.jxd20072.automationapp.core.nodes.props.ValidationResult
import com.jxd20072.automationapp.ui.shared.BaseTextInput

@Composable
fun UserSettingsItem(
	item: ConfigurationEntry.NUMBER?,
	defaultItem: ConfigurationEntry.NUMBER,
	displayName: String,
	description: String = "",
	validate: ((ConfigurationEntry.NUMBER) -> ValidationResult)?,
	modifier: Modifier = Modifier,
	onValueChanged: (ConfigurationEntry.NUMBER) -> Unit
) {
	HorizontalUserSettingsItemContainer(
		displayName = displayName,
		description = description,
		modifier = modifier
	) {
		BaseTextInput(
			placeholderText = "",
			text = item?.value ?: defaultItem.value,
			modifier = Modifier
				.fillMaxWidth(),
			imeAction = ImeAction.Done,
			validateInput = {
				val number = it.toFloatOrNull()
				if (number == null) {
					Log.d("APP_A", "AAA")
					ValidationResult.Error("Only numbers allowed")
				} else {
					validate?.invoke(ConfigurationEntry.NUMBER(it)) ?: ValidationResult.Success
				}
			},
			onTextChanged = { onValueChanged(ConfigurationEntry.NUMBER(it)) }
		)
	}
}