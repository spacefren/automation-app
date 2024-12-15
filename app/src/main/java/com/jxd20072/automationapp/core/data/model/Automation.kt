package com.jxd20072.automationapp.core.data.model

import com.jxd20072.automationapp.core.data.serializer.InstantSerializer
import com.jxd20072.automationapp.core.data.serializer.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

@Serializable
data class AutomationMetadata(
	val name: String,
	val icon: String,

	@Serializable(with = InstantSerializer::class)
	val created: Instant,
	@Serializable(with = InstantSerializer::class)
	val updated: Instant,
	@Serializable(with = InstantSerializer::class)
	val lastRan: Instant?,

	val tags: List<String>,

	@Serializable(with = UUIDSerializer::class)
	val id: UUID
)

@Serializable
data class Automation(
	val metadata: AutomationMetadata,
	val variables: Set<Map<String, Int>>,
	val triggers: List<TriggerNode>,
	val functions: List<FunctionNode>
)

@Serializable
enum class NodeType { TRIGGER, FUNCTION, ACTION }

@Serializable
data class TriggerNode(
	val type: NodeType = NodeType.TRIGGER,
	val name: String,
	val content: List<ActionNode>
)

@Serializable
data class FunctionNode(
	val type: NodeType = NodeType.FUNCTION,
	val name: String,
	val content: List<ActionNode>
)

@Serializable
data class ActionNode(
	val type: NodeType = NodeType.ACTION,
	val name: String,
)