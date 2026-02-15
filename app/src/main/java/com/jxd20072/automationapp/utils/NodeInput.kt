package com.jxd20072.automationapp.utils

import com.jxd20072.automationapp.core.data.model.Value
import com.jxd20072.automationapp.core.data.model.VariableDef
import com.jxd20072.automationapp.core.nodes.NodeDescriptionPiece

fun getNodeInputPiece(
    variables: Set<VariableDef>,
    value: Value?
): NodeDescriptionPiece {
    return when (value) {
        is Value.VARIABLE -> NodeDescriptionPiece.VariableInput(
            variables.find { it.id == value.id }?.name ?: "Unknown Variable"
        )

        else -> NodeDescriptionPiece.Input(value?.valueAsString() ?: "")
    }
}