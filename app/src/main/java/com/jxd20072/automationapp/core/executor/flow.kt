package com.jxd20072.automationapp.core.executor

import com.jxd20072.automationapp.core.data.model.ActionNodeDef
import com.jxd20072.automationapp.core.data.model.VariableDef
import com.jxd20072.automationapp.core.nodes.actionNodeFactory
import com.jxd20072.automationapp.core.nodes.actions.ActionNode

class ExecutionFlow(
	private val actionDefinitions: List<ActionNodeDef>,
	val initialVariables: Set<VariableDef>,
	val scope: String = "main"
) {
	lateinit var context: ExecutionFlowContext
	private lateinit var actions: List<ActionNode>

	private fun createContext(): ExecutionFlowContext {
		return ExecutionFlowContext(
			scope = scope,
			initialVariables = initialVariables,
		)
	}

	private fun buildActions(): List<ActionNode> {
		return actionDefinitions.map { actionNodeDef ->
			val action = actionNodeFactory[actionNodeDef.name]?.invoke()
				?: throw Exception("Action node not found: ${actionNodeDef.name}")
			// Should validate first too
			actionNodeDef.input.forEach { nodeDefInputEntry ->
				val actionNodeInput = action.input[nodeDefInputEntry.key]
					?: throw Exception(
						"Variable ${nodeDefInputEntry.key} not found in ${actionNodeDef.name}"
					)

				val newVar = linkInput(
					actionNodeInput.name,
					actionNodeInput.type,
					nodeDefInputEntry.value,
					context
				)

				if (newVar != null) {
					@Suppress("UNCHECKED_CAST")
					(actionNodeInput.onValueChange as (Any) -> Unit).invoke(newVar)
				}
			}
			action
		}
	}

	private fun resetState() {
		context = createContext()
		actions = buildActions()
	}

	fun startExecution() {
		resetState()

		context.getVariables().forEach { variable -> readVariable(variable, context) }
		actions.forEach { action ->
			action.execute(context)
		}
	}
}