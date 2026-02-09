package com.jxd20072.automationapp.core.data.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class AutomationDefMetadata(
    val version: Int
)

@Serializable
data class VariableDef(
    val name: String,
    val id: String = UUID.randomUUID().toString(),
    val value: ConfigurationValue<*>
)

@Serializable
data class AutomationDef(
    val metadata: AutomationDefMetadata,
    val variables: Set<VariableDef>,
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