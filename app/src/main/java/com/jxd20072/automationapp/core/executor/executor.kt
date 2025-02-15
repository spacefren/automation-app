package com.jxd20072.automationapp.core.executor

import com.jxd20072.automationapp.core.data.repository.automations.AutomationDraft

fun executeAutomation(automationDef: AutomationDraft) {
	val entryPoint = automationDef.functions.find { it.name == "OnStart" }
	if (entryPoint == null) return

	val executionFlow = ExecutionFlow(
		actionDefinitions = entryPoint.content,
		initialVariables = automationDef.variables
	)
	executionFlow.startExecution()
}