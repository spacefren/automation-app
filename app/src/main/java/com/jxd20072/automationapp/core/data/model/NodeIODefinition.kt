package com.jxd20072.automationapp.core.data.model

import com.jxd20072.automationapp.ui.editor.viewmodel.utils.ProblemSeverity
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

@Serializable
sealed interface Value {
    @Serializable
    data class TEXT(val value: String) : Value

    @Serializable
    data class FLOAT(val value: Double) : Value {
        companion object {
            fun fromString(stringValue: String): Value.FLOAT? {
                val number = stringValue.toDoubleOrNull()
                return if (number != null) Value.FLOAT(number) else null
            }
        }
    }

    @Serializable
    data class BOOL(val value: Boolean) : Value

    @Serializable
    data class VARIABLE(val id: String) : Value

    @Serializable
    data class CHOICE(val selected: String) : Value

    fun valueAsString(): String {
        return when (this) {
            is Value.TEXT -> value
            is Value.FLOAT -> value.toString()
            is Value.BOOL -> value.toString()
            is Value.VARIABLE -> id
            is Value.CHOICE -> selected
        }
    }
}

sealed interface InputAcceptedValue<T : Value> {
    val defaultValue: T
    val validate: (value: T) -> ValidationResult

    data class TEXT(
        override val defaultValue: Value.TEXT,
        override val validate: (value: Value.TEXT) -> ValidationResult = { ValidationResult.Success },
    ) : InputAcceptedValue<Value.TEXT>

    data class NUMBER(
        override val defaultValue: Value.FLOAT,
        override val validate: (value: Value.FLOAT) -> ValidationResult = { ValidationResult.Success },
    ) : InputAcceptedValue<Value.FLOAT>

    data class BOOL(
        override val defaultValue: Value.BOOL,
        override val validate: (value: Value.BOOL) -> ValidationResult = { ValidationResult.Success },
    ) : InputAcceptedValue<Value.BOOL>

    data class CHOICE(
        val options: List<String>,
        override val defaultValue: Value.CHOICE,
        override val validate: (value: Value.CHOICE) -> ValidationResult = { ValidationResult.Success },
    ) : InputAcceptedValue<Value.CHOICE>

    data class VARIABLE(
        val acceptedTypes: Set<KClass<out Value>>,
        override val defaultValue: Value.VARIABLE,
        override val validate: (value: Value.VARIABLE) -> ValidationResult = { ValidationResult.Success },
    ) : InputAcceptedValue<Value.VARIABLE>
}

data class NodeInputSchema(
    val displayName: String,
    val description: String,
    val shouldShow: ((
        input: Map<String, Value>,
        automationDef: AutomationDef
    ) -> Boolean) = { _, _ -> true },
    val accepts: List<InputAcceptedValue<*>>,
) {
    companion object {
        fun withVariables(
            displayName: String,
            description: String,
            shouldShow: ((
                input: Map<String, Value>,
                automationDef: AutomationDef
            ) -> Boolean) = { _, _ -> true },
            accepts: List<InputAcceptedValue<*>>,
        ): NodeInputSchema {
            return NodeInputSchema(
                displayName = displayName,
                description = description,
                shouldShow = shouldShow,
                accepts = accepts + accepts
                    .filter { it !is InputAcceptedValue.VARIABLE }
                    .map {
                        InputAcceptedValue.VARIABLE(
                            acceptedTypes = setOf(it.defaultValue::class),
                            defaultValue = Value.VARIABLE("")
                        )
                    }
            )
        }
    }
}

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