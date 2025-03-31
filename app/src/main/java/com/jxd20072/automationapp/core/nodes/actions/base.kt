package com.jxd20072.automationapp.core.nodes.actions

import com.jxd20072.automationapp.core.data.model.VariableDef
import com.jxd20072.automationapp.core.executor.ExecutionFlowContext
import com.jxd20072.automationapp.core.nodes.props.NodeInputDefinition

data class ActionNodeData(
	val displayName: String,
	val name: String,
	val category: String,
	val editorActionDescription: (input: List<VariableDef>) -> List<String>,
	val description: String,
	val icon: Int,
	val inputDefinitions: List<NodeInputDefinition>
)

open class ActionNode(val id: String) {
	var input: List<VariableDef> = emptyList()
	open fun execute(context: ExecutionFlowContext) {}
}