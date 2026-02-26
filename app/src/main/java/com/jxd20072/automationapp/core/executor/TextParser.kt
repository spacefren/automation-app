package com.jxd20072.automationapp.core.executor

import com.jxd20072.automationapp.core.data.model.Value
import com.jxd20072.automationapp.core.data.model.VariableDef
import com.notkamui.keval.keval

fun substituteVariables(text: String, variables: Set<VariableDef>): String {
    var finalText = text
    variables.forEach {
        finalText = finalText.replace("@${it.id}", it.value.valueAsString())
        finalText = finalText.replace("@${it.name}", it.value.valueAsString())
    }

    return finalText
}

inline fun <reified T : Value> parseTextAsValue(text: String, variables: Set<VariableDef>): T? {
    val reducedText = substituteVariables(text, variables)

    return when (T::class) {
        Value.TEXT::class -> Value.TEXT(reducedText)
        Value.INT::class -> Value.INT(reducedText.keval().toInt())
        Value.FLOAT::class -> Value.FLOAT(reducedText.keval().toFloat())
        Value.BOOL::class -> Value.BOOL(reducedText.toBoolean())
        Value.VARIABLE::class -> Value.VARIABLE(variables.find { it.id == reducedText }?.id ?: "")
        Value.CHOICE::class -> Value.CHOICE(reducedText)
        else -> null
    } as T?
}