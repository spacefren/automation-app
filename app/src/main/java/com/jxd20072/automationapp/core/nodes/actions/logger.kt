package com.jxd20072.automationapp.core.nodes.actions

import android.util.Log
import com.jxd20072.automationapp.core.executor.ExecutionFlowContext

private const val TAG = "APP_LoggerNode"

class LoggerNode : ActionNode() {
	private lateinit var text: String

	override val name = "Log"
	override val description = ""
	override val input = mapOf(
		"text" to ActionNodeInput.TEXT(
			name = "text",
			description = "hello",
			defaultValue = "",
			validate = { true },
			onValueChange = { text = it }
		)
	)

	private fun processText(text: String, context: ExecutionFlowContext): String {
		return Regex("""@\w*""").replace(text) { matchResult ->
			context.getVariable(matchResult.value.removePrefix("@"))?.value
				?: matchResult.value
		}
	}

	override fun execute(context: ExecutionFlowContext) {
		super.execute(context)

		Log.d(TAG, processText(text, context))
		Log.d(TAG, context.getVariables().map { "${it.value} (${it.type})" }.toString())
	}
}