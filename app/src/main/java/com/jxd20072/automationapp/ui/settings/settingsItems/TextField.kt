package com.jxd20072.automationapp.ui.settings.settingsItems

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import com.jxd20072.automationapp.core.data.model.ConfigurationValue
import com.jxd20072.automationapp.core.nodes.props.ValidationResult
import com.jxd20072.automationapp.ui.shared.components.BaseTextInput
import com.jxd20072.automationapp.ui.theme.LocalTheme

@Composable
fun SettingsItem(
    item: ConfigurationValue.TEXT,
    displayName: String,
    description: String = "",
    validate: ((ConfigurationValue.TEXT) -> ValidationResult)?,
    modifier: Modifier = Modifier,
    onValueChanged: (ConfigurationValue.TEXT) -> Unit,
    trailingContent: (@Composable RowScope.() -> Unit)? = null,
) {
    VerticalSettingsItemContainer(
        displayName = displayName,
        description = description,
        validationResult = validate?.invoke(item),
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel2)
        ) {
            BaseTextInput(
                placeholderText = "",
                text = item.value,
                modifier = Modifier.fillMaxWidth(),
                imeAction = ImeAction.Done,
                onTextChanged = { onValueChanged(ConfigurationValue.TEXT(it)) }
            )
            if (trailingContent != null) {
                trailingContent()
            }
        }
    }
}

@Composable
fun ExpandedSettingsItem(
    item: ConfigurationValue.TEXT,
    displayName: String,
    description: String = "",
    validate: ((ConfigurationValue.TEXT) -> ValidationResult)?,
    buttonIcon: Int,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    onValueChanged: (ConfigurationValue.TEXT) -> Unit,
) {
    SettingsItem(
        item = item,
        displayName = displayName,
        description = description,
        validate = validate,
        modifier = modifier,
        onValueChanged = onValueChanged,
    ) {
    }
}