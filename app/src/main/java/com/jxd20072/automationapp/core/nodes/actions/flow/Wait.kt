package com.jxd20072.automationapp.core.nodes.actions.flow

import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.core.data.model.InputAcceptedValue
import com.jxd20072.automationapp.core.data.model.NodeInputSchema
import com.jxd20072.automationapp.core.data.model.NodeType
import com.jxd20072.automationapp.core.data.model.ValidationResult
import com.jxd20072.automationapp.core.data.model.Value
import com.jxd20072.automationapp.core.executor.ExecutionContext
import com.jxd20072.automationapp.core.executor.ExecutionFlow
import com.jxd20072.automationapp.core.nodes.NodeCategory
import com.jxd20072.automationapp.core.nodes.NodeData
import com.jxd20072.automationapp.core.nodes.NodeDescriptionPiece
import com.jxd20072.automationapp.core.nodes.NodeRegistryEntry
import com.jxd20072.automationapp.core.nodes.actions.ActionNode
import com.jxd20072.automationapp.ui.editor.viewmodel.utils.ProblemSeverity
import com.jxd20072.automationapp.utils.getNodeInputPiece
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class WaitAction(id: String) : ActionNode(id) {
    override fun execute(context: ExecutionContext, flow: ExecutionFlow) {
        val duration = getInput<Value.INT>("duration", context).value

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
        category = NodeCategory.FLOW,
        editorActionDescription = { input, variables ->
            listOf(
                NodeDescriptionPiece.Text("Wait"),
                getNodeInputPiece(variables, input["duration"])
            )
        },
        description = "Pause the execution for a specified duration",
        icon = R.drawable.round_play_arrow_24,
        inputSchemas = mapOf(
            "duration" to NodeInputSchema.withVariables(
                displayName = "Duration",
                description = "The duration of the pause, in milliseconds",
                accepts = listOf(
                    InputAcceptedValue.INT(
                        defaultValue = Value.INT(1000),
                        validate = {
                            if (it.value < 0) ValidationResult.Error(
                                severity = ProblemSeverity.ERROR,
                                message = "Duration can't be negative"
                            ) else ValidationResult.Success
                        }
                    )
                ),
            )
        ),
        outputDefinitions = emptyMap(),
        permissions = emptyList(),
        canHaveContent = false
    ),
    factory = { WaitAction(it) }
)