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
	val topLevel: List<NodeDef>,
)

@Serializable
enum class NodeType {
	ACTION, TRIGGER, FUNCTION
}

@Serializable
data class NodeDef(
	val name: String,
	val nodeType: NodeType,
	val id: String = UUID.randomUUID().toString(),
	val input: Map<String, ConfigurationEntry<*>> = emptyMap(),
	val output: Map<String, ConfigurationEntry<*>> = emptyMap(),
	val content: List<NodeDef> = emptyList(),
)