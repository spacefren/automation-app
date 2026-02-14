package com.jxd20072.automationapp.core.nodes

import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.core.data.model.NodeDef
import com.jxd20072.automationapp.core.data.model.NodeInputSchema
import com.jxd20072.automationapp.core.data.model.NodeOutputDefinition
import com.jxd20072.automationapp.core.data.model.NodeType
import com.jxd20072.automationapp.core.data.model.Value
import com.jxd20072.automationapp.core.data.model.VariableDef
import com.jxd20072.automationapp.core.executor.ExecutionContext

enum class NodeCategory(
    val displayName: String,
    val icon: Int,
) {
    DEBUG("Debug", R.drawable.round_align_left_24),
    FLOW("Flow", R.drawable.round_electric_bolt_24),
    VARIABLES("Variables", R.drawable.round_data_object_24),
    SYSTEM("System", R.drawable.round_settings_24),
}

data class NodeRegistryEntry<T>(
    val data: NodeData,
    val factory: (id: String) -> T
) where T : BaseNode

sealed interface NodeDescriptionPiece {
    val value: String

    data class Text(
        override val value: String
    ) : NodeDescriptionPiece

    data class Input(
        override val value: String
    ) : NodeDescriptionPiece

    data class VariableInput(
        override val value: String
    ) : NodeDescriptionPiece
}

data class NodeData(
    val nodeType: NodeType,
    val displayName: String,
    val name: String,
    val category: NodeCategory,
    val editorActionDescription: (
        input: Map<String, Value>,
        variables: Set<VariableDef>
    ) -> List<NodeDescriptionPiece>,
    val description: String,
    val icon: Int,
    val canHaveContent: Boolean,
    val inputSchemas: Map<String, NodeInputSchema>,
    val outputDefinitions: Map<String, NodeOutputDefinition>,
    val permissions: List<String>
)

abstract class BaseNode(id: String) {
    var input: Map<String, Value> = emptyMap()
    var content: List<NodeDef> = emptyList()

    inline fun <reified T> getInput(
        inputEntry: String,
        context: ExecutionContext
    ): T where T : Value {
        val entry = this.input[inputEntry] ?: error("Input $inputEntry not found")

        if (entry is Value.VARIABLE && T::class != Value.VARIABLE::class) {
            val variable = context.getVariable(entry.id)
            if (variable?.value !is T) {
                error("Variable input must be of type ${T::class.simpleName}")
            }
            return variable.value
        } else if (entry is T) {
            return entry
        } else {
            error("${entry::class.simpleName} could not be converted to type ${T::class}")
        }
    }
}