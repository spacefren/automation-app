package com.jxd20072.automationapp.ui.settings

import com.jxd20072.automationapp.core.data.AppSettings
import com.jxd20072.automationapp.core.data.model.Value

fun createUserEditableSettings(appSettings: AppSettings): UserEditableSettings {
    return UserEditableSettings(
        autoSave = Value.BOOL(appSettings.autoSave),
        autoOpenKeyboard = Value.BOOL(appSettings.autoOpenKeyboard)
    )
}