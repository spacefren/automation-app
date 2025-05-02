package com.jxd20072.automationapp.core.executor

import com.jxd20072.automationapp.core.data.model.AutomationDef
import com.jxd20072.automationapp.core.data.repository.automations.AutomationDraft
import com.jxd20072.automationapp.core.data.repository.automations.createAutomationDraft

fun executeAutomation(automation: AutomationDraft) {
	val entryPoint = automation.functions.find { it.name == "OnStart" }
	if (entryPoint == null) return

	val executionFlow = ExecutionFlow(
		actionDefinitions = entryPoint.content,
		initialVariables = automation.variables
	)
	executionFlow.startExecution()
}

fun executeAutomation(automation: AutomationDef) {
	executeAutomation(createAutomationDraft(automation))
}