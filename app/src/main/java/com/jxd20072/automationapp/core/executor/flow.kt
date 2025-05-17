package com.jxd20072.automationapp.core.executor

import android.util.Log
import com.jxd20072.automationapp.core.data.model.ActionNodeDef
import com.jxd20072.automationapp.core.data.model.VariableDef
import com.jxd20072.automationapp.core.nodes.actionNodes
import com.jxd20072.automationapp.core.nodes.actions.ActionNode
import java.time.Instant

private const val TAG = "APP_ExecutionFlow"

class ExecutionFlow(
	private val actionDefinitions: List<ActionNodeDef>,
	val initialVariables: Map<String, VariableDef<*>>,
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
			val actionNodeEntry = actionNodes[actionNodeDef.name]
				?: throw Exception("Action node not found: ${actionNodeDef.name}")
			val action = actionNodeEntry.factory.invoke(actionNodeDef.id)

			val input = actionNodeEntry.data.inputDefinitions.mapValues { inputEntry ->
				actionNodeDef.input[inputEntry.key]
					?: inputEntry.value.defaultValue
			}
			action.input = input

			action
		}
	}

	private fun resetState() {
		context = createContext()
		actions = buildActions()
	}

	fun startExecution() {
		Log.i(TAG, "STARTING EXECUTION")

		resetState()
		context.onStartExecution()

		actions.forEach { action ->
			action.execute(context)
		}

		Log.i(
			TAG, "EXECUTION ENDED in ${
				Instant.now().toEpochMilli() - context.executionStart.toEpochMilli()
			} ms"
		)
	}
}