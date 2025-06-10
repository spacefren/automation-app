package com.jxd20072.automationapp.ui.userSettings

import com.jxd20072.automationapp.core.data.UserSettings
import com.jxd20072.automationapp.core.data.model.ConfigurationEntry

fun createUserEditableSettings(userSettings: UserSettings): UserEditableSettings {
	return UserEditableSettings(
		autoSave = ConfigurationEntry.BOOL(userSettings.autoSave),
		autoOpenKeyboard = ConfigurationEntry.BOOL(userSettings.autoOpenKeyboard)
	)
}