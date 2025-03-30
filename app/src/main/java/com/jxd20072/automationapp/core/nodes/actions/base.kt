package com.jxd20072.automationapp.core.nodes.actions

import com.jxd20072.automationapp.core.executor.ExecutionFlowContext
import com.jxd20072.automationapp.core.nodes.props.NodeInput

data class ActionNodeData(
	val displayName: String,
	val name: String,
	val category: String,
	val editorActionDescription: (input: Map<String, String>) -> List<String>,
	val description: String,
	val icon: Int,
	val inputDefinitions: Map<String, NodeInput<*>>
)

open class ActionNode(val id: String) {
	var input = mutableMapOf<String, Any>()
	open fun execute(context: ExecutionFlowContext) {}
}