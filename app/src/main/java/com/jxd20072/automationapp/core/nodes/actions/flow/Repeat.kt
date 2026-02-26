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

class RepeatAction(id: String) : ActionNode(id) {
    override fun execute(context: ExecutionContext, flow: ExecutionFlow) {
        val times = getInput<Value.FLOAT>("times", context).value.toInt()

        for (i in 1..times) {
            flow.executeActionPath(this@RepeatAction.content, context)
        }
    }
}

val RepeatActionNode = NodeRegistryEntry<ActionNode>(
    data = NodeData(
        nodeType = NodeType.ACTION,
        displayName = "Repeat",
        name = "repeat",
        category = NodeCategory.FLOW,
        editorActionDescription = { input, variables ->
            listOf(
                NodeDescriptionPiece.Text("Repeat"),
                getNodeInputPiece(variables, input["times"]),
                NodeDescriptionPiece.Text("times")
            )
        },
        description = "Execute the node's contents the specified amount of times",
        icon = R.drawable.round_play_arrow_24,
        inputSchemas = mapOf(
            "times" to NodeInputSchema.create(
                displayName = "Times",
                description = "How many times to repeat",
                accepts = listOf(
                    InputAcceptedValue.INT(
                        defaultValue = Value.INT(5),
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
        canHaveContent = true
    ),
    factory = { RepeatAction(it) }
)