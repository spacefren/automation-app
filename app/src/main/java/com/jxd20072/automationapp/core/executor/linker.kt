package com.jxd20072.automationapp.core.executor

import com.jxd20072.automationapp.core.data.model.VariableDef
import com.jxd20072.automationapp.core.data.model.VariableDefType

fun linkInput(
	type: VariableDefType,
	value: String,
	context: ExecutionFlowContext
): Any? {
	return readVariable(VariableDef.withType("", value, type), context)
}