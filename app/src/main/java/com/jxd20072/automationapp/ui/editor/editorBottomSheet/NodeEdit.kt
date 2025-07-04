package com.jxd20072.automationapp.ui.editor.editorBottomSheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.Text
import com.jxd20072.automationapp.core.data.model.ConfigurationValue
import com.jxd20072.automationapp.core.data.model.NodeType
import com.jxd20072.automationapp.core.nodes.props.NodeInputDefinition
import com.jxd20072.automationapp.core.nodes.props.ValidationResult
import com.jxd20072.automationapp.ui.editor.viewmodel.bottomSheet.NodeEditState
import com.jxd20072.automationapp.ui.shared.SettingsResetButton
import com.jxd20072.automationapp.ui.theme.LocalTheme
import com.jxd20072.automationapp.ui.userSettings.settingsItems.UserSettingsItem

@Composable
fun EditorNodeEditScreen(
	nodeEditState: NodeEditState?,
	setNodeConfig: (name: String, config: ConfigurationValue<*>) -> Unit,
	resetDefaultConfig: () -> Unit,
) {
	if (nodeEditState == null) return

	Column(
		verticalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel1),
		modifier = Modifier
			.imePadding()
			.padding(LocalTheme.layoutSystem.spacing.paddingLevel1)
			.windowInsetsPadding(WindowInsets.navigationBars)
	) {
		Row(
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier.fillMaxWidth()
		) {
			Text(
				text = "Edit " + when (nodeEditState.nodeDef.nodeType) {
					NodeType.ACTION -> "action"
					NodeType.TRIGGER -> "trigger"
					NodeType.FUNCTION -> "function"
				},
				style = LocalTheme.typography.subTitle,
				color = LocalTheme.colorScheme.onBackground
			)
			SettingsResetButton(resetDefaultConfig)
		}

		nodeEditState.nodeData.inputDefinitions.forEach { inputDef ->
			val name = inputDef.key
			val inputDef = inputDef.value
			val currentValue = nodeEditState.nodeDef.input[name]
			NodeEditField(name, inputDef, currentValue) { setNodeConfig(name, it) }
		}
	}
}

@Composable
fun NodeEditField(
	name: String,
	inputDefinition: NodeInputDefinition<*>,
	currentValue: ConfigurationValue<*>?, // Should be T but kotlin is just dumb
	onValueChanged: (ConfigurationValue<*>) -> Unit
) {
	when (inputDefinition.defaultValue) {
		is ConfigurationValue.BOOL -> {
			if (currentValue != null && currentValue !is ConfigurationValue.BOOL) return
			UserSettingsItem(
				item = currentValue ?: inputDefinition.defaultValue,
				displayName = name,
				description = inputDefinition.description,
				validate = inputDefinition.validate as (ConfigurationValue.BOOL) -> ValidationResult,
				onValueChanged = onValueChanged
			)
		}

		is ConfigurationValue.TEXT -> {
			if (currentValue != null && currentValue !is ConfigurationValue.TEXT) return
			UserSettingsItem(
				item = currentValue ?: inputDefinition.defaultValue,
				displayName = name,
				description = inputDefinition.description,
				validate = inputDefinition.validate as (ConfigurationValue.TEXT) -> ValidationResult,
				onValueChanged = onValueChanged
			)
		}

		is ConfigurationValue.NUMBER -> {
			if (currentValue != null && currentValue !is ConfigurationValue.NUMBER) return
			UserSettingsItem(
				item = currentValue,
				defaultItem = inputDefinition.defaultValue,
				displayName = name,
				description = inputDefinition.description,
				validate = inputDefinition.validate as (ConfigurationValue.NUMBER) -> ValidationResult,
				onValueChanged = onValueChanged
			)
		}

		else -> {}
	}
}