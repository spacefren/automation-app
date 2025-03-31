package com.jxd20072.automationapp.core.executor

import android.util.Log
import com.jxd20072.automationapp.core.data.model.VariableDef
import com.jxd20072.automationapp.core.data.repository.automations.ActionNodeDraft
import com.jxd20072.automationapp.core.nodes.actionNodes
import com.jxd20072.automationapp.core.nodes.actions.ActionNode
import java.time.Instant

private const val TAG = "APP_ExecutionFlow"

class ExecutionFlow(
	private val actionDefinitions: List<ActionNodeDraft>,
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
		return actionDefinitions.map { actionNodeDraft ->
			val actionNodeEntry = actionNodes[actionNodeDraft.name]
				?: throw Exception("Action node not found: ${actionNodeDraft.name}")
			val action = actionNodeEntry.factory.invoke(actionNodeDraft.id)
			val actionData = actionNodeEntry.data

			val actionInput = actionData.inputDefinitions.map { inputDef ->
				actionNodeDraft.input.find { it.name == inputDef.name } ?: VariableDef.withType(
					name = inputDef.name,
					value = inputDef.defaultValue,
					type = inputDef.type
				)
			}

			action.input = actionInput

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

		context.getVariables().forEach { variable -> readVariable(variable, context) }
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