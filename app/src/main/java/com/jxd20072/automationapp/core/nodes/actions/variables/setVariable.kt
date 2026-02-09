package com.jxd20072.automationapp.core.nodes.actions.variables

import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.core.data.LogLevel
import com.jxd20072.automationapp.core.data.model.ConfigurationValue
import com.jxd20072.automationapp.core.data.model.NodeType
import com.jxd20072.automationapp.core.executor.ExecutionContext
import com.jxd20072.automationapp.core.nodes.NodeData
import com.jxd20072.automationapp.core.nodes.NodeDescriptionPiece
import com.jxd20072.automationapp.core.nodes.NodeRegistryEntry
import com.jxd20072.automationapp.core.nodes.actions.ActionNode
import com.jxd20072.automationapp.core.nodes.props.NodeInputDefinition

class SetVariable(id: String) : ActionNode(id) {
    override fun execute(context: ExecutionContext) {
        val variableId = getInput<ConfigurationValue.VARIABLE>("variable", context).value
        val variable = context.getVariable(variableId)

        if (variable == null) {
            context.log(LogLevel.ERROR, "Could not find variable: $variableId")
            return
        }

        when (variable.value) {
            is ConfigurationValue.BOOL -> {
                val newVal = getInput<ConfigurationValue.BOOL>("value_bool", context)
                context.setVariable(variable.copy(value = newVal))
            }

            is ConfigurationValue.TEXT -> {
                val newVal = getInput<ConfigurationValue.TEXT>("value_text", context)
                context.setVariable(variable.copy(value = newVal))
            }

            is ConfigurationValue.NUMBER -> {
                val newVal = getInput<ConfigurationValue.NUMBER>("value_number", context)
                context.setVariable(variable.copy(value = newVal))
            }

            else -> TODO()
        }
    }
}

val SetVariableActionNode = NodeRegistryEntry<ActionNode>(
    data = NodeData(
        nodeType = NodeType.ACTION,
        displayName = "Set Variable",
        name = "setVariable",
        category = "variables",
        editorActionDescription = { input, variables ->
            val variableId = (input["variable"] as? ConfigurationValue.VARIABLE)?.value ?: ""
            val variable = variables.find { it.id == variableId }
                ?: return@NodeData listOf(
                    NodeDescriptionPiece.Text("Set"),
                    NodeDescriptionPiece.VariableInput("Unknown Variable"),
                )

            val valueType = when (variable.value) {
                is ConfigurationValue.BOOL -> "bool"
                is ConfigurationValue.TEXT -> "text"
                is ConfigurationValue.NUMBER -> "number"
                else -> TODO()
            }

            listOf(
                NodeDescriptionPiece.Text("Set"),
                NodeDescriptionPiece.VariableInput(variable.name),
                NodeDescriptionPiece.Text("to"),
                NodeDescriptionPiece.Input(input["value_$valueType"]?.value.toString())
            )
        },
        description = "Pause the execution for a specified duration",
        icon = R.drawable.round_data_object_24,
        inputDefinitions = mapOf(
            "variable" to NodeInputDefinition(
                displayName = "Variable",
                description = "The variable to change",
                defaultValue = ConfigurationValue.VARIABLE(""),
            ),
            "value_bool" to NodeInputDefinition(
                displayName = "Value",
                description = "",
                shouldShow = { input, automationDef ->
                    val variable = input["variable"]
                    if (variable !is ConfigurationValue.VARIABLE) return@NodeInputDefinition false

                    automationDef.variables.find { it.id == variable.value }?.value is ConfigurationValue.BOOL
                },
                defaultValue = ConfigurationValue.BOOL(true),
            ),
            "value_text" to NodeInputDefinition(
                displayName = "Value",
                description = "",
                shouldShow = { input, automationDef ->
                    val variable = input["variable"]
                    if (variable !is ConfigurationValue.VARIABLE) return@NodeInputDefinition false

                    automationDef.variables.find { it.id == variable.value }?.value is ConfigurationValue.TEXT
                },
                defaultValue = ConfigurationValue.TEXT(""),
            ),
            "value_number" to NodeInputDefinition(
                displayName = "Value",
                description = "",
                shouldShow = { input, automationDef ->
                    val variable = input["variable"]
                    if (variable !is ConfigurationValue.VARIABLE) return@NodeInputDefinition false

                    automationDef.variables.find { it.id == variable.value }?.value is ConfigurationValue.NUMBER
                },
                defaultValue = ConfigurationValue.NUMBER("0"),
            ),
        ),
        outputDefinitions = emptyMap(),
        permissions = emptyList()
    ),
    factory = { SetVariable(it) }
)