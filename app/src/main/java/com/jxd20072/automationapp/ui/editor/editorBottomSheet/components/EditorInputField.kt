package com.jxd20072.automationapp.ui.editor.editorBottomSheet.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.core.data.model.InputAcceptedValue
import com.jxd20072.automationapp.core.data.model.NodeInputSchema
import com.jxd20072.automationapp.core.data.model.ValidationResult
import com.jxd20072.automationapp.core.data.model.Value
import com.jxd20072.automationapp.core.data.model.VariableDef
import com.jxd20072.automationapp.ui.editor.viewmodel.utils.EditorProblem
import com.jxd20072.automationapp.ui.shared.components.IconButton
import com.jxd20072.automationapp.ui.shared.settingsItems.SettingsItem
import com.jxd20072.automationapp.ui.theme.LocalTheme
import com.jxd20072.automationapp.ui.theme.NestedLevel
import kotlin.reflect.KClass

@Composable
fun TypeSwitchButton(
    currentType: KClass<out Value>,
    onSwitch: () -> Unit,
) {
    IconButton(
        color = LocalTheme.colorScheme.surface,
        iconColor = LocalTheme.colorScheme.onSurface,
        drawableRes = when (currentType) {
            Value.TEXT::class -> R.drawable.round_text_24
            Value.INT::class -> R.drawable.round_number_24
            Value.BOOL::class -> R.drawable.round_bool_24
            Value.VARIABLE::class -> R.drawable.round_data_object_24
            Value.EXPRESSION::class -> R.drawable.round_functions_24
            Value.CHOICE::class -> R.drawable.round_equal_24
            else -> R.drawable.round_error_24
        },
        nestedLevel = NestedLevel.ONE,
        onClick = onSwitch,
        modifier = Modifier
            .size(37.dp)
            .aspectRatio(1f, true)
    )
}

@Composable
fun EditorInputField(
    currentValue: Value,
    variablesList: Set<VariableDef>,
    inputSchema: NodeInputSchema,
    problem: EditorProblem.NodeInputProblem?,
    promptVariableSelect: (
        variableTypes: Set<KClass<out Value>>,
        (variableId: String) -> Unit
    ) -> Unit,
    onValueChanged: (Value) -> Unit,
) {
    AnimatedContent(
        targetState = currentValue,
        contentKey = { it::class }
    ) { targetState ->
        val currentIndex = when (
            val index =
                inputSchema.accepts.indexOfFirst { it.defaultValue::class == targetState::class }
        ) {
            -1 -> 0
            else -> index
        }

        when (targetState) {
            is Value.VARIABLE -> {
                SettingsItem(
                    item = targetState,
                    variablesList = variablesList,
                    displayName = inputSchema.displayName,
                    description = inputSchema.description,
                    validate = {
                        if (problem != null) ValidationResult.Error(
                            problem.severity,
                            problem.message
                        )
                        else ValidationResult.Success
                    },
                    onValueChanged = { onValueChanged(it) },
                    promptVariableSelect = {
                        promptVariableSelect(
                            (inputSchema.accepts
                                .firstOrNull { it is InputAcceptedValue.VARIABLE } as? InputAcceptedValue.VARIABLE)
                                ?.acceptedTypes
                                ?: emptySet()
                        ) { id ->
                            onValueChanged(Value.VARIABLE(id))
                        }
                    }
                ) {
                    if (inputSchema.accepts.size == 1) return@SettingsItem
                    val nextIndex = (currentIndex + 1) % inputSchema.accepts.size

                    TypeSwitchButton(inputSchema.accepts[nextIndex].defaultValue::class) {
                        onValueChanged(inputSchema.accepts[nextIndex].defaultValue)
                    }
                }
            }

            is Value.TEXT -> {
                SettingsItem(
                    item = targetState,
                    displayName = inputSchema.displayName,
                    description = inputSchema.description,
                    validate = {
                        if (problem != null) ValidationResult.Error(
                            problem.severity,
                            problem.message
                        )
                        else ValidationResult.Success
                    },
                    onValueChanged = { onValueChanged(it) },
                ) {
                    if (inputSchema.accepts.size == 1) return@SettingsItem
                    val nextIndex = (currentIndex + 1) % inputSchema.accepts.size

                    TypeSwitchButton(inputSchema.accepts[nextIndex].defaultValue::class) {
                        onValueChanged(inputSchema.accepts[nextIndex].defaultValue)
                    }
                }
            }

            is Value.BOOL -> {
                SettingsItem(
                    item = targetState,
                    displayName = inputSchema.displayName,
                    description = inputSchema.description,
                    validate = {
                        if (problem != null) ValidationResult.Error(
                            problem.severity,
                            problem.message
                        )
                        else ValidationResult.Success
                    },
                    onValueChanged = { onValueChanged(it) },
                ) {
                    if (inputSchema.accepts.size == 1) return@SettingsItem
                    val nextIndex = (currentIndex + 1) % inputSchema.accepts.size

                    TypeSwitchButton(inputSchema.accepts[nextIndex].defaultValue::class) {
                        onValueChanged(inputSchema.accepts[nextIndex].defaultValue)
                    }
                }
            }

            is Value.FLOAT -> {
                SettingsItem(
                    item = targetState,
                    displayName = inputSchema.displayName,
                    description = inputSchema.description,
                    validate = {
                        if (problem != null) ValidationResult.Error(
                            problem.severity,
                            problem.message
                        )
                        else ValidationResult.Success
                    },
                    onValueChanged = { onValueChanged(it) },
                ) {
                    if (inputSchema.accepts.size == 1) return@SettingsItem
                    val nextIndex = (currentIndex + 1) % inputSchema.accepts.size

                    TypeSwitchButton(inputSchema.accepts[nextIndex].defaultValue::class) {
                        onValueChanged(inputSchema.accepts[nextIndex].defaultValue)
                    }
                }
            }

            is Value.INT -> {
                SettingsItem(
                    item = targetState,
                    displayName = inputSchema.displayName,
                    description = inputSchema.description,
                    validate = {
                        if (problem != null) ValidationResult.Error(
                            problem.severity,
                            problem.message
                        )
                        else ValidationResult.Success
                    },
                    onValueChanged = { onValueChanged(it) },
                ) {
                    if (inputSchema.accepts.size == 1) return@SettingsItem
                    val nextIndex = (currentIndex + 1) % inputSchema.accepts.size

                    TypeSwitchButton(inputSchema.accepts[nextIndex].defaultValue::class) {
                        onValueChanged(inputSchema.accepts[nextIndex].defaultValue)
                    }
                }
            }

            is Value.EXPRESSION -> {
                SettingsItem(
                    item = Value.TEXT(targetState.value),
                    displayName = inputSchema.displayName,
                    description = inputSchema.description,
                    validate = {
                        if (problem != null) ValidationResult.Error(
                            problem.severity,
                            problem.message
                        )
                        else ValidationResult.Success
                    },
                    onValueChanged = { onValueChanged(Value.EXPRESSION(it.value)) },
                ) {
                    if (inputSchema.accepts.size == 1) return@SettingsItem
                    val nextIndex = (currentIndex + 1) % inputSchema.accepts.size

                    TypeSwitchButton(inputSchema.accepts[nextIndex].defaultValue::class) {
                        onValueChanged(inputSchema.accepts[nextIndex].defaultValue)
                    }
                }
            }

            is Value.CHOICE -> {}
        }
    }
}
