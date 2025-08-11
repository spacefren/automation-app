package com.jxd20072.automationapp.core.executor

import android.content.Context
import com.jxd20072.automationapp.core.data.model.AutomationDef
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun executeAutomation(automation: AutomationDef, context: Context) {
	val executionFlow = ExecutionFlow(
		automationDef = automation,
		appContext = context
	)
	CoroutineScope(Dispatchers.Default).launch {
		executionFlow.startExecution()
	}
}