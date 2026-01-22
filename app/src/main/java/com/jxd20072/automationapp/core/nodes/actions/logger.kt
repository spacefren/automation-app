package com.jxd20072.automationapp.core.nodes.actions

import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.core.data.LogLevel
import com.jxd20072.automationapp.core.data.model.ConfigurationValue
import com.jxd20072.automationapp.core.data.model.NodeType
import com.jxd20072.automationapp.core.executor.ExecutionContext
import com.jxd20072.automationapp.core.nodes.NodeData
import com.jxd20072.automationapp.core.nodes.NodeRegistryEntry
import com.jxd20072.automationapp.core.nodes.props.NodeInputDefinition
import com.jxd20072.automationapp.core.nodes.props.ValidationResult
import com.jxd20072.automationapp.ui.shared.components.LocalSnackbarState
import com.jxd20072.automationapp.ui.shared.components.SnackbarData
import com.jxd20072.automationapp.ui.shared.components.SnackbarType

class LoggerAction(id: String) : ActionNode(id) {
    private fun processText(text: String, context: ExecutionContext): String {
        return Regex("""@\w*""").replace(text) { matchResult ->
            (context.getVariable(matchResult.value.removePrefix("@"))?.value ?: matchResult.value)
                .toString()
        }
    }

    override fun execute(context: ExecutionContext) {
        val text = getInput<ConfigurationValue.TEXT>("text").value

        context.log(LogLevel.INFO, text)

        LocalSnackbarState.showSnackbar(
            SnackbarData(
                text = processText(text, context),
                type = SnackbarType.INFO,
                icon = R.drawable.round_align_left_24
            )
        )
    }
}

val LoggerNode = NodeRegistryEntry<ActionNode>(
    data = NodeData(
        nodeType = NodeType.ACTION,
        displayName = "Log message",
        name = "log",
        category = "debug",
        editorActionDescription = { input ->
            listOf(
                "Log",
                "*${input["text"]?.value}"
            )
        },
        description = "Append a message to the logs screen",
        icon = R.drawable.round_align_left_24,
        inputDefinitions = mapOf(
            "text" to NodeInputDefinition(
                name = "Text",
                description = "The text to display this is a longer, more complete description",
                defaultValue = ConfigurationValue.TEXT("default value"),
                validate = { ValidationResult.Success },
            ),
            "validatedText" to NodeInputDefinition(
                name = "Validated Text",
                description = "This one doesn't accept any value!",
                defaultValue = ConfigurationValue.TEXT("default value 2"),
                validate = { textConfig ->
                    when {
                        textConfig.value.isBlank() -> ValidationResult.Error("Text can't be blank")
                        textConfig.value == "hi" -> ValidationResult.Error("I don't like that one")
                        else -> ValidationResult.Success
                    }
                },
            ),
            "otherOption" to NodeInputDefinition(
                name = "Other Option",
                description = "This is a shorter description.",
                defaultValue = ConfigurationValue.BOOL(true),
                validate = { ValidationResult.Success },
            )
        ),
        outputDefinitions = emptyMap(),
        permissions = emptyList()
    ),
    factory = { LoggerAction(it) }
)
