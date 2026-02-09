package com.jxd20072.automationapp.ui.editor.editorBottomSheet.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.core.data.model.ConfigurationValue
import com.jxd20072.automationapp.core.data.model.VariableDef
import com.jxd20072.automationapp.core.nodes.props.NodeInputDefinition
import com.jxd20072.automationapp.core.nodes.props.ValidationResult
import com.jxd20072.automationapp.ui.shared.components.IconButton
import com.jxd20072.automationapp.ui.shared.settingsItems.SettingsItem
import com.jxd20072.automationapp.ui.theme.LocalTheme
import com.jxd20072.automationapp.ui.theme.NestedLevel

@Composable
fun VariableSwitchButton(
    icon: Int,
    onSwitch: () -> Unit,
) {
    IconButton(
        color = LocalTheme.colorScheme.surface,
        iconColor = LocalTheme.colorScheme.onSurface,
        drawableRes = icon,
        nestedLevel = NestedLevel.ONE,
        onClick = onSwitch,
        modifier = Modifier
            .size(37.dp)
            .aspectRatio(1f, true)
    )
}

@Composable
inline fun <T> EditorInputField(
    currentValue: ConfigurationValue<*>,
    variablesList: Set<VariableDef>,
    inputDefinition: NodeInputDefinition<T>,
    noinline promptVariableSelect: ((variableId: String) -> Unit) -> Unit,
    crossinline onValueChanged: (ConfigurationValue<*>) -> Unit,
) where T : ConfigurationValue<*> {
    AnimatedContent(
        targetState = currentValue,
        contentKey = { it::class }
    ) { targetState ->
        when (targetState) {
            is ConfigurationValue.VARIABLE -> {
                SettingsItem(
                    item = targetState,
                    variablesList = variablesList,
                    displayName = inputDefinition.displayName,
                    description = inputDefinition.description,
                    validate = {
                        if (inputDefinition.defaultValue !is ConfigurationValue.VARIABLE) {
                            val variable = variablesList.find { it.id == targetState.value }
                            if (variable == null) {
                                return@SettingsItem ValidationResult.Error("Variable not found")
                            } else if (variable.value::class != inputDefinition.defaultValue::class) {
                                return@SettingsItem ValidationResult.Error(
                                    "Variable type must be ${
                                        inputDefinition.defaultValue::class.simpleName
                                    }"
                                )
                            }
                        }
                        return@SettingsItem ValidationResult.Success
                    },
                    onValueChanged = { onValueChanged(it) },
                    promptVariableSelect = promptVariableSelect
                ) {
                    if (inputDefinition.defaultValue !is ConfigurationValue.VARIABLE) {
                        VariableSwitchButton(R.drawable.round_text_24) {
                            onValueChanged(inputDefinition.defaultValue)
                        }
                    }
                }
            }

            else -> {
                when (inputDefinition.defaultValue) {
                    is ConfigurationValue.TEXT -> {
                        SettingsItem(
                            item = targetState as? ConfigurationValue.TEXT
                                ?: inputDefinition.defaultValue,
                            displayName = inputDefinition.displayName,
                            description = inputDefinition.description,
                            validate = inputDefinition.validate as ((ConfigurationValue.TEXT) -> ValidationResult)?,
                            onValueChanged = { onValueChanged(it) },
                        ) {
                            VariableSwitchButton(R.drawable.round_data_object_24) {
                                onValueChanged(ConfigurationValue.VARIABLE(""))
                            }
                        }
                    }

                    is ConfigurationValue.BOOL -> {
                        SettingsItem(
                            item = targetState as? ConfigurationValue.BOOL
                                ?: inputDefinition.defaultValue,
                            displayName = inputDefinition.displayName,
                            description = inputDefinition.description,
                            validate = inputDefinition.validate as ((ConfigurationValue.BOOL) -> ValidationResult)?,
                            onValueChanged = { onValueChanged(it) },
                        ) {
                            VariableSwitchButton(R.drawable.round_data_object_24) {
                                onValueChanged(ConfigurationValue.VARIABLE(""))
                            }
                        }
                    }

                    is ConfigurationValue.NUMBER -> {
                        SettingsItem(
                            item = targetState as? ConfigurationValue.NUMBER
                                ?: inputDefinition.defaultValue,
                            displayName = inputDefinition.displayName,
                            description = inputDefinition.description,
                            validate = inputDefinition.validate as ((ConfigurationValue.NUMBER) -> ValidationResult)?,
                            onValueChanged = { onValueChanged(it) },
                        ) {
                            VariableSwitchButton(R.drawable.round_data_object_24) {
                                onValueChanged(ConfigurationValue.VARIABLE(""))
                            }
                        }
                    }

                    else -> {}
                }
            }
        }
    }
}
