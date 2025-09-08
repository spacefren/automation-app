package com.jxd20072.automationapp.core.executor

import com.jxd20072.automationapp.core.data.model.AutomationDef
import com.jxd20072.automationapp.core.data.model.NodeDef
import com.jxd20072.automationapp.core.data.model.NodeType
import com.jxd20072.automationapp.core.nodes.BaseNode
import com.jxd20072.automationapp.core.nodes.NodeRegistryEntry
import com.jxd20072.automationapp.core.nodes.actionNodes
import com.jxd20072.automationapp.core.nodes.actions.ActionNode
import com.jxd20072.automationapp.core.nodes.triggerNodes
import com.jxd20072.automationapp.core.nodes.triggers.TriggerNode
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

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
		context.onStartExecution()
		val jobs = mutableListOf<Job>()

		coroutineScope {
			// Execute entry actions
			val entryPoint = automationDef.topLevel.find { it.id == "0" }
			if (entryPoint != null) {
				jobs.add(
					launch {
						executeActionPath(entryPoint.content, context)
					}
				)
			}

			// Init triggers
			automationDef.topLevel
				.filter { it.nodeType == NodeType.TRIGGER }
				.map { buildTrigger(it) }
				.forEach { trigger ->
					jobs.add(
						launch {
							trigger.startListening(context) {
								executeActionPath(trigger.content, context)
							}
						}
					)
				}
		}

		// Wait for jobs to finish
		jobs.joinAll()
	}

	fun executeActionPath(nodeDefinitions: List<NodeDef>, context: ExecutionContext) {
		executeActions(nodeDefinitions.map { actionDef ->
			actions.find { it.id == actionDef.id } ?: buildAction(actionDef)
		}, context)
	}

	fun executeActions(builtActions: List<ActionNode>, context: ExecutionContext) {
		builtActions.forEach { it.execute(context) }
	}
}