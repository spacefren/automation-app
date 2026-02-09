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

//@Serializable
//sealed interface VariableDefValue<T> {
//    val value: T
//
//    @Serializable
//    data class TEXT(
//        override val value: String = "",
//    ) : VariableDefValue<String>
//
//    @Serializable
//    data class BOOL(
//        override val value: Boolean = true,
//    ) : VariableDefValue<Boolean>
//
//    @Serializable
//    data class NUMBER(
//        override val value: String = "0",
//    ) : VariableDefValue<String>
//}

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