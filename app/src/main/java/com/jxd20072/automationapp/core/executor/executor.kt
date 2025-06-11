package com.jxd20072.automationapp.core.executor

import com.jxd20072.automationapp.core.data.model.AutomationDef
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun executeAutomation(automation: AutomationDef) {
	val executionFlow = ExecutionFlow(
		automationDef = automation
	)
	CoroutineScope(Dispatchers.Default).launch {
		executionFlow.startExecution()
	}
}