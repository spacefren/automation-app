package com.jxd20072.automationapp.core.data.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class AutomationDefMetadata(
	val version: Int
)

@Serializable
sealed interface VariableDef<T> {
	val name: String
	val value: T

	@Serializable
	data class TEXT(
		override val value: String,
		override val name: String
	) : VariableDef<String>

	@Serializable
	data class BOOL(
		override val value: Boolean,
		override val name: String
	) : VariableDef<Boolean>

	@Serializable
	data class NUMBER(
		override val value: Int,
		override val name: String
	) : VariableDef<Int>
}

@Serializable
data class AutomationDef(
	val metadata: AutomationDefMetadata,
	val variables: Set<VariableDef<*>>,
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
	val input: Map<String, ConfigurationValue<*>> = emptyMap(),
	val output: Map<String, ConfigurationValue<*>> = emptyMap(),
	val content: List<NodeDef> = emptyList(),
)