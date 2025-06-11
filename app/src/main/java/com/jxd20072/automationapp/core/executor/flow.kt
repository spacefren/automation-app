package com.jxd20072.automationapp.core.executor

import android.util.Log
import com.jxd20072.automationapp.core.data.model.ActionNodeDef
import com.jxd20072.automationapp.core.data.model.AutomationDef
import com.jxd20072.automationapp.core.data.model.TriggerDef
import com.jxd20072.automationapp.core.nodes.actionNodes
import com.jxd20072.automationapp.core.nodes.actions.ActionNode
import com.jxd20072.automationapp.core.nodes.triggerNodes
import com.jxd20072.automationapp.core.nodes.triggers.TriggerNode
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.time.Instant

private const val TAG = "APP_ExecutionFlow"

class ExecutionFlow(
	private val automationDef: AutomationDef,
	val scope: String = "main"
) {
	lateinit var context: ExecutionFlowContext
	private var actions: Set<ActionNode> = emptySet()
	private var triggers: Set<TriggerNode> = emptySet()

	private fun createContext(): ExecutionFlowContext {
		return ExecutionFlowContext(
			scope = scope,
			initialVariables = automationDef.variables,
		)
	}

	private fun buildAction(actionNodeDef: ActionNodeDef): ActionNode {
		val actionNodeEntry = actionNodes[actionNodeDef.name]
			?: throw Exception("Action not found: ${actionNodeDef.name}")
		val action = actionNodeEntry.factory.invoke(actionNodeDef.id)

		val input = actionNodeEntry.data.inputDefinitions.mapValues { inputEntry ->
			actionNodeDef.input[inputEntry.key] ?: inputEntry.value.defaultValue
		}
		action.input = input

		actions = actions + action
		return action
	}

	private fun buildTrigger(triggerNodeDef: TriggerDef): TriggerNode {
		val triggerNodeEntry = triggerNodes[triggerNodeDef.name]
			?: throw Exception("Trigger not found: ${triggerNodeDef.name}")
		val trigger = triggerNodeEntry.factory.invoke(triggerNodeDef.id)

		val input = triggerNodeEntry.data.inputDefinitions.mapValues { inputEntry ->
			triggerNodeDef.input[inputEntry.key] ?: inputEntry.value.defaultValue
		}
		trigger.input = input
		trigger.content = triggerNodeDef.content.map { actionNodeDef ->
			actions.find { it.id == actionNodeDef.id } ?: buildAction(actionNodeDef)
		}

		triggers = triggers + trigger
		return trigger
	}

	private fun resetState() {
		context = createContext()
	}

	suspend fun startExecution() {
		Log.i(TAG, "STARTING EXECUTION")

		// Setup
		resetState()
		context.onStartExecution()

		// Build nodes
		automationDef.entryPoint.forEach { buildAction(it) }
		automationDef.triggers.forEach { buildTrigger(it) }

		// Init actions
		actions.forEach { action ->
			action.onInit(context)
		}

		coroutineScope {
			// Execute entry actions
			Log.d("APP_A", "running actions...");
			async {
				executeActionPath(automationDef.entryPoint)
				Log.d("APP_A", "actions done!");
			}

			// Init triggers
			Log.d("APP_A", "setting up triggers...");
			triggers.forEach { trigger ->
				async {
					trigger.onInit(context) {
						executeActions(trigger.content)
					}
					Log.d("APP_A", "trigger fired!");
				}
			}

			Log.d("APP_A", "automation running!");
		}

		Log.i(
			TAG, "EXECUTION ENDED in ${
				Instant.now().toEpochMilli() - context.executionStart.toEpochMilli()
			} ms"
		)
	}

	fun executeActionPath(actionDefinitions: List<ActionNodeDef>) {
		executeActions(actionDefinitions.map { actionDef -> actions.find { it.id == actionDef.id }!! })
	}

	fun executeActions(builtActions: List<ActionNode>) {
		builtActions.forEach { it.execute(context) }
	}
}