package com.jxd20072.automationapp.core.nodes.triggers

import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.core.data.model.InputAcceptedValue
import com.jxd20072.automationapp.core.data.model.NodeInputSchema
import com.jxd20072.automationapp.core.data.model.NodeType
import com.jxd20072.automationapp.core.data.model.Value
import com.jxd20072.automationapp.core.executor.ExecutionContext
import com.jxd20072.automationapp.core.nodes.NodeCategory
import com.jxd20072.automationapp.core.nodes.NodeData
import com.jxd20072.automationapp.core.nodes.NodeDescriptionPiece
import com.jxd20072.automationapp.core.nodes.NodeRegistryEntry
import com.jxd20072.automationapp.core.nodes.actions.getNodeInputPiece
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class WaitTrigger(id: String) : TriggerNode(id) {
    override fun startListening(
        context: ExecutionContext,
        trigger: (output: Map<String, Value>) -> Unit
    ) {
        val duration = getInput<Value.INT>("duration", context).value

        runBlocking {
            delay(duration.toLong())
            trigger(emptyMap())
        }
    }
}

val WaitTriggerNode = NodeRegistryEntry<TriggerNode>(
    data = NodeData(
        nodeType = NodeType.TRIGGER,
        displayName = "Wait",
        name = "wait",
        category = NodeCategory.FLOW,
        editorActionDescription = { input, variables ->
            listOf(
                NodeDescriptionPiece.Text("Wait"),
                getNodeInputPiece(variables, input["duration"])
            )
        },
        description = "Run selected actions after a specified duration",
        icon = R.drawable.round_electric_bolt_24,
        inputSchemas = mapOf(
            "duration" to NodeInputSchema(
                displayName = "Duration",
                description = "The duration of the delay, in milliseconds",
                accepts = listOf(InputAcceptedValue.INT(Value.INT(1000)))
            ),
        ),
        outputDefinitions = emptyMap(),
        permissions = emptyList(),
        canHaveContent = true
    ),
    factory = { WaitTrigger(it) }
)