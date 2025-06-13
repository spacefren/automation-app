package com.jxd20072.automationapp.core.nodes.triggers

import com.jxd20072.automationapp.core.data.model.ConfigurationEntry
import com.jxd20072.automationapp.core.executor.ExecutionFlowContext
import com.jxd20072.automationapp.core.nodes.BaseNode

open class TriggerNode(val id: String) : BaseNode(id) {
	open fun startListening(
		context: ExecutionFlowContext,
		trigger: (output: Map<String, ConfigurationEntry<*>>) -> Unit
	) {
	}
}