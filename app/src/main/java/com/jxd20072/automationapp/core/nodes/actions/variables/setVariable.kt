package com.jxd20072.automationapp.core.nodes.actions.variables

import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.core.data.LogLevel
import com.jxd20072.automationapp.core.data.model.ConfigurationValue
import com.jxd20072.automationapp.core.data.model.NodeType
import com.jxd20072.automationapp.core.data.model.VariableDefValue
import com.jxd20072.automationapp.core.executor.ExecutionContext
import com.jxd20072.automationapp.core.nodes.NodeData
import com.jxd20072.automationapp.core.nodes.NodeRegistryEntry
import com.jxd20072.automationapp.core.nodes.actions.ActionNode
import com.jxd20072.automationapp.core.nodes.props.NodeInputDefinition

class SetVariable(id: String) : ActionNode(id) {
    override fun execute(context: ExecutionContext) {
        val variableId = getInput<ConfigurationValue.VARIABLE>("variable").value
        val variable = context.getVariable(variableId)

        if (variable == null) {
            context.log(LogLevel.ERROR, "Could not find variable: $variableId")
            return
        }

        when (variable.value) {
            is VariableDefValue.BOOL -> {
                val newVal = getInput<ConfigurationValue.BOOL>("value_bool").value
                context.setVariable(variable.copy(value = VariableDefValue.BOOL(newVal)))
            }

            is VariableDefValue.TEXT -> {
                val newVal = getInput<ConfigurationValue.TEXT>("value_text").value
                context.setVariable(variable.copy(value = VariableDefValue.TEXT(newVal)))
            }

            is VariableDefValue.NUMBER -> {
                val newVal = getInput<ConfigurationValue.NUMBER>("value_number").value
                context.setVariable(variable.copy(value = VariableDefValue.NUMBER(newVal)))
            }
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
                ?: return@NodeData listOf("Set", "*Unknown Variable")

            val valueType = when (variable.value) {
                is VariableDefValue.BOOL -> "bool"
                is VariableDefValue.TEXT -> "text"
                is VariableDefValue.NUMBER -> "number"
            }

            listOf(
                "Set",
                "*${variable.name}",
                "to",
                "*${input["value_$valueType"]?.value.toString()}"
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

                    automationDef.variables.find { it.id == variable.value }?.value is VariableDefValue.BOOL
                },
                defaultValue = ConfigurationValue.BOOL(true),
            ),
            "value_text" to NodeInputDefinition(
                displayName = "Value",
                description = "",
                shouldShow = { input, automationDef ->
                    val variable = input["variable"]
                    if (variable !is ConfigurationValue.VARIABLE) return@NodeInputDefinition false

                    automationDef.variables.find { it.id == variable.value }?.value is VariableDefValue.TEXT
                },
                defaultValue = ConfigurationValue.TEXT(""),
            ),
            "value_number" to NodeInputDefinition(
                displayName = "Value",
                description = "",
                shouldShow = { input, automationDef ->
                    val variable = input["variable"]
                    if (variable !is ConfigurationValue.VARIABLE) return@NodeInputDefinition false

                    automationDef.variables.find { it.id == variable.value }?.value is VariableDefValue.NUMBER
                },
                defaultValue = ConfigurationValue.NUMBER("0"),
            ),
        ),
        outputDefinitions = emptyMap(),
        permissions = emptyList()
    ),
    factory = { SetVariable(it) }
)