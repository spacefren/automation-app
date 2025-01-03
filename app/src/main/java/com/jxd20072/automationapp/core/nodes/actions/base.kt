package com.jxd20072.automationapp.core.nodes.actions

import com.jxd20072.automationapp.core.data.model.VariableDef
import com.jxd20072.automationapp.core.data.model.VariableDefType
import com.jxd20072.automationapp.core.executor.ExecutionFlowContext
import java.util.UUID

sealed class ActionNodeInput<T>(
	val name: String,
	val description: String,
	val type: VariableDefType,
	val defaultValue: T,
	val validate: ((T) -> Boolean)? = null,
) where T : Any {
	class TEXT(
		name: String,
		description: String,
		defaultValue: String,
		validate: ((String) -> Boolean)?,
	) : ActionNodeInput<String>(
		name, description, VariableDefType.TEXT, defaultValue, validate
	)

	class BOOL(
		name: String,
		description: String,
		defaultValue: Boolean,
		validate: ((Boolean) -> Boolean)?,
	) : ActionNodeInput<Boolean>(
		name, description, VariableDefType.BOOL, defaultValue, validate
	)

	class NUMBER(
		name: String,
		description: String,
		defaultValue: Float,
		validate: ((Float) -> Boolean)?,
	) : ActionNodeInput<Float>(
		name, description, VariableDefType.NUMBER, defaultValue, validate
	)

	class VARIABLE(
		name: String,
		description: String,
		defaultValue: VariableDef,
		validate: ((VariableDef) -> Boolean)?,
	) : ActionNodeInput<VariableDef>(
		name, description, VariableDefType.VARIABLE, defaultValue, validate
	)

	class GHOST<V>(
		name: String,
		description: String,
		val enabled: (Map<String, ActionNodeInput<*>>) -> Boolean,
		defaultValue: V,
		validate: ((V) -> Boolean)? = null,
	) : ActionNodeInput<V>(
		name, description, when (defaultValue) {
			is String -> VariableDefType.TEXT
			is Float -> VariableDefType.NUMBER
			is Boolean -> VariableDefType.BOOL
			else -> VariableDefType.TEXT
		}, defaultValue, validate
	) where V : Any
}

data class ActionNode(
	val data: ActionNodeData,
	val input: Map<String, Any> = data.inputDefinitions.mapValues { it.value.defaultValue },
	val id: String = UUID.randomUUID().toString(),
	val execute:
		(input: Map<String, Any>, context: ExecutionFlowContext) -> Unit
)

data class ActionNodeData(
	val name: String,
	val description: String,
	val inputDefinitions: Map<String, ActionNodeInput<*>>
)