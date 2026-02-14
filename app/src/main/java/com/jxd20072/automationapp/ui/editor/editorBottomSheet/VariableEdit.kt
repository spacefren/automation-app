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
import com.jxd20072.automationapp.core.data.model.ValidationResult
import com.jxd20072.automationapp.core.data.model.Value
import com.jxd20072.automationapp.core.nodes.actions.variables.getConfigValueName
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.EditorSession
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.VariableEditUiState
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.VariableEditViewModel
import com.jxd20072.automationapp.ui.shared.components.AppScreen
import com.jxd20072.automationapp.ui.shared.settingsItems.SettingsItem
import com.jxd20072.automationapp.ui.theme.LocalTheme

fun getVariableDefaultValue(name: String): Value? {
    return when (name) {
        "text" -> Value.TEXT("")
        "boolean" -> Value.BOOL(true)
        "float" -> Value.FLOAT(0f)
        "int" -> Value.INT(0)
        else -> null
    }
}

@Composable
fun VariableEditScreen(
    editorSession: EditorSession,
    variableId: String,
    isCreating: Boolean,
    variableEditViewModel: VariableEditViewModel = hiltViewModel<VariableEditViewModel, VariableEditViewModel.Factory>(
        creationCallback = { factory ->
            factory.create(
                editorSession = editorSession,
                variableId = variableId,
                isCreating = isCreating
            )
        }
    )
) {
    val variableEditUiState by variableEditViewModel.variableUiState.collectAsState()

    if (variableEditUiState !is VariableEditUiState.Success) return
    val variableDef = (variableEditUiState as VariableEditUiState.Success).variableDef

    AppScreen(
        modifier = Modifier
            .padding(LocalTheme.layoutSystem.spacing.paddingLevel1)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .imePadding()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel1),
        ) {
            Text(
                text = "${if (isCreating) "New" else "Edit"} variable",
                style = LocalTheme.typography.subTitle,
                color = LocalTheme.colorScheme.onBackground
            )

            SettingsItem(
                displayName = "Name",
                description = "A unique identifier for your variable. Great names are short and descriptive!",
                item = Value.TEXT(variableDef.name),
                validate = { variableEditViewModel.validateVariableName(it.value) },
                onValueChanged = { newVal ->
                    variableEditViewModel.changeName(newVal.value)
                }
            )

            SettingsItem(
                displayName = "Variable type",
                description = "The type of data that the variable will hold.",
                item = Value.CHOICE(getConfigValueName(variableDef.value)),
                options = listOf(
                    Value.CHOICE("text"),
                    Value.CHOICE("boolean"),
                    Value.CHOICE("int"),
                    Value.CHOICE("float"),
                ),
                onValueChanged = {
                    val newValue = getVariableDefaultValue(it.selected)
                    if (newValue != null) variableEditViewModel.changeValue(newValue)
                }
            )

            AnimatedContent(
                targetState = variableDef.value,
                contentKey = { it::class }
            ) { value ->
                when (value) {
                    is Value.TEXT -> {
                        SettingsItem(
                            displayName = "Value",
                            description = "The starting value of your variable.",
                            item = value,
                            validate = { ValidationResult.Success },
                            onValueChanged = { newVal ->
                                variableEditViewModel.changeValue(newVal)
                            }
                        )
                    }

                    is Value.BOOL -> {
                        SettingsItem(
                            displayName = "Value",
                            description = "The starting value of your variable.",
                            item = value,
                            validate = { ValidationResult.Success },
                            onValueChanged = { newVal ->
                                variableEditViewModel.changeValue(newVal)
                            }
                        )
                    }

                    is Value.FLOAT -> {
                        SettingsItem(
                            displayName = "Value",
                            description = "The starting value of your variable.",
                            item = value,
                            validate = { ValidationResult.Success },
                            onValueChanged = { newVal: Value.FLOAT ->
                                variableEditViewModel.changeValue(newVal)
                            }
                        )
                    }

                    is Value.INT -> {
                        SettingsItem(
                            displayName = "Value",
                            description = "The starting value of your variable.",
                            item = value,
                            validate = { ValidationResult.Success },
                            onValueChanged = { newVal: Value.INT ->
                                variableEditViewModel.changeValue(newVal)
                            }
                        )
                    }

                    else -> {}
                }
            }

        }
    }
}