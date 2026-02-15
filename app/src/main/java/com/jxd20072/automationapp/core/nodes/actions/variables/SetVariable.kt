package com.jxd20072.automationapp.core.nodes.actions.variables

import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.core.data.LogLevel
import com.jxd20072.automationapp.core.data.model.InputAcceptedValue
import com.jxd20072.automationapp.core.data.model.NodeInputSchema
import com.jxd20072.automationapp.core.data.model.NodeType
import com.jxd20072.automationapp.core.data.model.Value
import com.jxd20072.automationapp.core.executor.ExecutionContext
import com.jxd20072.automationapp.core.executor.ExecutionFlow
import com.jxd20072.automationapp.core.nodes.NodeCategory
import com.jxd20072.automationapp.core.nodes.NodeData
import com.jxd20072.automationapp.core.nodes.NodeDescriptionPiece
import com.jxd20072.automationapp.core.nodes.NodeRegistryEntry
import com.jxd20072.automationapp.core.nodes.actions.ActionNode
import com.jxd20072.automationapp.utils.getNodeInputPiece
import com.jxd20072.automationapp.utils.specs.getValueName

class SetVariable(id: String) : ActionNode(id) {
    override fun execute(context: ExecutionContext, flow: ExecutionFlow) {
        val variableId = getInput<Value.VARIABLE>("variable", context).id
        val variable = context.getVariable(variableId)

        if (variable == null) {
            context.log(LogLevel.ERROR, "Could not find variable: $variableId")
            return
        }

        val suffix = getValueName(variable.value)
        val newVal = when (variable.value) {
            is Value.TEXT -> getInput<Value.TEXT>(
                "value_$suffix",
                context
            )

            is Value.BOOL -> getInput<Value.BOOL>(
                "value_$suffix",
                context
            )

            is Value.INT -> getInput<Value.INT>(
                "value_$suffix",
                context
            )

            is Value.FLOAT -> getInput<Value.FLOAT>(
                "value_$suffix",
                context
            )

            else -> {
                context.log(LogLevel.ERROR, "Variable type not supported")
                return
            }
        }

        if (newVal::class != variable.value::class) {
            context.log(LogLevel.ERROR, "New variable value type doesn't match old type")
            return
        }

        context.setVariable(variable.copy(value = newVal))
    }
}

val SetVariableActionNode = NodeRegistryEntry<ActionNode>(
    data = NodeData(
        nodeType = NodeType.ACTION,
        displayName = "Set Variable",
        name = "setVariable",
        category = NodeCategory.VARIABLES,
        editorActionDescription = { input, variables ->
            val variableId = (input["variable"] as? Value.VARIABLE)?.id ?: ""
            val variable = variables.find { it.id == variableId }
                ?: return@NodeData listOf(
                    NodeDescriptionPiece.Text("Set"),
                    NodeDescriptionPiece.VariableInput("Unknown Variable"),
                )

            val valueType = getValueName(variable.value)
            val valueInput = getNodeInputPiece(variables, input["value_$valueType"])

            listOf(
                NodeDescriptionPiece.Text("Set"),
                getNodeInputPiece(variables, input["variable"]),
                NodeDescriptionPiece.Text("to"),
                valueInput,
            )
        },
        description = "Pause the execution for a specified duration",
        icon = R.drawable.round_data_object_24,
        inputSchemas = mapOf(
            "variable" to NodeInputSchema(
                displayName = "Variable",
                description = "The variable to change",
                accepts = listOf(
                    InputAcceptedValue.VARIABLE(
                        acceptedTypes = setOf(
                            Value.INT::class,
                            Value.FLOAT::class,
                            Value.TEXT::class,
                            Value.BOOL::class
                        ),
                        defaultValue = Value.VARIABLE("")
                    )
                ),
            ),
            "value_bool" to NodeInputSchema.withVariables(
                displayName = "Value",
                description = "",
                accepts = listOf(
                    InputAcceptedValue.BOOL(
                        defaultValue = Value.BOOL(true)
                    )
                ),
                shouldShow = { input, automationDef ->
                    val variable = input["variable"]
                    if (variable !is Value.VARIABLE) return@withVariables false

                    automationDef.variables.find { it.id == variable.id }?.value is Value.BOOL
                },
            ),
            "value_float" to NodeInputSchema.withVariables(
                displayName = "Value",
                description = "",
                accepts = listOf(
                    InputAcceptedValue.FLOAT(
                        defaultValue = Value.FLOAT(0f)
                    )
                ),
                shouldShow = { input, automationDef ->
                    val variable = input["variable"]
                    if (variable !is Value.VARIABLE) return@withVariables false

                    automationDef.variables.find { it.id == variable.id }?.value is Value.FLOAT
                },
            ),
            "value_text" to NodeInputSchema.withVariables(
                displayName = "Value",
                description = "",
                accepts = listOf(
                    InputAcceptedValue.TEXT(
                        defaultValue = Value.TEXT("")
                    )
                ),
                shouldShow = { input, automationDef ->
                    val variable = input["variable"]
                    if (variable !is Value.VARIABLE) return@withVariables false

                    automationDef.variables.find { it.id == variable.id }?.value is Value.TEXT
                },
            ),
        ),
        outputDefinitions = emptyMap(),
        permissions = emptyList(),
        canHaveContent = false
    ),
    factory = { SetVariable(it) }
)