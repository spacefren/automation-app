package com.jxd20072.automationapp.core.nodes.actions.variables

import com.jxd20072.automationapp.core.data.model.Value

//import com.jxd20072.automationapp.R
//import com.jxd20072.automationapp.core.data.LogLevel
//import com.jxd20072.automationapp.core.data.model.NodeType
//import com.jxd20072.automationapp.core.executor.ExecutionContext
//import com.jxd20072.automationapp.core.executor.ExecutionFlow
//import com.jxd20072.automationapp.core.nodes.NodeCategory
//import com.jxd20072.automationapp.core.nodes.NodeData
//import com.jxd20072.automationapp.core.nodes.NodeDescriptionPiece
//import com.jxd20072.automationapp.core.nodes.NodeRegistryEntry
//import com.jxd20072.automationapp.core.nodes.actions.ActionNode
//import com.jxd20072.automationapp.core.nodes.actions.getNodeInputPiece
////
fun getConfigValueName(value: Value): String {
    return when (value) {
        is Value.BOOL -> "bool"
        is Value.TEXT -> "text"
        is Value.FLOAT -> "number"
        is Value.VARIABLE -> "variable"
        is Value.CHOICE -> "choose"
    }
}
//
//class SetVariable(id: String) : ActionNode(id) {
//    override fun execute(context: ExecutionContext, flow: ExecutionFlow) {
//        val variableId = getInput<ConfigurationValue.VARIABLE>("variable", context).value
//        val variable = context.getVariable(variableId)
//
//        if (variable == null) {
//            context.log(LogLevel.ERROR, "Could not find variable: $variableId")
//            return
//        }
//
//        val suffix = getConfigValueName(variable.value)
//        val newVal = when (variable.value) {
//            is ConfigurationValue.TEXT -> getInput<ConfigurationValue.TEXT>(
//                "value_$suffix",
//                context
//            )
//
//            is ConfigurationValue.BOOL -> getInput<ConfigurationValue.BOOL>(
//                "value_$suffix",
//                context
//            )
//
//            is ConfigurationValue.NUMBER -> getInput<ConfigurationValue.NUMBER>(
//                "value_$suffix",
//                context
//            )
//
//            is ConfigurationValue.CHOOSE -> getInput<ConfigurationValue.CHOOSE>(
//                "value_$suffix",
//                context
//            )
//
//            is ConfigurationValue.VARIABLE -> {
//                context.log(LogLevel.ERROR, "Variables can't hold other variables as values")
//                return
//            }
//        }
//
//        if (newVal::class != variable.value::class) {
//            context.log(LogLevel.ERROR, "New variable value type doesn't match old type")
//            return
//        }
//
//        context.setVariable(variable.copy(value = newVal))
//    }
//}
//
//val SetVariableActionNode = NodeRegistryEntry<ActionNode>(
//    data = NodeData(
//        nodeType = NodeType.ACTION,
//        displayName = "Set Variable",
//        name = "setVariable",
//        category = NodeCategory.VARIABLES,
//        editorActionDescription = { input, variables ->
//            val variableId = (input["variable"] as? ConfigurationValue.VARIABLE)?.value ?: ""
//            val variable = variables.find { it.id == variableId }
//                ?: return@NodeData listOf(
//                    NodeDescriptionPiece.Text("Set"),
//                    NodeDescriptionPiece.VariableInput("Unknown Variable"),
//                )
//
//            val valueType = getConfigValueName(variable.value)
//            val valueInput = getNodeInputPiece(variables, input["value_$valueType"])
//
//            listOf(
//                NodeDescriptionPiece.Text("Set"),
//                getNodeInputPiece(variables, input["variable"]),
//                NodeDescriptionPiece.Text("to"),
//                valueInput,
//            )
//        },
//        description = "Pause the execution for a specified duration",
//        icon = R.drawable.round_data_object_24,
//        inputSchemas = mapOf(
//            "variable" to NodeInputDefinition(
//                displayName = "Variable",
//                description = "The variable to change",
//                defaultValue = ConfigurationValue.VARIABLE(""),
//            ),
//            "value_bool" to NodeInputDefinition(
//                displayName = "Value",
//                description = "",
//                shouldShow = { input, automationDef ->
//                    val variable = input["variable"]
//                    if (variable !is ConfigurationValue.VARIABLE) return@NodeInputDefinition false
//
//                    automationDef.variables.find { it.id == variable.value }?.value is ConfigurationValue.BOOL
//                },
//                defaultValue = ConfigurationValue.BOOL(true),
//            ),
//            "value_text" to NodeInputDefinition(
//                displayName = "Value",
//                description = "",
//                shouldShow = { input, automationDef ->
//                    val variable = input["variable"]
//                    if (variable !is ConfigurationValue.VARIABLE) return@NodeInputDefinition false
//
//                    automationDef.variables.find { it.id == variable.value }?.value is ConfigurationValue.TEXT
//                },
//                defaultValue = ConfigurationValue.TEXT(""),
//            ),
//            "value_number" to NodeInputDefinition(
//                displayName = "Value",
//                description = "",
//                shouldShow = { input, automationDef ->
//                    val variable = input["variable"]
//                    if (variable !is ConfigurationValue.VARIABLE) return@NodeInputDefinition false
//
//                    automationDef.variables.find { it.id == variable.value }?.value is ConfigurationValue.NUMBER
//                },
//                defaultValue = ConfigurationValue.NUMBER("0"),
//            ),
//        ),
//        outputDefinitions = emptyMap(),
//        permissions = emptyList(),
//        canHaveContent = false
//    ),
//    factory = { SetVariable(it) }
//)