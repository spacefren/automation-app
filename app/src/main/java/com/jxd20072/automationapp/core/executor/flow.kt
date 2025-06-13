package com.jxd20072.automationapp.core.executor

import android.util.Log
import com.jxd20072.automationapp.core.data.model.AutomationDef
import com.jxd20072.automationapp.core.data.model.NodeDef
import com.jxd20072.automationapp.core.data.model.NodeType
import com.jxd20072.automationapp.core.nodes.BaseNode
import com.jxd20072.automationapp.core.nodes.NodeRegistryEntry
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

	// TODO: Rework this. Should all actions be built here or inside the content of every function/trigger?
	private var actions: Set<ActionNode> = emptySet()
	private var triggers: Set<TriggerNode> = emptySet()

	private fun createContext(): ExecutionFlowContext {
		return ExecutionFlowContext(
			scope = scope,
			initialVariables = automationDef.variables,
		)
	}

	private fun <T : BaseNode> buildNode(nodeDef: NodeDef, registryEntry: NodeRegistryEntry<T>): T {
		val node = registryEntry.factory.invoke(nodeDef.id)

		val input = registryEntry.data.inputDefinitions.mapValues { inputEntry ->
			nodeDef.input[inputEntry.key] ?: inputEntry.value.defaultValue
		}
		node.input = input
		node.content = nodeDef.content

		return node
	}

	private fun buildAction(nodeDef: NodeDef): ActionNode {
		val actionNodeEntry = actionNodes[nodeDef.name]
			?: throw Exception("Action not found: ${nodeDef.name}")

		val action = buildNode(nodeDef, actionNodeEntry)
		actions += action
		return action
	}

	private fun buildTrigger(nodeDef: NodeDef): TriggerNode {
		val triggerNodeEntry = triggerNodes[nodeDef.name]
			?: throw Exception("Trigger not found: ${nodeDef.name}")

		val trigger = buildNode(nodeDef, triggerNodeEntry)
		triggers += trigger
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

		coroutineScope {
			// Execute entry actions
			Log.d("APP_A", "running actions...");
			val entryPoint = automationDef.topLevel.find { it.id == "0" }
			if (entryPoint != null) {
				async {
					executeActionPath(entryPoint.content)
					Log.d("APP_A", "actions done!");
				}
			}

			// Init triggers
			Log.d("APP_A", "setting up triggers...");
			automationDef.topLevel
				.filter { it.nodeType == NodeType.TRIGGER }
				.map { buildTrigger(it) }
				.forEach { trigger ->
					async {
						trigger.startListening(context) {
							Log.d("APP_A", "trigger fired!");
							executeActionPath(trigger.content)
						}
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

	fun executeActionPath(nodeDefinitions: List<NodeDef>) {
		executeActions(nodeDefinitions.map { actionDef ->
			actions.find { it.id == actionDef.id } ?: buildAction(actionDef)
		})
	}

	fun executeActions(builtActions: List<ActionNode>) {
		builtActions.forEach { it.execute(context) }
	}
}