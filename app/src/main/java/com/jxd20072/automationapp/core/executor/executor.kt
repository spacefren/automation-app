package com.jxd20072.automationapp.core.executor

import android.content.Context
import com.jxd20072.automationapp.core.data.model.AutomationDef
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.UUID

class AutomationExecutor(
	private val automationDef: AutomationDef,
	private val context: Context
) {
	val id = UUID.randomUUID().toString()
	private val executionFlow = ExecutionFlow(
		automationDef = automationDef,
	)
	private val executionContext = ExecutionContext(
		initialVariables = automationDef.variables,
		appContext = context
	)

	val logs = executionContext.logs

	private var job: Job? = null

	fun start(onEndCallback: (() -> Unit)? = null) {
		job = CoroutineScope(Dispatchers.Default).launch {
			executionFlow.startExecution(executionContext)
		}
		job?.invokeOnCompletion { onEndCallback?.invoke() }
	}

	fun stop() {
		job?.cancel()
	}
}
