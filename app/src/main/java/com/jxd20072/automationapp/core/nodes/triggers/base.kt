package com.jxd20072.automationapp.core.nodes.triggers

import com.jxd20072.automationapp.core.data.model.VariableDef
import com.jxd20072.automationapp.core.executor.ExecutionFlowContext
import com.jxd20072.automationapp.core.nodes.props.NodeInputDefinition

data class TriggerNodeData(
	val displayName: String,
	val name: String,
	val description: String,
	val inputDefinitions: List<NodeInputDefinition>,
	val outputDefinitions: List<VariableDef>
)

open class TriggerNode(val id: String) {
	var input = mutableMapOf<String, Any>()
	open fun onStart(context: ExecutionFlowContext, emit: () -> Unit) {}
	open fun onEnd() {}
}
