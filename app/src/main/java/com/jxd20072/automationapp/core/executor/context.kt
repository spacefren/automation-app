package com.jxd20072.automationapp.core.executor

import com.jxd20072.automationapp.core.data.model.VariableDef
import java.time.Instant

class ExecutionFlowContext(
	val scope: String,
	initialVariables: Map<String, VariableDef<*>>,
) {
	private val variables = initialVariables.toMutableMap()
	lateinit var executionStart: Instant

	fun setVariable(name: String, newVar: VariableDef<*>) {
		variables[name] = newVar
	}

	fun onStartExecution() {
		executionStart = Instant.now()
	}

	fun getVariables(): Map<String, VariableDef<*>> {
		return variables
	}

	fun getVariable(name: String): VariableDef<*>? {
		return variables[name]
	}
}
