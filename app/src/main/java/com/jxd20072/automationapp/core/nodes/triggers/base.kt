package com.jxd20072.automationapp.core.nodes.triggers

import com.jxd20072.automationapp.core.data.model.ConfigurationValue
import com.jxd20072.automationapp.core.executor.ExecutionFlowContext
import com.jxd20072.automationapp.core.nodes.BaseNode

abstract class TriggerNode(val id: String) : BaseNode(id) {
	abstract fun startListening(
		context: ExecutionFlowContext,
		trigger: (output: Map<String, ConfigurationValue<*>>) -> Unit
	)
}