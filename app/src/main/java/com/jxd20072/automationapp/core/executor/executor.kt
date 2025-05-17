package com.jxd20072.automationapp.core.executor

import com.jxd20072.automationapp.core.data.model.AutomationDef

fun executeAutomation(automation: AutomationDef) {
	val entryPoint = automation.functions.find { it.name == "OnStart" }
	if (entryPoint == null) return

	val executionFlow = ExecutionFlow(
		actionDefinitions = entryPoint.content,
		initialVariables = automation.variables
	)
	executionFlow.startExecution()
}