package com.jxd20072.automationapp.ui.shared.settingsItems

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import com.jxd20072.automationapp.core.data.model.ValidationResult
import com.jxd20072.automationapp.core.data.model.Value
import com.jxd20072.automationapp.ui.shared.components.BaseTextInput

@Composable
fun SettingsItem(
    item: Value.TEXT,
    displayName: String,
    description: String = "",
    validate: ((Value.TEXT) -> ValidationResult)?,
    modifier: Modifier = Modifier,
    onValueChanged: (Value.TEXT) -> Unit,
    trailingContent: (@Composable RowScope.() -> Unit)? = null,
) {
    VerticalSettingsItemContainer(
        displayName = displayName,
        description = description,
        validationResult = validate?.invoke(item),
        trailingContent = trailingContent,
        modifier = modifier
    ) {
        BaseTextInput(
            placeholderText = "",
            text = item.value,
            modifier = Modifier.weight(1f),
            imeAction = ImeAction.Done,
            onTextChanged = { onValueChanged(Value.TEXT(it)) }
        )
    }
}