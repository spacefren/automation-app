package com.jxd20072.automationapp.utils.specs

import com.jxd20072.automationapp.core.data.model.Value

fun getValueName(value: Value): String {
    return when (value) {
        is Value.BOOL -> "bool"
        is Value.TEXT -> "text"
        is Value.FLOAT -> "float"
        is Value.INT -> "int"
        is Value.VARIABLE -> "variable"
        is Value.CHOICE -> "choice"
        is Value.EXPRESSION -> "expression"
    }
}

fun getVariableDefaultValue(name: String): Value? {
    return when (name) {
        "text" -> Value.TEXT("")
        "bool" -> Value.BOOL(true)
        "float" -> Value.FLOAT(0f)
        "int" -> Value.INT(0)
        "expression" -> Value.EXPRESSION("")
        else -> null
    }
}