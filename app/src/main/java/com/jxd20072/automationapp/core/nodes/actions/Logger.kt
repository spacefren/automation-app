package com.jxd20072.automationapp.core.nodes.actions

import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.core.data.LogLevel
import com.jxd20072.automationapp.core.data.model.ConfigurationValue
import com.jxd20072.automationapp.core.data.model.NodeType
import com.jxd20072.automationapp.core.data.model.VariableDef
import com.jxd20072.automationapp.core.executor.ExecutionContext
import com.jxd20072.automationapp.core.executor.ExecutionFlow
import com.jxd20072.automationapp.core.nodes.NodeCategory
import com.jxd20072.automationapp.core.nodes.NodeData
import com.jxd20072.automationapp.core.nodes.NodeDescriptionPiece
import com.jxd20072.automationapp.core.nodes.NodeRegistryEntry
import com.jxd20072.automationapp.core.nodes.props.NodeInputDefinition
import com.jxd20072.automationapp.core.nodes.props.ValidationResult
import com.jxd20072.automationapp.ui.editor.viewmodel.utils.ProblemSeverity
import com.jxd20072.automationapp.ui.shared.components.LocalSnackbarState
import com.jxd20072.automationapp.ui.shared.components.SnackbarData
import com.jxd20072.automationapp.ui.shared.components.SnackbarType

class LoggerAction(id: String) : ActionNode(id) {
    override fun execute(context: ExecutionContext, flow: ExecutionFlow) {
        val text = getInput<ConfigurationValue.TEXT>("text", context).value

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

inline fun <reified T> getNodeInputPiece(
    variables: Set<VariableDef>,
    configValue: ConfigurationValue<*>?
): NodeDescriptionPiece where T : ConfigurationValue<*> {
    return when (configValue) {
        is T -> NodeDescriptionPiece.Input(configValue.value.toString())
        is ConfigurationValue.VARIABLE -> NodeDescriptionPiece.VariableInput(
            variables.find { it.id == configValue.value }?.name ?: "Unknown Variable"
        )

        else -> NodeDescriptionPiece.Input(configValue?.value.toString())
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
                getNodeInputPiece<ConfigurationValue.TEXT>(variables, input["text"])
            )
        },
        description = "Append a message to the logs screen",
        icon = R.drawable.round_align_left_24,
        inputDefinitions = mapOf(
            "text" to NodeInputDefinition(
                displayName = "Text",
                description = "The text to display this is a longer, more complete description",
                defaultValue = ConfigurationValue.TEXT("default value"),
                validate = { ValidationResult.Success },
            ),
            "validatedText" to NodeInputDefinition(
                displayName = "Validated Text",
                description = "This one doesn't accept any value!",
                defaultValue = ConfigurationValue.TEXT("default value 2"),
                validate = { textConfig ->
                    when {
                        textConfig.value.isBlank() -> ValidationResult.Error(
                            severity = ProblemSeverity.WARNING,
                            "Text can't be blank :("
                        )

                        textConfig.value == "hi" -> ValidationResult.Error(
                            severity = ProblemSeverity.ERROR,
                            "I DON'T like that one. NEVER AGAIN."
                        )

                        else -> ValidationResult.Success
                    }
                },
            ),
            "otherOption" to NodeInputDefinition(
                displayName = "Other Option",
                description = "This is a shorter description.",
                defaultValue = ConfigurationValue.BOOL(true),
                validate = { ValidationResult.Success },
            )
        ),
        outputDefinitions = emptyMap(),
        canHaveContent = false,
        permissions = emptyList()
    ),
    factory = { LoggerAction(it) }
)
