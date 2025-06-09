package com.jxd20072.automationapp.ui.userSettings

import com.jxd20072.automationapp.core.data.UserSettings
import com.jxd20072.automationapp.core.data.model.NodeInputEntry

fun createUserEditableSettings(userSettings: UserSettings): UserEditableSettings {
	return UserEditableSettings(
		autoSave = NodeInputEntry.BOOL(userSettings.autoSave),
		autoOpenKeyboard = NodeInputEntry.BOOL(userSettings.autoOpenKeyboard)
	)
}