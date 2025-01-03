package com.jxd20072.automationapp.core.executor

import com.jxd20072.automationapp.core.data.model.AutomationDef

fun executeAutomation(automationDef: AutomationDef) {
	val executionFlow = ExecutionFlow(
		actionDefinitions = automationDef.entryPoint.content,
		initialVariables = automationDef.variables
	)
	executionFlow.startExecution()
}