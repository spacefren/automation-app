package com.jxd20072.automationapp.core.nodes.triggers

import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.core.data.model.ConfigurationEntry
import com.jxd20072.automationapp.core.data.model.NodeType
import com.jxd20072.automationapp.core.executor.ExecutionFlowContext
import com.jxd20072.automationapp.core.nodes.NodeData
import com.jxd20072.automationapp.core.nodes.NodeRegistryEntry
import com.jxd20072.automationapp.core.nodes.props.NodeInputDefinition
import com.jxd20072.automationapp.core.nodes.props.ValidationResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class WaitTrigger(id: String) : TriggerNode(id) {
	override fun onInit(
		context: ExecutionFlowContext,
		trigger: (output: Map<String, ConfigurationEntry<*>>) -> Unit
	) {
		super.onInit(context)

		val duration = getInput<ConfigurationEntry.NUMBER>("Duration").value

		runBlocking {
			delay(duration.toLong())
			trigger(emptyMap())
		}
	}
}

val WaitTriggerNode = NodeRegistryEntry<TriggerNode>(
	data = NodeData(
		nodeType = NodeType.TRIGGER,
		displayName = "Wait",
		name = "wait",
		category = "debug",
		editorActionDescription = { input ->
			listOf(
				"Wait",
				"*${input["Duration"]?.value}ms"
			)
		},
		description = "Run selected actions after a specified duration",
		icon = R.drawable.round_electric_bolt_24,
		inputDefinitions = mapOf(
			"Duration" to NodeInputDefinition(
				description = "The duration of the delay, in milliseconds",
				defaultValue = ConfigurationEntry.NUMBER(1000),
				validate = { ValidationResult.Success }
			),
			"IsThisWorking" to NodeInputDefinition(
				description = "if you see this, yes",
				defaultValue = ConfigurationEntry.BOOL(true),
				validate = { ValidationResult.Success }
			)
		),
		outputDefinitions = emptyMap()
	),
	factory = { WaitTrigger(it) }
)