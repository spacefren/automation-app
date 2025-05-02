package com.jxd20072.automationapp.ui.userSettings.categories

import androidx.compose.runtime.Composable
import com.jxd20072.automationapp.ui.userSettings.UserSettingsUiState
import com.jxd20072.automationapp.ui.userSettings.settingsItems.UserSettingsItem

@Composable
fun GeneralSettingsCategory(
	userSettingsUiState: UserSettingsUiState.Success,
	onAutoSaveChanged: (newVal: Boolean) -> Unit,
	onAutoOpenKeyboardChanged: (newVal: Boolean) -> Unit,
	openAutomationsFolder: () -> Unit
) {
	SettingsCategory(
		name = "General"
	) {
		UserSettingsItem(
			item = userSettingsUiState.settings.autoSave,
			displayName = "Auto-save automations",
			onValueChanged = onAutoSaveChanged
		)
		UserSettingsItem(
			item = userSettingsUiState.settings.autoOpenKeyboard,
			displayName = "Automatically open keyboard",
			description = "When navigating to a screen with a text field, automatically show the keyboard",
			onValueChanged = onAutoOpenKeyboardChanged
		)
		UserSettingsItem(
			displayName = "Open automations folder",
			onActivate = openAutomationsFolder
		)
	}
}