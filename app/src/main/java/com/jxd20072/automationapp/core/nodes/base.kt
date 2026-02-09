package com.jxd20072.automationapp.core.nodes

import com.jxd20072.automationapp.core.data.model.ConfigurationValue
import com.jxd20072.automationapp.core.data.model.NodeDef
import com.jxd20072.automationapp.core.data.model.NodeType
import com.jxd20072.automationapp.core.data.model.VariableDef
import com.jxd20072.automationapp.core.nodes.props.NodeInputDefinition
import com.jxd20072.automationapp.core.nodes.props.NodeOutputDefinition

data class NodeRegistryEntry<T>(
    val data: NodeData,
    val factory: (id: String) -> T
) where T : BaseNode

sealed class NodeDescriptionPiece {
    data class Text(
        val value: String
    ) : NodeDescriptionPiece()

    data class Input(
        val value: String
    ) : NodeDescriptionPiece()

    data class VariableInput(
        val value: String
    ) : NodeDescriptionPiece()
}

data class NodeData(
    val nodeType: NodeType,
    val displayName: String,
    val name: String,
    val category: String,
    val editorActionDescription: (
        input: Map<String, ConfigurationValue<*>>,
        variables: Set<VariableDef>
    ) -> List<NodeDescriptionPiece>,
    val description: String,
    val icon: Int,
    val inputDefinitions: Map<String, NodeInputDefinition<*>>,
    val outputDefinitions: Map<String, NodeOutputDefinition>,
    val permissions: List<String>
)

abstract class BaseNode(id: String) {
    var input: Map<String, ConfigurationValue<*>> = emptyMap()
    var content: List<NodeDef> = emptyList()

    inline fun <reified T> getInput(inputEntry: String): T where T : ConfigurationValue<*> {
        val entry = this.input[inputEntry]
        if (entry is T) {
            return entry
        } else {
            error("$inputEntry could not be converted to type ${T::class}")
        }
    }
}