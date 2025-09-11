package com.jxd20072.automationapp.core.executor

import android.content.Context
import com.jxd20072.automationapp.core.data.AutomationLogEntry
import com.jxd20072.automationapp.core.data.LogLevel
import com.jxd20072.automationapp.core.data.model.VariableDef
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.Instant
import java.util.UUID

class ExecutionContext(
	val appContext: Context,
	initialVariables: Set<VariableDef>,
) {
	var executionStart: Instant? = null
	var executionStop: Instant? = null

	fun onStartExecution() {
		executionStart = Instant.now()
	}

	fun onStopExecution() {
		executionStop = Instant.now()
	}

	private val variables = initialVariables.toMutableSet()

	fun setVariable(variable: VariableDef) {
		variables.removeIf { it.id == variable.id }
		variables.add(variable)
	}

	fun getVariable(name: String): VariableDef? {
		return variables.find { it.name == name }
	}

	private val _logsState = MutableStateFlow(emptyList<AutomationLogEntry>())
	val logs = _logsState.asStateFlow()

	fun log(level: LogLevel, message: String) {
		_logsState.update {
			it + AutomationLogEntry
				.newBuilder()
				.setId(UUID.randomUUID().toString())
				.setTime(Instant.now().toString())
				.setLevel(level)
				.setMessage(message)
				.build()
		}
	}
}
