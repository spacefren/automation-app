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
import com.jxd20072.automationapp.core.data.model.NodeInputEntry
import com.jxd20072.automationapp.core.nodes.props.NodeInputDefinition
import com.jxd20072.automationapp.ui.editor.viewmodel.bottomSheet.NodeEditState
import com.jxd20072.automationapp.ui.shared.SettingsResetButton
import com.jxd20072.automationapp.ui.theme.LocalTheme
import com.jxd20072.automationapp.ui.userSettings.settingsItems.UserSettingsItem

@Composable
fun EditorNodeEditScreen(
	nodeEditState: NodeEditState,
	resetDefaultConfig: () -> Unit,
) {
	if (nodeEditState is NodeEditState.Editing) {
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
					text = "Edit node",
					style = LocalTheme.typography.subTitle,
					color = LocalTheme.colorScheme.onBackground
				)
				SettingsResetButton(resetDefaultConfig)
			}

			nodeEditState.actionData.inputDefinitions.forEach {
				val name = it.key
				val value = it.value
				NodeEditField(name, value, value.defaultValue, { })
			}
		}
	}
}

@Composable
fun NodeEditField(
	name: String,
	inputDefinition: NodeInputDefinition<*>,
	currentValue: NodeInputEntry<*>, // Should be T but kotlin is just dumb
	onValueChanged: (NodeInputEntry<*>) -> Unit
) {
	when (currentValue) {
		is NodeInputEntry.BOOL -> {
			UserSettingsItem(
				item = currentValue,
				displayName = name,
				description = inputDefinition.description,
				validate = inputDefinition.validate as (NodeInputEntry.BOOL) -> Boolean,
				onValueChanged = onValueChanged
			)
		}

		is NodeInputEntry.TEXT -> {
			UserSettingsItem(
				item = currentValue,
				displayName = name,
				description = inputDefinition.description,
				validate = inputDefinition.validate as (NodeInputEntry.TEXT) -> Boolean,
				onValueChanged = onValueChanged
			)
		}

		else -> {}
	}
}