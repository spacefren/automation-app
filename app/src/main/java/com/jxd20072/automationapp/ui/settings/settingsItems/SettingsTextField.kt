package com.jxd20072.automationapp.ui.settings.settingsItems

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import com.jxd20072.automationapp.core.data.model.ConfigurationValue
import com.jxd20072.automationapp.core.nodes.props.ValidationResult
import com.jxd20072.automationapp.ui.shared.components.BaseTextInput

@Composable
fun UserSettingsItem(
    item: ConfigurationValue.TEXT,
    displayName: String,
    description: String = "",
    validate: ((ConfigurationValue.TEXT) -> ValidationResult)?,
    modifier: Modifier = Modifier,
    onValueChanged: (ConfigurationValue.TEXT) -> Unit
) {
    VerticalUserSettingsItemContainer(
        displayName = displayName,
        description = description,
        validationResult = validate?.invoke(item),
        modifier = modifier
    ) {
        BaseTextInput(
            placeholderText = "",
            text = item.value,
            modifier = Modifier.fillMaxWidth(),
            imeAction = ImeAction.Done,
            onTextChanged = { onValueChanged(ConfigurationValue.TEXT(it)) }
        )
    }
}
