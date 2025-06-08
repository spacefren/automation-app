package com.jxd20072.automationapp.ui.editor.editorBottomSheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.Text
import com.jxd20072.automationapp.ui.editor.viewmodel.bottomSheet.NodeEditState
import com.jxd20072.automationapp.ui.shared.SettingsResetButton
import com.jxd20072.automationapp.ui.theme.LocalTheme

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
				Text(
					text = it.key,
					style = LocalTheme.typography.body,
					color = LocalTheme.colorScheme.onBackground
				)
			}
		}
	}
}