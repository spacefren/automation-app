package com.jxd20072.automationapp.core.nodes.actions

import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.core.data.model.ConfigurationValue
import com.jxd20072.automationapp.core.data.model.NodeType
import com.jxd20072.automationapp.core.executor.ExecutionContext
import com.jxd20072.automationapp.core.nodes.NodeData
import com.jxd20072.automationapp.core.nodes.NodeRegistryEntry
import com.jxd20072.automationapp.core.nodes.props.NodeInputDefinition
import com.jxd20072.automationapp.core.nodes.props.ValidationResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class WaitAction(id: String) : ActionNode(id) {
    override fun execute(context: ExecutionContext) {
        val duration = getInput<ConfigurationValue.NUMBER>("duration").value

        runBlocking {
            delay(duration.toLong())
        }
    }
}

val WaitActionNode = NodeRegistryEntry<ActionNode>(
    data = NodeData(
        nodeType = NodeType.ACTION,
        displayName = "Wait",
        name = "wait",
        category = "flow",
        editorActionDescription = { input ->
            listOf(
                "Wait",
                "*${input["duration"]?.value}ms"
            )
        },
        description = "Pause the execution for a specified duration",
        icon = R.drawable.round_play_arrow_24,
        inputDefinitions = mapOf(
            "duration" to NodeInputDefinition(
                name = "Duration",
                description = "The duration of the pause, in milliseconds",
                defaultValue = ConfigurationValue.NUMBER("10000"),
                validate = { ValidationResult.Success }
            )
        ),
        outputDefinitions = emptyMap(),
        permissions = emptyList()
    ),
    factory = { WaitAction(it) }
)