package com.jxd20072.automationapp.core.data.repository.automations

//data class AutomationDraft(
//	val metadata: AutomationDefMetadata,
//	val variables: Set<VariableDef>,
//	val triggers: List<TriggerDraft>,
//	val functions: List<FunctionNodeDraft>
//)
//
//data class ActionNodeDraft(
//	val name: String,
//	val id: String = UUID.randomUUID().toString(),
//	val input: Map<String, NodeInputEntry<*>>,
//)
//
//data class FunctionNodeDraft(
//	val name: String,
//	val id: String = UUID.randomUUID().toString(),
//	val input: Map<String, NodeInputEntry<*>>,
//	val content: List<ActionNodeDraft>,
//	val output: Map<String, NodeInputEntry<*>>,
//)
//
//data class TriggerDraft(
//	val name: String,
//	val id: String = UUID.randomUUID().toString()
//)
//
//fun createActionNodeDraft(actionNodeDef: ActionNodeDef): ActionNodeDraft {
//	return ActionNodeDraft(
//		input = actionNodeDef.input,
//		name = actionNodeDef.name
//	)
//}
//
//fun createTriggerNodeDraft(triggerDef: TriggerDef): TriggerDraft {
//	return TriggerDraft(
//		name = triggerDef.name,
//	)
//}
//
//fun createAutomationDraft(automationDef: AutomationDef): AutomationDraft {
//	return AutomationDraft(
//		metadata = automationDef.metadata,
//		variables = automationDef.variables,
//		triggers = automationDef.triggers.map { createTriggerNodeDraft(it) },
//		functions = automationDef.functions.map { functionDef ->
//			FunctionNodeDraft(
//				name = functionDef.name,
//				input = functionDef.input,
//				content = functionDef.content.map { createActionNodeDraft(it) },
//				output = functionDef.output
//			)
//		}
//	)
//}
//
//fun createActionNodeDef(actionNodeDraft: ActionNodeDraft): ActionNodeDef {
//	return ActionNodeDef(
//		name = actionNodeDraft.name,
//		input = actionNodeDraft.input
//	)
//}
//
//fun createTriggerDef(triggerDraft: TriggerDraft): TriggerDef {
//	return TriggerDef(
//		name = triggerDraft.name
//	)
//}
//
//fun createFunctionNodeDef(functionNodeDraft: FunctionNodeDraft): FunctionNodeDef {
//	return FunctionNodeDef(
//		name = functionNodeDraft.name,
//		content = functionNodeDraft.content.map { createActionNodeDef(it) },
//		input = functionNodeDraft.input,
//		output = functionNodeDraft.output
//	)
//}
//
//fun createAutomationDef(automationDraft: AutomationDraft): AutomationDef {
//	return AutomationDef(
//		metadata = automationDraft.metadata,
//		variables = automationDraft.variables,
//		triggers = automationDraft.triggers.map { createTriggerDef(it) },
//		functions = automationDraft.functions.map { createFunctionNodeDef(it) }
//	)
//}