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
sealed class VariableDef(val variableType: VariableDefType) {
	abstract val name: String
	abstract var value: String

	@Serializable
	class TEXT(
		override val name: String,
		override var value: String
	) : VariableDef(VariableDefType.TEXT)

	@Serializable
	class BOOL(
		override val name: String,
		override var value: String
	) : VariableDef(VariableDefType.BOOL)

	@Serializable
	class NUMBER(
		override val name: String,
		override var value: String
	) : VariableDef(VariableDefType.NUMBER)

	@Serializable
	class VARIABLE(
		override val name: String,
		override var value: String
	) : VariableDef(VariableDefType.VARIABLE)

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
	val triggers: List<TriggerDef>,
	val functions: List<FunctionNodeDef>
)

@Serializable
data class TriggerDef(
	val name: String,
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