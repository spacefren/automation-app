package com.jxd20072.automationapp.ui.editor.viewmodel.utils

import android.util.Log
import androidx.compose.runtime.Immutable
import com.jxd20072.automationapp.core.data.model.AutomationDef
import com.jxd20072.automationapp.core.data.model.InputAcceptedValue
import com.jxd20072.automationapp.core.data.model.NodeDef
import com.jxd20072.automationapp.core.data.model.NodeInputSchema
import com.jxd20072.automationapp.core.data.model.NodeType
import com.jxd20072.automationapp.core.data.model.ValidationResult
import com.jxd20072.automationapp.core.data.model.Value
import com.jxd20072.automationapp.core.data.model.VariableDef
import com.jxd20072.automationapp.core.nodes.actionNodes
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

fun getResultFromInputSchema(
    inputEntry: NodeInputSchema,
    input: Value,
    automationVariables: Set<VariableDef>
): Pair<ProblemSeverity, String>? {
    val acceptedValue = inputEntry.accepts.find { it.defaultValue::class == input::class }

    if (acceptedValue == null) return Pair(ProblemSeverity.ERROR, "Input of unsupported value type")
    else if (acceptedValue is InputAcceptedValue.VARIABLE) {
        val variable = automationVariables.find { it.id == (input as Value.VARIABLE).id }
            ?: return Pair(ProblemSeverity.ERROR, "Variable not found")

        return if (!acceptedValue.acceptedTypes.contains(variable.value::class)) {
            Pair(ProblemSeverity.ERROR, "Variable used as input of unsupported value type")
        } else null
    } else {
        return when (val result = (acceptedValue.validate as (Value) -> ValidationResult)(input)) {
            is ValidationResult.Success -> null
            is ValidationResult.Error -> Pair(ProblemSeverity.ERROR, result.message)
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
            problems += nodeData.inputSchemas.map { (name, schema) ->
                val value = nodeDef.input[name] ?: return@map null
                val result = getResultFromInputSchema(
                    schema,
                    value,
                    automationVariables
                )

                if (result != null) {
                    return@map EditorProblem.NodeInputProblem(
                        nodeId = nodeDef.id,
                        originatingInput = name,
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
