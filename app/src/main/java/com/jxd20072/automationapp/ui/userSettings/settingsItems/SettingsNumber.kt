package com.jxd20072.automationapp.ui.userSettings.settingsItems

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import com.jxd20072.automationapp.core.data.model.ConfigurationValue
import com.jxd20072.automationapp.core.nodes.props.ValidationResult
import com.jxd20072.automationapp.ui.shared.BaseTextInput

@Composable
fun UserSettingsItem(
	item: ConfigurationValue.NUMBER?,
	defaultItem: ConfigurationValue.NUMBER,
	displayName: String,
	description: String = "",
	validate: ((ConfigurationValue.NUMBER) -> ValidationResult)?,
	modifier: Modifier = Modifier,
	onValueChanged: (ConfigurationValue.NUMBER) -> Unit
) {
	fun validationFunction(text: String): ValidationResult {
		val number = text.toFloatOrNull()
		return if (number == null) {
			ValidationResult.Error("Only numbers allowed")
		} else {
			validate?.invoke(ConfigurationValue.NUMBER(text)) ?: ValidationResult.Success
		}
	}

	HorizontalUserSettingsItemContainer(
		displayName = displayName,
		description = description,
		validationResult = validationFunction(item?.value ?: defaultItem.value),
		modifier = modifier
	) {
		BaseTextInput(
			placeholderText = "",
			text = item?.value ?: defaultItem.value,
			modifier = Modifier,
			imeAction = ImeAction.Done,
			onTextChanged = { onValueChanged(ConfigurationValue.NUMBER(it)) }
		)
	}
}