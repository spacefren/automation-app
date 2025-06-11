package com.jxd20072.automationapp.core.nodes

import com.jxd20072.automationapp.core.nodes.triggers.TriggerNode
import com.jxd20072.automationapp.core.nodes.triggers.WaitTriggerNode

data class TriggerNodesEntry(
	val data: NodeData,
	val factory: (id: String) -> TriggerNode
)

val triggerNodes: Map<String, TriggerNodesEntry> = mapOf(
	"wait" to WaitTriggerNode
)