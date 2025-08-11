package com.jxd20072.automationapp.core.executor

import android.content.Context
import com.jxd20072.automationapp.core.data.model.VariableDef
import java.time.Instant

class ExecutionFlowContext(
	val appContext: Context,
	initialVariables: Set<VariableDef>,
) {
	private val variables = initialVariables.toMutableSet()
	var executionStart: Instant? = null

	fun setVariable(variable: VariableDef) {
		variables.removeIf { it.id == variable.id }
		variables.add(variable)
	}

	fun onStartExecution() {
		executionStart = Instant.now()
	}

	fun getVariables(): Set<VariableDef> {
		return variables
	}

	fun getVariable(name: String): VariableDef? {
		return variables.find { it.name == name }
	}
}
