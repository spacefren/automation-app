package com.jxd20072.automationapp.core.nodes.actions

import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.core.data.model.VariableDef
import com.jxd20072.automationapp.core.data.model.VariableDefType
import com.jxd20072.automationapp.core.executor.ExecutionFlowContext
import com.jxd20072.automationapp.core.executor.readVariable
import com.jxd20072.automationapp.core.nodes.ActionNodesEntry
import com.jxd20072.automationapp.core.nodes.props.NodeInputDefinition
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

private const val TAG = "APP_NODE:WAIT"

class WaitAction(id: String) : ActionNode(id) {
	override fun execute(context: ExecutionFlowContext) {
		super.execute(context)

		val durationVar = this.input.find { it.name == "Duration" } as VariableDef.NUMBER
		val duration = readVariable(durationVar)

		if (duration == null) {
			return
		}

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
				"*${input.find { it.name == "Duration" }?.value}ms"
			)
		},
		description = "Pause the execution for a specified duration",
		icon = R.drawable.round_play_arrow_24,
		inputDefinitions = listOf(
			NodeInputDefinition(
				name = "Duration",
				description = "The duration of the pause, in milliseconds",
				type = VariableDefType.NUMBER,
				validate = { true },
				defaultValue = "1000"
			)
		)
	),
	factory = { WaitAction(it) }
)