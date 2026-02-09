package com.jxd20072.automationapp.ui.editor.editorBottomSheet.editorInputItems

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.core.data.model.ConfigurationValue
import com.jxd20072.automationapp.core.data.model.VariableDef
import com.jxd20072.automationapp.core.data.model.VariableDefValue
import com.jxd20072.automationapp.core.nodes.props.ValidationResult
import com.jxd20072.automationapp.ui.settings.settingsItems.VerticalSettingsItemContainer
import com.jxd20072.automationapp.ui.shared.components.BaseTextInput
import com.jxd20072.automationapp.ui.shared.components.IconButton
import com.jxd20072.automationapp.ui.theme.LocalTheme
import com.jxd20072.automationapp.ui.theme.NestedLevel

inline fun <reified T : ConfigurationValue<*>> extendedValidate(
    item: ConfigurationValue<*>,
    automationVariables: Set<VariableDef>,
    noinline validate: ((T) -> ValidationResult)? = null
): ValidationResult {
    return when (item) {
        is ConfigurationValue.VARIABLE -> {
            val variable = automationVariables.find { it.id == item.value }

            if (variable != null) {
                if (variable.value is VariableDefValue.TEXT) {
                    ValidationResult.Success
                } else {
                    ValidationResult.Error("Variable type not allowed")
                }
            } else {
                ValidationResult.Error("Variable not found")
            }
        }

        is T -> {
            validate?.invoke(item) ?: ValidationResult.Success
        }

        else -> ValidationResult.Error("Input type not allowed")
    }
}

@Composable
inline fun <reified T> EditorInputField(
    item: ConfigurationValue<*>,
    automationVariables: Set<VariableDef>,
    displayName: String,
    description: String = "",
    noinline validate: ((T) -> ValidationResult)?,
    modifier: Modifier = Modifier,
    crossinline onValueChanged: (ConfigurationValue<*>) -> Unit
) where T : ConfigurationValue<*> {
    when (item) {
        is ConfigurationValue.VARIABLE -> {

        }

        else -> {
            when (T::class) {
                ConfigurationValue.TEXT::class -> {
                    val safeItem = (item as? ConfigurationValue.TEXT) ?: ConfigurationValue.TEXT("")
                    VerticalSettingsItemContainer(
                        displayName = displayName,
                        description = description,
                        validationResult = validate?.invoke(safeItem as T),
                        modifier = modifier
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel2)
                        ) {
                            BaseTextInput(
                                placeholderText = "",
                                text = (item as? ConfigurationValue.TEXT)?.value ?: "",
                                modifier = Modifier.weight(1f),
                                imeAction = ImeAction.Done,
                                onTextChanged = { onValueChanged(ConfigurationValue.TEXT(it)) }
                            )
                            IconButton(
                                color = LocalTheme.colorScheme.surface,
                                iconColor = LocalTheme.colorScheme.onSurface,
                                drawableRes = R.drawable.round_bool_24,
                                nestedLevel = NestedLevel.ONE,
                                modifier = Modifier
                                    .size(35.dp)
                            ) { onValueChanged(ConfigurationValue.VARIABLE("")) }
                        }
                    }
                }
            }
        }
    }
}
