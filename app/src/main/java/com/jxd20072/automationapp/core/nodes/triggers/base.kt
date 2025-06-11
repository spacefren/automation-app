package com.jxd20072.automationapp.core.nodes.triggers

import com.jxd20072.automationapp.core.data.model.ConfigurationEntry
import com.jxd20072.automationapp.core.executor.ExecutionFlowContext
import com.jxd20072.automationapp.core.nodes.BaseNode
import com.jxd20072.automationapp.core.nodes.actions.ActionNode

open class TriggerNode(val id: String) : BaseNode(id) {
	var content: List<ActionNode> = emptyList()

	open fun onInit(
		context: ExecutionFlowContext,
		trigger: (output: Map<String, ConfigurationEntry<*>>) -> Unit
	) {
		super.onInit(context)
	}
}