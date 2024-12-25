package com.jxd20072.automationapp.core.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AutomationDefMetadata(
	val version: Int
)

@Serializable
enum class VariableDefType {
	TEXT, BOOL, NUMBER, VARIABLE
}

@Serializable
sealed class VariableDef(val name: String, var value: String, val type: VariableDefType) {
	class TEXT(name: String, value: String) :
		VariableDef(name, value, VariableDefType.TEXT)

	class BOOL(name: String, value: String) :
		VariableDef(name, value, VariableDefType.BOOL)

	class NUMBER(name: String, value: String) :
		VariableDef(name, value, VariableDefType.NUMBER)

	class VARIABLE(name: String, value: String) :
		VariableDef(name, value, VariableDefType.VARIABLE)

	companion object {
		fun withType(name: String, value: String, type: VariableDefType): VariableDef {
			return when (type) {
				VariableDefType.TEXT -> TEXT(name, value)
				VariableDefType.BOOL -> BOOL(name, value)
				VariableDefType.NUMBER -> NUMBER(name, value)
				VariableDefType.VARIABLE -> VARIABLE(name, value)
			}
		}
	}
}

@Serializable
data class AutomationDef(
	val metadata: AutomationDefMetadata,
	val variables: Set<VariableDef>,
	val entryPoint: EntryPointNodeDef,
	val triggers: List<TriggerNodeDef>,
	val functions: List<FunctionNodeDef>
)

@Serializable
data class EntryPointNodeDef(
	val content: List<ActionNodeDef>
)

@Serializable
data class TriggerNodeDef(
	val name: String,
	val content: List<ActionNodeDef>
)

@Serializable
data class FunctionNodeDef(
	val name: String,
	val input: Map<String, String>,
	val content: List<ActionNodeDef>,
	val output: Map<String, String>
)

@Serializable
data class ActionNodeDef(
	val input: Map<String, String>,
	val name: String,
)