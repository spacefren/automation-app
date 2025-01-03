package com.jxd20072.automationapp.core.nodes.actions

import android.util.Log
import com.jxd20072.automationapp.core.executor.ExecutionFlowContext

private const val TAG = "APP_LoggerNode"

fun processText(text: String, context: ExecutionFlowContext): String {
	return Regex("""@\w*""").replace(text) { matchResult ->
		context.getVariable(matchResult.value.removePrefix("@"))?.value
			?: matchResult.value
	}
}

val loggerAction = ActionNode(
	data = ActionNodeData(
		name = "Log",
		description = "",
		inputDefinitions = mapOf(
			"text" to ActionNodeInput.TEXT(
				name = "text",
				description = "hello",
				defaultValue = "",
				validate = { true },
			)
		),
	),
	execute = { input, context ->
		val text = input["text"] as String
		Log.d(TAG, processText(text, context))
		Log.d(TAG, context.getVariables().map { "${it.value} (${it.type})" }.toString())
	}
)