package com.jxd20072.automationapp.core.executor

import com.jxd20072.automationapp.core.data.model.AutomationDef

fun executeAutomation(automation: AutomationDef) {
	val executionFlow = ExecutionFlow(
		actionDefinitions = automation.entryPoint,
		initialVariables = automation.variables
	)
	executionFlow.startExecution()
}