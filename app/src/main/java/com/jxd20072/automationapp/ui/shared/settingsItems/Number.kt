package com.jxd20072.automationapp.ui.shared.settingsItems

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.jxd20072.automationapp.core.data.model.ValidationResult
import com.jxd20072.automationapp.core.data.model.Value
import com.jxd20072.automationapp.ui.editor.viewmodel.utils.ProblemSeverity
import com.jxd20072.automationapp.ui.shared.components.BaseTextInput

@Composable
fun SettingsItem(
    item: Value.FLOAT,
    displayName: String,
    description: String = "",
    validate: ((Value.FLOAT) -> ValidationResult)?,
    modifier: Modifier = Modifier,
    onValueChanged: (Value.FLOAT) -> Unit,
    trailingContent: (@Composable RowScope.() -> Unit)? = null
) {
    var textValue by remember { mutableStateOf(item.valueAsString()) }

    fun validationFunction(text: String): ValidationResult {
        val number = Value.FLOAT.fromString(text)?.value
        return if (number == null) {
            ValidationResult.Error(
                severity = ProblemSeverity.ERROR,
                message = "Only numbers allowed"
            )
        } else {
            validate?.invoke(Value.FLOAT(number)) ?: ValidationResult.Success
        }
    }

    HorizontalSettingsItemContainer(
        displayName = displayName,
        description = description,
        validationResult = validationFunction(textValue),
        trailingContent = trailingContent,
        modifier = modifier
    ) {
        BaseTextInput(
            placeholderText = "",
            text = textValue,
            modifier = Modifier
                .sizeIn(minWidth = 100.dp),
            imeAction = ImeAction.Done,
            onTextChanged = {
                textValue = it
                val doubleValue = Value.FLOAT.fromString(it)
                if (doubleValue != null) {
                    onValueChanged(doubleValue)
                }
            }
        )
    }
}