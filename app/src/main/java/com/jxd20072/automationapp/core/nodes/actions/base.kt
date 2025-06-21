package com.jxd20072.automationapp.core.nodes.actions

import com.jxd20072.automationapp.core.executor.ExecutionFlowContext
import com.jxd20072.automationapp.core.nodes.BaseNode

abstract class ActionNode(val id: String) : BaseNode(id) {
	abstract fun execute(context: ExecutionFlowContext)
}