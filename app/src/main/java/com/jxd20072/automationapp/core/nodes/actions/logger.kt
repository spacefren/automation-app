package com.jxd20072.automationapp.core.nodes.actions

import android.util.Log
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.core.executor.ExecutionFlowContext
import com.jxd20072.automationapp.core.nodes.ActionNodesEntry
import com.jxd20072.automationapp.core.nodes.props.NodeInput

private const val TAG = "APP_LoggerNode"

class LoggerAction(id: String) : ActionNode(id) {
	private fun processText(text: String, context: ExecutionFlowContext): String {
		return Regex("""@\w*""").replace(text) { matchResult ->
			context.getVariable(matchResult.value.removePrefix("@"))?.value
				?: matchResult.value
		}
	}

	override fun execute(context: ExecutionFlowContext) {
		super.execute(context)

		val text = this.input["text"] as String
		Log.d(TAG, processText(text, context))
		Log.d(
			TAG,
			context.getVariables().map { "${it.name}: ${it.value} (${it.variableType})" }.toString()
		)
	}
}

val LoggerNode = ActionNodesEntry(
	data = ActionNodeData(
		displayName = "Log",
		name = "log",
		editorActionDescription = { input -> listOf("Log", "*${input["text"] as String}") },
		description = "Append a message to the logs screen",
		icon = R.drawable.round_align_left_24,
		inputDefinitions = mapOf(
			"text" to NodeInput.TEXT(
				name = "Text",
				description = "The text to display",
				validate = { true },
				defaultValue = "placeholder"
			)
		)
	),
	factory = { LoggerAction(it) }
)