package com.jxd20072.automationapp.core.nodes.actions.variables

import android.util.Log
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
        val variableID = getInput<ConfigurationValue.VARIABLE>("variable").value
        val variable = context.getVariable(variableID)

        if (variable == null) {
            context.log(LogLevel.ERROR, "Could not find variable: $variableID")
            return
        }

        when (variable.value) {
            is VariableDefValue.BOOL -> {
                val newVal = getInput<ConfigurationValue.BOOL>("value_bool").value
                context.setVariable(variable.copy(value = VariableDefValue.BOOL(newVal)))
            }

            else -> {

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
        editorActionDescription = { input ->
            listOf(
                "Wait",
                "*${input["Duration"]?.value}ms"
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
            )
        ),
        outputDefinitions = emptyMap(),
        permissions = emptyList()
    ),
    factory = { SetVariable(it) }
)