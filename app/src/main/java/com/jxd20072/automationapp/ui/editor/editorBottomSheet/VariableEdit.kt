package com.jxd20072.automationapp.ui.editor.editorBottomSheet

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
import com.jxd20072.automationapp.ui.theme.LocalTheme
import com.jxd20072.automationapp.ui.userSettings.settingsItems.UserSettingsItem

@Composable
fun VariableEditScreen(
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
			text = "Edit variable",
			style = LocalTheme.typography.subTitle,
			color = LocalTheme.colorScheme.onBackground
		)

		UserSettingsItem(
			displayName = "Name",
			item = ConfigurationValue.TEXT(editingVariable.name),
			validate = { validateVariableName(it.value) },
		) { newVal ->
			changeVariableName(newVal.value)
		}
	}
}