package com.jxd20072.automationapp.utils

import com.jxd20072.automationapp.core.data.model.ConfigurationValue

fun getPrefixForConfigurationValue(value: ConfigurationValue<*>): String {
    return when(value) {
        is ConfigurationValue.VARIABLE -> "@"
        else -> "*"
    }
}