package com.jxd20072.automationapp.core.executor

import android.util.Log
import com.jxd20072.automationapp.core.data.model.ActionNodeDef
import com.jxd20072.automationapp.core.data.model.VariableDef
import com.jxd20072.automationapp.core.nodes.actionNodes
import com.jxd20072.automationapp.core.nodes.actions.ActionNode
import java.time.Instant

private const val TAG = "APP_EecutionFlow"

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
			val action = actionNodes[actionNodeDef.name]
				?: throw Exception("Action node not found: ${actionNodeDef.name}")
			// Should validate first too

			val input = mutableMapOf<String, Any>()
			actionNodeDef.input.forEach { nodeDefInputEntry ->
				val actionNodeInput = action.data.inputDefinitions[nodeDefInputEntry.key]
					?: throw Exception(
						"Input ${nodeDefInputEntry.key} not found in ${actionNodeDef.name}"
					)

				val newVar = linkInput(
					actionNodeInput.type,
					nodeDefInputEntry.value,
					context
				)

				if (newVar != null) {
					input[nodeDefInputEntry.key] = newVar
				}
			}
			action.copy(input = input)
		}
	}

	private fun resetState() {
		context = createContext()
		actions = buildActions()
	}

	fun startExecution() {
		Log.i(TAG, "STARTING EXECUTION");

		resetState()
		context.onStartExecution()

		context.getVariables().forEach { variable -> readVariable(variable, context) }
		actions.forEach { action ->
			action.execute(action.input, context)
		}

		Log.i(
			TAG, "EXECUTION ENDED in ${
				Instant.now().toEpochMilli() - context.executionStart.toEpochMilli()
			} ms"
		);
	}
}