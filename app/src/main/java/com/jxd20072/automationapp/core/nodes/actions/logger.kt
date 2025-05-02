package com.jxd20072.automationapp.core.nodes.actions

import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.core.data.model.VariableDefType
import com.jxd20072.automationapp.core.executor.ExecutionFlowContext
import com.jxd20072.automationapp.core.nodes.ActionNodesEntry
import com.jxd20072.automationapp.core.nodes.props.NodeInputDefinition
import com.jxd20072.automationapp.ui.shared.LocalSnackbarState
import com.jxd20072.automationapp.ui.shared.SnackbarData
import com.jxd20072.automationapp.ui.shared.SnackbarType

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

		val text = this.input.find { it.name == "Text" }?.value!!

//		Log.d(
//			TAG,
//			context.getVariables().map { "${it.name}: ${it.value} (${it.variableType})" }.toString()
//		)

		LocalSnackbarState.showSnackbar(
			SnackbarData(
				text = processText(text, context),
				type = SnackbarType.INFO,
				icon = R.drawable.round_align_left_24
			)
		)
	}
}

val LoggerNode = ActionNodesEntry(
	data = ActionNodeData(
		displayName = "Log message",
		name = "log",
		category = "debug",
		editorActionDescription = { input ->
			listOf(
				"Log",
				"*${input.find { it.name == "Text" }?.value}"
			)
		},
		description = "Append a message to the logs screen",
		icon = R.drawable.round_align_left_24,
		inputDefinitions = listOf(
			NodeInputDefinition(
				name = "Text",
				description = "The text to display",
				type = VariableDefType.TEXT,
				validate = { true },
				defaultValue = "placeholder"
			)
		)
	),
	factory = { LoggerAction(it) }
)