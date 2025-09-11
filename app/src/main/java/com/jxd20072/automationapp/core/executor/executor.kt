package com.jxd20072.automationapp.core.executor

import android.content.Context
import android.util.Log
import com.jxd20072.automationapp.core.data.model.AutomationDef
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID

private const val TAG = "APP_Executor"

class AutomationExecutor(
	private val automationDef: AutomationDef,
	private val context: Context
) {
	val id = UUID.randomUUID().toString()
	val executorScope = CoroutineScope(Dispatchers.Default)
	private val executionFlow = ExecutionFlow(
		automationDef = automationDef,
	)
	private val executionContext = ExecutionContext(
		initialVariables = automationDef.variables,
		appContext = context
	)

	val logs = executionContext.logs

	var job: Job? = null

	fun start(onEndCallback: (() -> Unit)? = null) {
		Log.i(TAG, "STARTING EXECUTION")
		job = executorScope.launch {
			executionFlow.startExecution(executionContext)
			delay(100L) // Let handlers react to the last node before closing the coroutine
		}

		job?.invokeOnCompletion {
			onEndCallback?.invoke()

			Log.i(
				TAG, "EXECUTION ENDED in ${
					Instant.now().toEpochMilli() - executionContext.executionStart!!.toEpochMilli()
				} ms"
			)
		}
	}

	fun stop() {
		job?.cancel()
	}
}
