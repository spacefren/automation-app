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
	val functions: List<FunctionDef>,
	val entryPoint: List<ActionDef>,
)

@Serializable
data class TriggerDef(
	val name: String,
	val id: String = UUID.randomUUID().toString(),
	val input: Map<String, ConfigurationEntry<*>>,
	val output: Map<String, ConfigurationEntry<*>>,
	val content: List<ActionDef>,
)

@Serializable
data class FunctionDef(
	val name: String,
	val id: String = UUID.randomUUID().toString(),
	val content: List<ActionDef>,
)

@Serializable
data class ActionDef(
	val name: String,
	val id: String = UUID.randomUUID().toString(),
	val input: Map<String, ConfigurationEntry<*>>,
	val output: Map<String, ConfigurationEntry<*>>,
)