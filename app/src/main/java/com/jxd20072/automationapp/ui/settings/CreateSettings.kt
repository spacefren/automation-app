package com.jxd20072.automationapp.ui.settings

import com.jxd20072.automationapp.core.data.AppSettings
import com.jxd20072.automationapp.core.data.model.ConfigurationValue

fun createUserEditableSettings(appSettings: AppSettings): UserEditableSettings {
    return UserEditableSettings(
        autoSave = ConfigurationValue.BOOL(appSettings.autoSave),
        autoOpenKeyboard = ConfigurationValue.BOOL(appSettings.autoOpenKeyboard)
    )
}