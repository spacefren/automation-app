package com.jxd20072.automationapp.core.nodes

import com.jxd20072.automationapp.core.nodes.actions.ActionNode
import com.jxd20072.automationapp.core.nodes.actions.LoggerNode
import com.jxd20072.automationapp.core.nodes.actions.WaitActionNode

data class ActionNodesEntry(
	val data: NodeData,
	val factory: (id: String) -> ActionNode
)

val actionNodes: Map<String, ActionNodesEntry> = mapOf(
	"log" to LoggerNode,
	"wait" to WaitActionNode,
	"log3" to LoggerNode,
)

/*

Some ideas to test the nodes system:

	Logger: Outputs a message to the logs
		- @message -> what to log, @variable annotation feature?
	SetVariable: Changes a variable
		- @variable -> the variable to change (must already be created)
		- @newValue -> the new value to assign to the variable
	Wait: Pauses the execution of the automation for a specified period of time
		- @duration -> how much to pause the thread for

*/