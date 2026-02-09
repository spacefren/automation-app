package com.jxd20072.automationapp.ui.editor.editorBottomSheet

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.jxd20072.automationapp.core.data.model.ConfigurationValue
import com.jxd20072.automationapp.core.data.model.VariableDefValue
import com.jxd20072.automationapp.core.nodes.props.ValidationResult
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.VariableEditUiState
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.VariableEditViewModel
import com.jxd20072.automationapp.ui.shared.components.AppScreen
import com.jxd20072.automationapp.ui.shared.settingsItems.SettingsItem
import com.jxd20072.automationapp.ui.theme.LocalTheme

fun getVariableValueName(value: VariableDefValue<*>): String {
    return when (value) {
        is VariableDefValue.TEXT -> "text"
        is VariableDefValue.BOOL -> "boolean"
        is VariableDefValue.NUMBER -> "number"
    }
}

fun getVariableDefaultValue(name: String): VariableDefValue<*>? {
    return when (name) {
        "text" -> VariableDefValue.TEXT()
        "boolean" -> VariableDefValue.BOOL()
        "number" -> VariableDefValue.NUMBER()
        else -> null
    }
}

@Composable
fun VariableEditScreen(
    variableId: String,
    isCreating: Boolean,
    variableEditViewModel: VariableEditViewModel = hiltViewModel<VariableEditViewModel, VariableEditViewModel.Factory>(
        creationCallback = { factory -> factory.create(variableId, isCreating) }
    )
) {
    val variableEditUiState by variableEditViewModel.variableUiState.collectAsState()

    if (variableEditUiState !is VariableEditUiState.Success) return
    val variableDef = (variableEditUiState as VariableEditUiState.Success).variableDef

    AppScreen {
        Column(
            verticalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel1),
            modifier = Modifier
                .imePadding()
                .padding(LocalTheme.layoutSystem.spacing.paddingLevel1)
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            Text(
                text = "${if (isCreating) "New" else "Edit"} variable",
                style = LocalTheme.typography.subTitle,
                color = LocalTheme.colorScheme.onBackground
            )

            SettingsItem(
                displayName = "Name",
                description = "A unique identifier for your variable. Great names are short and descriptive!",
                item = ConfigurationValue.TEXT(variableDef.name),
                validate = { variableEditViewModel.validateVariableName(it.value) },
                onValueChanged = { newVal ->
                    variableEditViewModel.changeName(newVal.value)
                }
            )

            SettingsItem(
                displayName = "Variable type",
                description = "The type of data that the variable will hold.",
                item = ConfigurationValue.CHOOSE(getVariableValueName(variableDef.value)),
                options = listOf(
                    ConfigurationValue.CHOOSE("text"),
                    ConfigurationValue.CHOOSE("boolean"),
                    ConfigurationValue.CHOOSE("number"),
                ),
                onValueChanged = {
                    val newValue = getVariableDefaultValue(it.value)
                    if (newValue != null) variableEditViewModel.changeValue(newValue)
                }
            )

            AnimatedContent(
                targetState = variableDef.value,
                contentKey = { it::class }
            ) { value ->
                when (value) {
                    is VariableDefValue.TEXT -> {
                        SettingsItem(
                            displayName = "Value",
                            description = "The starting value of your variable.",
                            item = ConfigurationValue.TEXT(value.value),
                            validate = { ValidationResult.Success },
                            onValueChanged = { newVal ->
                                variableEditViewModel.changeValue(VariableDefValue.TEXT(newVal.value))
                            }
                        )
                    }

                    is VariableDefValue.BOOL -> {
                        SettingsItem(
                            displayName = "Value",
                            description = "The starting value of your variable.",
                            item = ConfigurationValue.BOOL(value.value),
                            validate = { ValidationResult.Success },
                            onValueChanged = { newVal ->
                                variableEditViewModel.changeValue(VariableDefValue.BOOL(newVal.value))
                            }
                        )
                    }

                    is VariableDefValue.NUMBER -> {
                        SettingsItem(
                            displayName = "Value",
                            description = "The starting value of your variable.",
                            item = ConfigurationValue.NUMBER(value.value),
                            validate = { ValidationResult.Success },
                            onValueChanged = { newVal: ConfigurationValue.NUMBER ->
                                variableEditViewModel.changeValue(VariableDefValue.NUMBER(newVal.value))
                            }
                        )
                    }
                }
            }

        }
    }
}