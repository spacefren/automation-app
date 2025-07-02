package com.jxd20072.automationapp.ui.userSettings

import com.jxd20072.automationapp.core.data.UserSettings
import com.jxd20072.automationapp.core.data.model.ConfigurationValue

fun createUserEditableSettings(userSettings: UserSettings): UserEditableSettings {
	return UserEditableSettings(
		autoSave = ConfigurationValue.BOOL(userSettings.autoSave),
		autoOpenKeyboard = ConfigurationValue.BOOL(userSettings.autoOpenKeyboard)
	)
}