package com.jxd20072.automationapp.ui.editor.viewmodel.utils

import android.util.Log
import androidx.compose.runtime.Immutable
import com.jxd20072.automationapp.core.data.model.AutomationDef
import com.jxd20072.automationapp.core.data.model.ConfigurationValue
import com.jxd20072.automationapp.core.data.model.NodeDef
import com.jxd20072.automationapp.core.data.model.NodeType
import com.jxd20072.automationapp.core.data.model.VariableDef
import com.jxd20072.automationapp.core.nodes.actionNodes
import com.jxd20072.automationapp.core.nodes.props.NodeInputDefinition
import com.jxd20072.automationapp.core.nodes.props.ValidationResult
import com.jxd20072.automationapp.core.nodes.triggerNodes

@Immutable
enum class ProblemSeverity {
    WARNING, ERROR
}

@Immutable
sealed interface EditorProblem {

    val severity: ProblemSeverity

    interface WithNode : EditorProblem {
        val nodeId: String
    }

    data class NodeProblem(
        override val severity: ProblemSeverity,
        override val nodeId: String,
        val message: String
    ) : WithNode

    data class NodeInputProblem(
        override val severity: ProblemSeverity,
        override val nodeId: String,
        val originatingInput: String,
        val message: String,
    ) : WithNode

    data class MissingPermission(
        override val severity: ProblemSeverity,
        override val nodeId: String,
        val permission: String,
    ) : WithNode
}

fun getResultFromInputDef(
    inputEntry: ConfigurationValue<*>,
    inputDef: NodeInputDefinition<*>,
    automationVariables: Set<VariableDef>
): Pair<ProblemSeverity, String>? {
    val isSameClass = inputEntry::class == inputDef.defaultValue::class

    if (!isSameClass) {
        if (inputEntry is ConfigurationValue.VARIABLE) {
            val variable = automationVariables.find { it.id == inputEntry.value }
                ?: return Pair(
                    ProblemSeverity.ERROR,
                    "Variable ${inputEntry.value} not found"
                )

            if (variable.value::class != inputDef.defaultValue::class) {
                return Pair(
                    ProblemSeverity.ERROR,
                    "Variable used as input should be of type ${
                        inputDef.defaultValue::class.simpleName
                    }"
                )
            }

            return null
        } else {
            return Pair(
                ProblemSeverity.ERROR,
                "Input should be of type ${
                    inputDef.defaultValue::class.simpleName
                }"
            )
        }
    } else {
        val result = (inputDef as NodeInputDefinition<Any?>).validate(inputEntry)

        return when (result) {
            is ValidationResult.Success -> null
            is ValidationResult.Error -> Pair(result.severity, result.message)
        }
    }
}

fun getProblemsFromNode(
    nodeDef: NodeDef,
    automationVariables: Set<VariableDef>,
    permissions: Map<String, Boolean>,
): Set<EditorProblem> {
    val problems = mutableSetOf<EditorProblem>()
    if (nodeDef.nodeType != NodeType.FUNCTION) {
        val nodeData =
            (if (nodeDef.nodeType == NodeType.TRIGGER) triggerNodes else actionNodes)[nodeDef.name]?.data

        if (nodeData == null) {
            problems += EditorProblem.NodeProblem(
                nodeId = nodeDef.id,
                severity = ProblemSeverity.ERROR,
                message = "Could not find node data for ${nodeDef.name}",
            )
        } else {
            problems += nodeData.inputDefinitions.map { inputDef ->
                val inputEntry = nodeDef.input[inputDef.key] ?: return@map null
                val result = getResultFromInputDef(
                    inputEntry,
                    inputDef.value,
                    automationVariables
                )

                if (result != null) {
                    return@map EditorProblem.NodeInputProblem(
                        nodeId = nodeDef.id,
                        originatingInput = inputDef.key,
                        severity = result.first,
                        message = result.second
                    )
                } else return@map null
            }.filterNotNull().toSet()

            problems += nodeData.permissions.map { permission ->
                if (permissions[permission] != true) {
                    EditorProblem.MissingPermission(
                        nodeId = nodeDef.id,
                        severity = ProblemSeverity.ERROR,
                        permission = permission
                    )
                } else return@map null
            }.filterNotNull().toSet()
        }
    }

    nodeDef.content.forEach {
        problems += getProblemsFromNode(it, automationVariables, permissions)
    }

    return problems
}

fun getProblems(
    automationDraft: AutomationDef,
    permissions: Map<String, Boolean>
): Set<EditorProblem> {
    val problems = mutableSetOf<EditorProblem>()

    Log.d("APP_A", "getProblems: $permissions")

    automationDraft.topLevel.forEach { topLevelNode ->
        problems += getProblemsFromNode(
            topLevelNode,
            automationDraft.variables,
            permissions
        )
    }

    return problems
}
