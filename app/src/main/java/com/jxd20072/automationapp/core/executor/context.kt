package com.jxd20072.automationapp.core.executor

import android.content.Context
import com.jxd20072.automationapp.core.data.AutomationLogEntry
import com.jxd20072.automationapp.core.data.LogLevel
import com.jxd20072.automationapp.core.data.model.VariableDef
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID

class ExecutionContext(
    val appContext: Context,
    val coroutineScope: CoroutineScope,
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

    fun getVariable(id: String): VariableDef? {
        return variables.find { it.id == id }
    }

    val logs = MutableSharedFlow<AutomationLogEntry>()

    fun log(level: LogLevel, message: String) {
        coroutineScope.launch {
            logs.emit(
                AutomationLogEntry
                    .newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setTime(Instant.now().toString())
                    .setLevel(level)
                    .setMessage(message)
                    .build()
            )
        }
    }
}
