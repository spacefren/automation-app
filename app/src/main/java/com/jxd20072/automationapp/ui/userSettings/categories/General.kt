package com.jxd20072.automationapp.ui.userSettings.categories

import androidx.compose.runtime.Composable
import com.jxd20072.automationapp.ui.userSettings.UserSettingsUiState
import com.jxd20072.automationapp.ui.userSettings.settingsItems.UserSettingsItem

@Composable
fun GeneralSettingsCategory(
	userSettingsUiState: UserSettingsUiState.Success,
	onAutoSaveChanged: (newVal: Boolean) -> Unit
) {
	SettingsCategory(
		name = "General"
	) {
		UserSettingsItem(
			item = userSettingsUiState.settings.autoSave,
			displayName = "Auto-save automations",
			description = "Automatically save changes to automations",
			onValueChanged = onAutoSaveChanged
		)
	}
}