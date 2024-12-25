package com.jxd20072.automationapp.core.executor

import com.jxd20072.automationapp.core.data.model.VariableDef
import com.jxd20072.automationapp.core.data.model.VariableDefType

private const val TAG = "APP_Executor/Linker"

fun linkInput(
	name: String,
	type: VariableDefType,
	value: String,
	context: ExecutionFlowContext
): Any? {
	return readVariable(VariableDef.withType(name, value, type), context)
}