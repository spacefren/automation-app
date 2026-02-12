package com.jxd20072.automationapp.core.nodes.actions

import com.jxd20072.automationapp.core.executor.ExecutionContext
import com.jxd20072.automationapp.core.executor.ExecutionFlow
import com.jxd20072.automationapp.core.nodes.BaseNode

abstract class ActionNode(val id: String) : BaseNode(id) {
    abstract fun execute(context: ExecutionContext, flow: ExecutionFlow)
}