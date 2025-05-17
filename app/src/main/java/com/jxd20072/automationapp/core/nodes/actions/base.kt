package com.jxd20072.automationapp.core.nodes.actions

import com.jxd20072.automationapp.core.data.model.NodeInputEntry
import com.jxd20072.automationapp.core.executor.ExecutionFlowContext
import com.jxd20072.automationapp.core.nodes.props.NodeInputDefinition

data class ActionNodeData(
	val displayName: String,
	val name: String,
	val category: String,
	val editorActionDescription: (input: Map<String, NodeInputEntry<*>>) -> List<String>,
	val description: String,
	val icon: Int,
	val inputDefinitions: Map<String, NodeInputDefinition<*>>
)

open class ActionNode(val id: String) {
	var input: Map<String, NodeInputEntry<*>> = emptyMap()
	open fun execute(context: ExecutionFlowContext) {}

	inline fun <reified T> getInput(inputEntry: String): T where T : NodeInputEntry<*> {
		val entry = this.input[inputEntry]
		if (entry is T) {
			return entry
		} else {
			error("$inputEntry could not be converted to type ${T::class}")
		}
	}
}