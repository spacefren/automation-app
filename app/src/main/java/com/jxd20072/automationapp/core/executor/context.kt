package com.jxd20072.automationapp.core.executor

import android.content.Context
import androidx.compose.runtime.Immutable
import com.jxd20072.automationapp.core.data.model.VariableDef
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.Instant

@Immutable
enum class LogLevel { INFO, WARNING, ERROR }

@Immutable
data class LogEntry(
	val level: LogLevel,
	val time: Instant,
	val message: String,
)

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

	private val _logsState = MutableStateFlow(emptyList<LogEntry>())
	val logs = _logsState.asStateFlow()

	fun log(entry: LogEntry) {
		_logsState.update { it + entry }
	}
}
