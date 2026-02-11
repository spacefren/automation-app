package com.jxd20072.automationapp.ui.shared.settingsItems

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.jxd20072.automationapp.core.data.model.ConfigurationValue
import com.jxd20072.automationapp.core.nodes.props.ValidationResult
import com.jxd20072.automationapp.ui.editor.viewmodel.utils.ProblemSeverity
import com.jxd20072.automationapp.ui.shared.components.BaseTextInput

@Composable
fun SettingsItem(
    item: ConfigurationValue.NUMBER,
    displayName: String,
    description: String = "",
    validate: ((ConfigurationValue.NUMBER) -> ValidationResult)?,
    modifier: Modifier = Modifier,
    onValueChanged: (ConfigurationValue.NUMBER) -> Unit,
    trailingContent: (@Composable RowScope.() -> Unit)? = null
) {
    fun validationFunction(text: String): ValidationResult {
        val number = text.toFloatOrNull()
        return if (number == null) {
            ValidationResult.Error(
                severity = ProblemSeverity.ERROR,
                message = "Only numbers allowed"
            )
        } else {
            validate?.invoke(ConfigurationValue.NUMBER(text)) ?: ValidationResult.Success
        }
    }

    HorizontalSettingsItemContainer(
        displayName = displayName,
        description = description,
        validationResult = validationFunction(item.value),
        trailingContent = trailingContent,
        modifier = modifier
    ) {
        BaseTextInput(
            placeholderText = "",
            text = item.value,
            modifier = Modifier
                .sizeIn(minWidth = 100.dp),
            imeAction = ImeAction.Done,
            onTextChanged = { onValueChanged(ConfigurationValue.NUMBER(it)) }
        )
    }
}