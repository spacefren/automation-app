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
import androidx.compose.ui.Modifier
import com.jxd20072.automationapp.core.data.model.ConfigurationValue
import com.jxd20072.automationapp.core.data.model.VariableDef
import com.jxd20072.automationapp.core.data.model.VariableDefValue
import com.jxd20072.automationapp.core.nodes.props.ValidationResult
import com.jxd20072.automationapp.ui.editor.viewmodel.bottomSheet.VariableEditState
import com.jxd20072.automationapp.ui.theme.LocalTheme
import com.jxd20072.automationapp.ui.userSettings.settingsItems.UserSettingsItem

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
	variableEditState: VariableEditState,
	editingVariable: VariableDef?,
	validateVariableName: (name: String) -> ValidationResult,
	changeVariableName: (newName: String) -> Unit,
	changeVariableValue: (newValue: VariableDefValue<*>) -> Unit,
) {
	if (editingVariable == null) return

	Column(
		verticalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel1),
		modifier = Modifier
			.imePadding()
			.padding(LocalTheme.layoutSystem.spacing.paddingLevel1)
			.windowInsetsPadding(WindowInsets.navigationBars)
	) {
		Text(
			text = "${if (variableEditState.isCreating) "Create" else "Edit"} variable",
			style = LocalTheme.typography.subTitle,
			color = LocalTheme.colorScheme.onBackground
		)

		UserSettingsItem(
			displayName = "Name",
			description = "A unique identifier for your variable. Great names are short and descriptive!",
			item = ConfigurationValue.TEXT(editingVariable.name),
			validate = { validateVariableName(it.value) },
		) { newVal ->
			changeVariableName(newVal.value)
		}

		UserSettingsItem(
			displayName = "Variable type",
			description = "The type of data that the variable will hold.",
			selectedOption = ConfigurationValue.CHOOSE(getVariableValueName(editingVariable.value)),
			options = listOf(
				ConfigurationValue.CHOOSE("text"),
				ConfigurationValue.CHOOSE("boolean"),
				ConfigurationValue.CHOOSE("number"),
			),
			onValueChanged = {
				val newValue = getVariableDefaultValue(it.value)
				if (newValue != null) changeVariableValue(newValue)
			}
		)

		AnimatedContent(
			targetState = editingVariable.value,
			contentKey = { it::class }
		) { value ->
			when (value) {
				is VariableDefValue.TEXT -> {
					UserSettingsItem(
						displayName = "Value",
						description = "The starting value of your variable.",
						item = ConfigurationValue.TEXT(value.value),
						validate = { ValidationResult.Success },
					) { newVal ->
						changeVariableValue(VariableDefValue.TEXT(newVal.value))
					}
				}

				is VariableDefValue.BOOL -> {
					UserSettingsItem(
						displayName = "Value",
						description = "The starting value of your variable.",
						item = ConfigurationValue.BOOL(value.value),
						validate = { ValidationResult.Success },
						onValueChanged = { newVal ->
							changeVariableValue(VariableDefValue.BOOL(newVal.value))
						}
					)
				}

				is VariableDefValue.NUMBER -> {
					UserSettingsItem(
						displayName = "Value",
						description = "The starting value of your variable.",
						item = ConfigurationValue.NUMBER(value.value),
						defaultItem = ConfigurationValue.NUMBER(VariableDefValue.NUMBER().value),
						validate = { ValidationResult.Success },
						onValueChanged = { newVal: ConfigurationValue.NUMBER ->
							changeVariableValue(VariableDefValue.NUMBER(newVal.value))
						}
					)
				}
			}
		}

	}
}