package com.jxd20072.automationapp.core.nodes

import com.jxd20072.automationapp.core.nodes.triggers.TriggerNode
import com.jxd20072.automationapp.core.nodes.triggers.WaitTriggerNode

val triggerNodes: Map<String, NodeRegistryEntry<TriggerNode>> = mapOf(
	"wait" to WaitTriggerNode
)