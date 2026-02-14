package com.jxd20072.automationapp.core.nodes

import com.jxd20072.automationapp.core.nodes.actions.ActionNode
import com.jxd20072.automationapp.core.nodes.actions.LoggerNode

import com.jxd20072.automationapp.core.nodes.actions.NotificationNode
import com.jxd20072.automationapp.core.nodes.actions.flow.RepeatActionNode
import com.jxd20072.automationapp.core.nodes.actions.flow.WaitActionNode
import com.jxd20072.automationapp.core.nodes.actions.variables.SetVariableActionNode

val actionNodes: Map<String, NodeRegistryEntry<ActionNode>> = listOf(
    LoggerNode,
    RepeatActionNode,
    WaitActionNode,
    NotificationNode,
    SetVariableActionNode,
).associateBy { it.data.name }

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