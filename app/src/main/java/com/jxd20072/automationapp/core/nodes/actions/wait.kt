package com.jxd20072.automationapp.core.nodes.actions

import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.core.data.model.ConfigurationEntry
import com.jxd20072.automationapp.core.executor.ExecutionFlowContext
import com.jxd20072.automationapp.core.nodes.ActionNodesEntry
import com.jxd20072.automationapp.core.nodes.props.NodeInputDefinition
import com.jxd20072.automationapp.core.nodes.props.ValidationResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

private const val TAG = "APP_NODE:WAIT"

class WaitAction(id: String) : ActionNode(id) {
	override fun execute(context: ExecutionFlowContext) {
		super.execute(context)

		val duration = getInput<ConfigurationEntry.NUMBER>("Duration").value

		runBlocking {
			delay(duration.toLong())
		}
	}
}

val WaitNode = ActionNodesEntry(
	data = ActionNodeData(
		displayName = "Wait",
		name = "wait",
		category = "debug",
		editorActionDescription = { input ->
			listOf(
				"Wait",
				"*${input["Duration"]?.value}ms"
			)
		},
		description = "Pause the execution for a specified duration",
		icon = R.drawable.round_play_arrow_24,
		inputDefinitions = mapOf(
			"Duration" to NodeInputDefinition(
				description = "The duration of the pause, in milliseconds",
				defaultValue = ConfigurationEntry.NUMBER(1000),
				validate = { ValidationResult.Success }
			)
		)
	),
	factory = { WaitAction(it) }
)