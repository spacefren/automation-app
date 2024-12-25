package com.jxd20072.automationapp.core.nodes.actions

import com.jxd20072.automationapp.core.data.model.VariableDef
import com.jxd20072.automationapp.core.data.model.VariableDefType
import com.jxd20072.automationapp.core.executor.ExecutionFlowContext

sealed class ActionNodeInput<T>(
	val name: String,
	val description: String,
	val type: VariableDefType,
	val defaultValue: T,
	val validate: ((T) -> Boolean)? = null,
	val onValueChange: (T) -> Unit
) {
	class TEXT(
		name: String,
		description: String,
		defaultValue: String,
		validate: ((String) -> Boolean)?,
		onValueChange: (String) -> Unit
	) : ActionNodeInput<String>(
		name, description, VariableDefType.TEXT, defaultValue, validate, onValueChange
	)

	class BOOL(
		name: String,
		description: String,
		defaultValue: Boolean,
		validate: ((Boolean) -> Boolean)?,
		onValueChange: (Boolean) -> Unit
	) : ActionNodeInput<Boolean>(
		name, description, VariableDefType.BOOL, defaultValue, validate, onValueChange
	)

	class NUMBER(
		name: String,
		description: String,
		defaultValue: Float,
		validate: ((Float) -> Boolean)?,
		onValueChange: (Float) -> Unit
	) : ActionNodeInput<Float>(
		name, description, VariableDefType.NUMBER, defaultValue, validate, onValueChange
	)

	class VARIABLE(
		name: String,
		description: String,
		defaultValue: VariableDef,
		validate: ((VariableDef) -> Boolean)?,
		onValueChange: (VariableDef) -> Unit
	) : ActionNodeInput<VariableDef>(
		name, description, VariableDefType.VARIABLE, defaultValue, validate, onValueChange
	)

	class GHOST<V>(
		name: String,
		description: String,
		val enabled: (Map<String, ActionNodeInput<*>>) -> Boolean,
		defaultValue: V,
		validate: ((V) -> Boolean)? = null,
		onValueChange: (V) -> Unit
	) : ActionNodeInput<V>(
		name, description, when (defaultValue) {
			is String -> VariableDefType.TEXT
			is Float -> VariableDefType.NUMBER
			is Boolean -> VariableDefType.BOOL
			else -> VariableDefType.TEXT
		}, defaultValue, validate, onValueChange
	)
}

open class ActionNode {
	open val name = ""
	open val description = ""
	open val input = emptyMap<String, ActionNodeInput<*>>()

	open fun execute(context: ExecutionFlowContext) {}
}