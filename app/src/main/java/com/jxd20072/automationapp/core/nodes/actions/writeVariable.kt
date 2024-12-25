package com.jxd20072.automationapp.core.nodes.actions

import com.jxd20072.automationapp.core.data.model.VariableDef
import com.jxd20072.automationapp.core.data.model.VariableDefType
import com.jxd20072.automationapp.core.executor.ExecutionFlowContext

class VariableWriteNode : ActionNode() {
	private var variable: VariableDef = VariableDef.TEXT("", "")
	private var newValue = "writtenTextVariable"

	override val name = "Write variable"
	override val description = ""
	override val input = mapOf(
		"variable" to ActionNodeInput.VARIABLE(
			name = "variable",
			description = "the variable to change",
			defaultValue = variable,
			validate = { true },
			onValueChange = { variable = it }
		),
		"value" to ActionNodeInput.GHOST(
			name = "value",
			description = "the new value of the variable",
			enabled = { variable.type == VariableDefType.TEXT },
			defaultValue = "defaultWrittenTextValue",
			onValueChange = { newValue = it }
		)
	)

	override fun execute(context: ExecutionFlowContext) {
		super.execute(context)

		context.setVariable(name = variable.name, value = newValue)
	}
}