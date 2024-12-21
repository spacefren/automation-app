package com.jxd20072.automationapp.core.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AutomationMetadata(
	val version: Int
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