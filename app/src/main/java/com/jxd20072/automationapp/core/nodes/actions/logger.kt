package com.jxd20072.automationapp.core.nodes.actions

import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.core.data.model.ConfigurationEntry
import com.jxd20072.automationapp.core.executor.ExecutionFlowContext
import com.jxd20072.automationapp.core.nodes.ActionNodesEntry
import com.jxd20072.automationapp.core.nodes.props.NodeInputDefinition
import com.jxd20072.automationapp.core.nodes.props.ValidationResult
import com.jxd20072.automationapp.ui.shared.LocalSnackbarState
import com.jxd20072.automationapp.ui.shared.SnackbarData
import com.jxd20072.automationapp.ui.shared.SnackbarType

private const val TAG = "APP_LoggerNode"

class LoggerAction(id: String) : ActionNode(id) {
	private fun processText(text: String, context: ExecutionFlowContext): String {
		return Regex("""@\w*""").replace(text) { matchResult ->
			(context.getVariable(matchResult.value.removePrefix("@"))?.value ?: matchResult.value)
				.toString()
		}
	}

	override fun execute(context: ExecutionFlowContext) {
		super.execute(context)

		val text = getInput<ConfigurationEntry.TEXT>("Text").value

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
				"*${input["Text"]?.value}"
			)
		},
		description = "Append a message to the logs screen",
		icon = R.drawable.round_align_left_24,
		inputDefinitions = mapOf(
			"Text" to NodeInputDefinition(
				description = "The text to display this is a longer, more complete description",
				defaultValue = ConfigurationEntry.TEXT("default value"),
				validate = { ValidationResult.Success },
			),
			"ValidatedText" to NodeInputDefinition(
				description = "This one doesn't accept any value!",
				defaultValue = ConfigurationEntry.TEXT("default value 2"),
				validate = { textConfig ->
					when {
						textConfig.value.isBlank() -> ValidationResult.Error("Text can't be blank")
						textConfig.value == "hi" -> ValidationResult.Error("I don't like that one")
						else -> ValidationResult.Success
					}
				},
			),
			"OtherOption" to NodeInputDefinition(
				description = "This is a shorter description.",
				defaultValue = ConfigurationEntry.BOOL(true),
				validate = { ValidationResult.Success },
			)
		)
	),
	factory = { LoggerAction(it) }
)