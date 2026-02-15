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
fun <T> GenericTextSettingsItem(
    item: T,
    displayName: String,
    description: String,
    valueToString: (T) -> String,
    stringToValue: (String) -> T?,
    validate: ((T) -> ValidationResult)?,
    failedConversionMessage: String,
    modifier: Modifier = Modifier,
    onValueChanged: (T) -> Unit,
    trailingContent: (@Composable RowScope.() -> Unit)? = null
) {
    var textValue by remember { mutableStateOf(valueToString(item)) }

    fun validationFunction(text: String): ValidationResult {
        val newItem = stringToValue(text)
        return if (newItem == null) {
            ValidationResult.Error(
                severity = ProblemSeverity.ERROR,
                message = failedConversionMessage
            )
        } else {
            validate?.invoke(newItem) ?: ValidationResult.Success
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
                val newValue = stringToValue(it)
                if (newValue != null) {
                    onValueChanged(newValue)
                }
            }
        )
    }
}

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
    GenericTextSettingsItem(
        item = item.value,
        displayName = displayName,
        description = description,
        validate = { validate?.invoke(Value.FLOAT(it)) ?: ValidationResult.Success },
        valueToString = { Value.FLOAT(it).valueAsString() },
        stringToValue = { Value.FLOAT.fromString(it)?.value },
        failedConversionMessage = "Only numbers allowed",
        onValueChanged = { onValueChanged(Value.FLOAT(it)) },
        trailingContent = trailingContent,
        modifier = modifier
    )
}

@Composable
fun SettingsItem(
    item: Value.INT,
    displayName: String,
    description: String = "",
    validate: ((Value.INT) -> ValidationResult)?,
    modifier: Modifier = Modifier,
    onValueChanged: (Value.INT) -> Unit,
    trailingContent: (@Composable RowScope.() -> Unit)? = null
) {
    GenericTextSettingsItem(
        item = item.value,
        displayName = displayName,
        description = description,
        validate = { validate?.invoke(Value.INT(it)) ?: ValidationResult.Success },
        valueToString = { Value.INT(it).valueAsString() },
        stringToValue = { Value.INT.fromString(it)?.value },
        failedConversionMessage = "Only integers allowed",
        onValueChanged = { onValueChanged(Value.INT(it)) },
        trailingContent = trailingContent,
        modifier = modifier
    )
}
