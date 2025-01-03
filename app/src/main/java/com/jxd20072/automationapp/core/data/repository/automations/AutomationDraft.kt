package com.jxd20072.automationapp.core.data.repository.automations

import com.jxd20072.automationapp.core.data.model.ActionNodeDef
import com.jxd20072.automationapp.core.data.model.AutomationDef
import com.jxd20072.automationapp.core.data.model.AutomationDefMetadata
import com.jxd20072.automationapp.core.data.model.TriggerNodeDef
import com.jxd20072.automationapp.core.data.model.VariableDef
import java.util.UUID

data class AutomationDraft(
	val metadata: AutomationDefMetadata,
	val variables: Set<VariableDef>,
	val entryPoint: List<ActionNodeDraft>,
	val triggers: List<TriggerNodeDef>,
	val functions: List<FunctionNodeDraft>
)

data class ActionNodeDraft(
	val id: String = UUID.randomUUID().toString(),
	val input: Map<String, String>,
	val name: String,
)

data class FunctionNodeDraft(
	val name: String,
	val id: String = UUID.randomUUID().toString(),
	val input: Map<String, String>,
	val content: List<ActionNodeDraft>,
	val output: Map<String, String>
)

fun createActionNodeDraft(actionNodeDef: ActionNodeDef): ActionNodeDraft {
	return ActionNodeDraft(
		input = actionNodeDef.input,
		name = actionNodeDef.name
	)
}

fun createAutomationDraft(automationDef: AutomationDef): AutomationDraft {
	return AutomationDraft(
		metadata = automationDef.metadata,
		variables = automationDef.variables,
		entryPoint = automationDef.entryPoint.map { createActionNodeDraft(it) },
		triggers = automationDef.triggers,
		functions = automationDef.functions.map { functionDef ->
			FunctionNodeDraft(
				name = functionDef.name,
				input = functionDef.input,
				content = functionDef.content.map { createActionNodeDraft(it) },
				output = functionDef.output
			)
		}
	)
}