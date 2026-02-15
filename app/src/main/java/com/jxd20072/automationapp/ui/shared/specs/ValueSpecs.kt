package com.jxd20072.automationapp.ui.shared.specs

import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.core.data.model.Value

fun getImageForValueType(value: Value): Int {
    return when (value) {
        is Value.BOOL -> R.drawable.round_bool_24
        is Value.TEXT -> R.drawable.round_text_24
        is Value.FLOAT -> R.drawable.round_number_24
        is Value.INT -> R.drawable.round_number_24
        else -> TODO()
    }
}