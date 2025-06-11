package com.jxd20072.automationapp.core.nodes.actions

import com.jxd20072.automationapp.core.executor.ExecutionFlowContext
import com.jxd20072.automationapp.core.nodes.BaseNode

open class ActionNode(val id: String) : BaseNode(id) {
	open fun execute(context: ExecutionFlowContext) {}
}