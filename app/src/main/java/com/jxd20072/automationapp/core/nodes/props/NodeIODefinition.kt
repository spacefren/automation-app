package com.jxd20072.automationapp.core.nodes.props

import com.jxd20072.automationapp.core.data.model.AutomationDef
import com.jxd20072.automationapp.core.data.model.ConfigurationValue
import com.jxd20072.automationapp.ui.editor.viewmodel.utils.ProblemSeverity

data class NodeInputDefinition<T>(
    val displayName: String,
    val description: String,
    val defaultValue: T,
    val shouldShow: ((
        input: Map<String, ConfigurationValue<*>>,
        automationDef: AutomationDef
    ) -> Boolean) = { _, _ -> true },
    val validate: (T) -> ValidationResult = { ValidationResult.Success },
) where T : ConfigurationValue<*>

enum class NodeOutputType {
    TEXT, BOOL, NUMBER
}

data class NodeOutputDefinition(
    val description: String,
    val outputType: NodeOutputType
)

sealed class ValidationResult {
    data object Success : ValidationResult()
    data class Error(
        val severity: ProblemSeverity,
        val message: String
    ) : ValidationResult()
}