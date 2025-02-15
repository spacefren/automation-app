package com.jxd20072.automationapp.core.data.repository.automations

import com.jxd20072.automationapp.core.data.model.ActionNodeDef
import com.jxd20072.automationapp.core.data.model.AutomationDef
import com.jxd20072.automationapp.core.data.model.AutomationDefMetadata
import com.jxd20072.automationapp.core.data.model.TriggerDef
import com.jxd20072.automationapp.core.data.model.VariableDef
import java.util.UUID

data class AutomationDraft(
	val metadata: AutomationDefMetadata,
	val variables: Set<VariableDef>,
	val triggers: List<TriggerNodeDraft>,
	val functions: List<FunctionNodeDraft>
)

data class ActionNodeDraft(
	val name: String,
	val id: String = UUID.randomUUID().toString(),
	val input: Map<String, String>,
)

data class FunctionNodeDraft(
	val name: String,
	val id: String = UUID.randomUUID().toString(),
	val input: Map<String, String>,
	val content: List<ActionNodeDraft>,
	val output: Map<String, String>
)

data class TriggerNodeDraft(
	val name: String,
	val id: String = UUID.randomUUID().toString()
)

fun createActionNodeDraft(actionNodeDef: ActionNodeDef): ActionNodeDraft {
	return ActionNodeDraft(
		input = actionNodeDef.input,
		name = actionNodeDef.name
	)
}

fun createTriggerNodeDraft(triggerNodeDef: TriggerDef): TriggerNodeDraft {
	return TriggerNodeDraft(
		name = triggerNodeDef.name,
	)
}

fun createAutomationDraft(automationDef: AutomationDef): AutomationDraft {
	return AutomationDraft(
		metadata = automationDef.metadata,
		variables = automationDef.variables,
		triggers = automationDef.triggers.map { createTriggerNodeDraft(it) },
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