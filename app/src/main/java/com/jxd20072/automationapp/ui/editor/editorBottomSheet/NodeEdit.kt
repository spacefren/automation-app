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
import com.jxd20072.automationapp.core.data.model.ConfigurationEntry
import com.jxd20072.automationapp.core.data.model.NodeDef
import com.jxd20072.automationapp.core.data.model.NodeType
import com.jxd20072.automationapp.core.nodes.NodeData
import com.jxd20072.automationapp.core.nodes.props.NodeInputDefinition
import com.jxd20072.automationapp.core.nodes.props.ValidationResult
import com.jxd20072.automationapp.ui.editor.viewmodel.bottomSheet.NodeEditState
import com.jxd20072.automationapp.ui.shared.SettingsResetButton
import com.jxd20072.automationapp.ui.theme.LocalTheme
import com.jxd20072.automationapp.ui.userSettings.settingsItems.UserSettingsItem

@Composable
fun EditorNodeEditScreen(
	nodeEditState: NodeEditState,
	editingNode: NodeDef?,
	editingNodeData: NodeData?,
	setNodeConfig: (name: String, config: ConfigurationEntry<*>) -> Unit,
	resetDefaultConfig: () -> Unit,
) {
	if (nodeEditState !is NodeEditState.Editing) return
	if (editingNode == null || editingNodeData == null) return

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
				text = "Edit " + when (editingNode.nodeType) {
					NodeType.ACTION -> "action"
					NodeType.TRIGGER -> "trigger"
					NodeType.FUNCTION -> "function"
				},
				style = LocalTheme.typography.subTitle,
				color = LocalTheme.colorScheme.onBackground
			)
			SettingsResetButton(resetDefaultConfig)
		}

		editingNodeData.inputDefinitions.forEach {
			val name = it.key
			val inputDef = it.value
			val currentValue = editingNode.input[name]
			NodeEditField(name, inputDef, currentValue) { setNodeConfig(name, it) }
		}
	}
}

@Composable
fun NodeEditField(
	name: String,
	inputDefinition: NodeInputDefinition<*>,
	currentValue: ConfigurationEntry<*>?, // Should be T but kotlin is just dumb
	onValueChanged: (ConfigurationEntry<*>) -> Unit
) {
	when (inputDefinition.defaultValue) {
		is ConfigurationEntry.BOOL -> {
			if (currentValue != null && currentValue !is ConfigurationEntry.BOOL) return
			UserSettingsItem(
				item = currentValue ?: inputDefinition.defaultValue,
				displayName = name,
				description = inputDefinition.description,
				validate = inputDefinition.validate as (ConfigurationEntry.BOOL) -> ValidationResult,
				onValueChanged = onValueChanged
			)
		}

		is ConfigurationEntry.TEXT -> {
			if (currentValue != null && currentValue !is ConfigurationEntry.TEXT) return
			UserSettingsItem(
				item = currentValue,
				defaultItem = inputDefinition.defaultValue,
				displayName = name,
				description = inputDefinition.description,
				validate = inputDefinition.validate as (ConfigurationEntry.TEXT) -> ValidationResult,
				onValueChanged = onValueChanged
			)
		}

		is ConfigurationEntry.NUMBER -> {
			if (currentValue != null && currentValue !is ConfigurationEntry.NUMBER) return
			UserSettingsItem(
				item = currentValue,
				defaultItem = inputDefinition.defaultValue,
				displayName = name,
				description = inputDefinition.description,
				validate = inputDefinition.validate as (ConfigurationEntry.NUMBER) -> ValidationResult,
				onValueChanged = onValueChanged
			)
		}

		else -> {}
	}
}