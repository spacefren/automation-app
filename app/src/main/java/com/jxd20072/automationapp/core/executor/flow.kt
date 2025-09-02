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
) {
	private var actions: Set<ActionNode> = emptySet()
	private var triggers: Set<TriggerNode> = emptySet()

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

	suspend fun startExecution(context: ExecutionContext) {
		Log.i(TAG, "STARTING EXECUTION")

		context.onStartExecution()

		coroutineScope {
			// Execute entry actions
			Log.d("APP_A", "running actions...");
			val entryPoint = automationDef.topLevel.find { it.id == "0" }
			if (entryPoint != null) {
				async {
					executeActionPath(entryPoint.content, context)
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
							executeActionPath(trigger.content, context)
						}
					}
				}

			Log.d("APP_A", "automation running!");
		}

		val executionStart = context.executionStart
		if (executionStart != null) {
			Log.i(
				TAG, "EXECUTION ENDED in ${
					Instant.now().toEpochMilli() - executionStart.toEpochMilli()
				} ms"
			)
		}
	}

	fun executeActionPath(nodeDefinitions: List<NodeDef>, context: ExecutionContext) {
		Log.d("APP_A", nodeDefinitions.toString())
		executeActions(nodeDefinitions.map { actionDef ->
			actions.find { it.id == actionDef.id } ?: buildAction(actionDef)
		}, context)
	}

	fun executeActions(builtActions: List<ActionNode>, context: ExecutionContext) {
		builtActions.forEach { it.execute(context) }
	}
}