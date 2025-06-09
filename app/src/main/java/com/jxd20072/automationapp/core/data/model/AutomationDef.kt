package com.jxd20072.automationapp.core.data.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class AutomationDefMetadata(
	val version: Int
)

@Serializable
sealed interface VariableDef<T> {
	val value: T

	@Serializable
	data class TEXT(
		override val value: String
	) : VariableDef<String>

	@Serializable
	data class BOOL(
		override val value: Boolean
	) : VariableDef<Boolean>

	@Serializable
	data class NUMBER(
		override val value: Int
	) : VariableDef<Int>

	@Serializable
	data class VARIABLE(
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
sealed class NodeInputEntry<T> {
	abstract val value: T

	@Serializable
	data class TEXT(
		override val value: String
	) : NodeInputEntry<String>()

	@Serializable
	data class BOOL(
		override val value: Boolean
	) : NodeInputEntry<Boolean>()

	@Serializable
	data class NUMBER(
		override val value: Int
	) : NodeInputEntry<Int>()

	@Serializable
	data class CHOOSE(
		override val value: String
	) : NodeInputEntry<String>()
}