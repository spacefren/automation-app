package com.jxd20072.automationapp.core.data.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class AutomationDefMetadata(
	val version: Int
)

@Serializable
enum class VariableDefType {
	TEXT, BOOL, NUMBER, VARIABLE
}

//@Serializable
//sealed class VariableDef(val variableType: VariableDefType) {
//	abstract val name: String
//	abstract var value: String
//
//	@Serializable
//	class TEXT(
//		override val name: String,
//		override var value: String
//	) : VariableDef(VariableDefType.TEXT)
//
//	@Serializable
//	class BOOL(
//		override val name: String,
//		override var value: String
//	) : VariableDef(VariableDefType.BOOL)
//
//	@Serializable
//	class NUMBER(
//		override val name: String,
//		override var value: String
//	) : VariableDef(VariableDefType.NUMBER)
//
//	@Serializable
//	class VARIABLE(
//		override val name: String,
//		override var value: String
//	) : VariableDef(VariableDefType.VARIABLE)
//
//	companion object {
//		fun withType(name: String, value: String, type: VariableDefType): VariableDef {
//			return when (type) {
//				VariableDefType.TEXT -> TEXT(name, value)
//				VariableDefType.BOOL -> BOOL(name, value)
//				VariableDefType.NUMBER -> NUMBER(name, value)
//				VariableDefType.VARIABLE -> VARIABLE(name, value)
//			}
//		}
//	}
//}

@Serializable
sealed interface VariableDef<T> {
	val value: T

	@Serializable
	class TEXT(
		override val value: String
	) : VariableDef<String>

	@Serializable
	class BOOL(
		override val value: Boolean
	) : VariableDef<Boolean>

	@Serializable
	class NUMBER(
		override val value: Int
	) : VariableDef<Int>

	@Serializable
	class VARIABLE(
		override val value: String
	) : VariableDef<String>
}

@Serializable
data class AutomationDef(
	val metadata: AutomationDefMetadata,
	val variables: Map<String, VariableDef<*>>,
	val triggers: List<TriggerDef>,
	val functions: List<FunctionNodeDef>,
	val entryPoint: List<ActionNodeDef>,
)

@Serializable
data class TriggerDef(
	val id: String = UUID.randomUUID().toString(),
	val name: String,
)

@Serializable
data class FunctionNodeDef(
	val name: String,
	val id: String = UUID.randomUUID().toString(),
	val content: List<ActionNodeDef>,
)

@Serializable
data class ActionNodeDef(
	val name: String,
	val id: String = UUID.randomUUID().toString(),
	val input: Map<String, NodeInputEntry<*>>,
)

@Serializable
sealed interface NodeInputEntry<T> {
	val value: T

	@Serializable
	data class TEXT(
		override val value: String
	) : NodeInputEntry<String>

	@Serializable
	data class BOOL(
		override val value: Boolean
	) : NodeInputEntry<Boolean>

	@Serializable
	data class NUMBER(
		override val value: Int
	) : NodeInputEntry<Int>

	@Serializable
	data class CHOOSE(
		val choices: Set<String>,
		override val value: String
	) : NodeInputEntry<String>
}