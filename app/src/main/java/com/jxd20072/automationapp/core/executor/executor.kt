package com.jxd20072.automationapp.core.executor

import com.jxd20072.automationapp.core.data.repository.automations.AutomationDraft

fun executeAutomation(automationDef: AutomationDraft) {
	val executionFlow = ExecutionFlow(
		actionDefinitions = automationDef.entryPoint,
		initialVariables = automationDef.variables
	)
	executionFlow.startExecution()
}