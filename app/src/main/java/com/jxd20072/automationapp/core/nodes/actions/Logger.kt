package com.jxd20072.automationapp.core.nodes.actions

import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.core.data.LogLevel
import com.jxd20072.automationapp.core.data.model.InputAcceptedValue
import com.jxd20072.automationapp.core.data.model.NodeInputSchema
import com.jxd20072.automationapp.core.data.model.NodeType
import com.jxd20072.automationapp.core.data.model.ValidationResult
import com.jxd20072.automationapp.core.data.model.Value
import com.jxd20072.automationapp.core.data.model.VariableDef
import com.jxd20072.automationapp.core.executor.ExecutionContext
import com.jxd20072.automationapp.core.executor.ExecutionFlow
import com.jxd20072.automationapp.core.nodes.NodeCategory
import com.jxd20072.automationapp.core.nodes.NodeData
import com.jxd20072.automationapp.core.nodes.NodeDescriptionPiece
import com.jxd20072.automationapp.core.nodes.NodeRegistryEntry
import com.jxd20072.automationapp.ui.editor.viewmodel.utils.ProblemSeverity
import com.jxd20072.automationapp.ui.shared.components.LocalSnackbarState
import com.jxd20072.automationapp.ui.shared.components.SnackbarData
import com.jxd20072.automationapp.ui.shared.components.SnackbarType

class LoggerAction(id: String) : ActionNode(id) {
    override fun execute(context: ExecutionContext, flow: ExecutionFlow) {
        val text = getInput<Value.TEXT>("text", context).value

        context.log(LogLevel.INFO, text)

        LocalSnackbarState.showSnackbar(
            SnackbarData(
                text = text,
                type = SnackbarType.INFO,
                icon = R.drawable.round_align_left_24
            )
        )
    }
}

fun getNodeInputPiece(
    variables: Set<VariableDef>,
    value: Value?
): NodeDescriptionPiece {
    return when (value) {
        is Value.VARIABLE -> NodeDescriptionPiece.VariableInput(
            variables.find { it.id == value.id }?.name ?: "Unknown Variable"
        )

        else -> NodeDescriptionPiece.Input(value?.valueAsString() ?: "")
    }
}

val LoggerNode = NodeRegistryEntry<ActionNode>(
    data = NodeData(
        nodeType = NodeType.ACTION,
        displayName = "Log message",
        name = "log",
        category = NodeCategory.DEBUG,
        editorActionDescription = { input, variables ->
            listOf(
                NodeDescriptionPiece.Text("Log"),
                getNodeInputPiece(variables, input["text"])
            )
        },
        description = "Append a message to the logs screen",
        icon = R.drawable.round_align_left_24,
        inputSchemas = mapOf(
            "text" to NodeInputSchema.withVariables(
                displayName = "Text",
                description = "Description goes here",
                accepts = listOf(InputAcceptedValue.TEXT(Value.TEXT("Default Value")))
            ),
            "validatedText" to NodeInputSchema(
                displayName = "Validated Text",
                description = "Description goes here as well",
                accepts = listOf(
                    InputAcceptedValue.TEXT(
                        defaultValue = Value.TEXT("Default value 2"),
                        validate = { value ->
                            when {
                                value.value.isBlank() -> ValidationResult.Error(
                                    severity = ProblemSeverity.WARNING,
                                    "Text can't be blank :("
                                )

                                value.value == "hi" -> ValidationResult.Error(
                                    severity = ProblemSeverity.ERROR,
                                    "I DON'T like that one. NEVER AGAIN."
                                )

                                else -> ValidationResult.Success
                            }
                        }
                    )
                )
            ),
            "otherOption" to NodeInputSchema(
                displayName = "Other option",
                description = "Hi",
                accepts = listOf(
                    InputAcceptedValue.TEXT(Value.TEXT("Haii")),
                    InputAcceptedValue.BOOL(Value.BOOL(true))
                )
            )
        ),
        outputDefinitions = emptyMap(),
        canHaveContent = false,
        permissions = emptyList()
    ),
    factory = { LoggerAction(it) }
)
