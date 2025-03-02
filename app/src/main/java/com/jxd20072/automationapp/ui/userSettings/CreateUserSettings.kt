package com.jxd20072.automationapp.ui.userSettings

import com.jxd20072.automationapp.core.data.UserSettings

fun createUserEditableSettings(userSettings: UserSettings): UserEditableSettings {
	return UserEditableSettings(
		autoSave = UserSettingsEntry(userSettings.autoSave),
		autoOpenKeyboard = UserSettingsEntry(userSettings.autoOpenKeyboard)
	)
}