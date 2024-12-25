package com.jxd20072.automationapp.core.executor

import com.jxd20072.automationapp.core.data.model.VariableDef
import java.time.Instant

private const val TAG = "APP_Executor/Context"

class ExecutionFlowContext(
	val scope: String,
	initialVariables: Set<VariableDef>,
) {
	private val variables = initialVariables.toMutableSet()
	val executionStart = Instant.now()

	fun setVariable(name: String, value: String) {
		val variable = variables.find { it.name == name }
			?: throw Exception("Variable not found: $name")
		variable.value = value
	}

	fun getVariables(): Set<VariableDef> {
		return variables
	}

	fun getVariable(name: String): VariableDef? {
		return variables.find { it.name == name }
	}
}