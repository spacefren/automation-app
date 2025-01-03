package com.jxd20072.automationapp.core.nodes.props

import com.jxd20072.automationapp.core.data.model.VariableDef
import com.jxd20072.automationapp.core.data.model.VariableDefType

sealed class NodeInput<T>(
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
	) : NodeInput<String>(
		name, description, VariableDefType.TEXT, defaultValue, validate
	)

	class BOOL(
		name: String,
		description: String,
		defaultValue: Boolean,
		validate: ((Boolean) -> Boolean)?,
	) : NodeInput<Boolean>(
		name, description, VariableDefType.BOOL, defaultValue, validate
	)

	class NUMBER(
		name: String,
		description: String,
		defaultValue: Float,
		validate: ((Float) -> Boolean)?,
	) : NodeInput<Float>(
		name, description, VariableDefType.NUMBER, defaultValue, validate
	)

	class VARIABLE(
		name: String,
		description: String,
		defaultValue: VariableDef,
		validate: ((VariableDef) -> Boolean)?,
	) : NodeInput<VariableDef>(
		name, description, VariableDefType.VARIABLE, defaultValue, validate
	)

	class GHOST<V>(
		name: String,
		description: String,
		val enabled: (Map<String, NodeInput<*>>) -> Boolean,
		defaultValue: V,
		validate: ((V) -> Boolean)? = null,
	) : NodeInput<V>(
		name, description, when (defaultValue) {
			is String -> VariableDefType.TEXT
			is Float -> VariableDefType.NUMBER
			is Boolean -> VariableDefType.BOOL
			else -> VariableDefType.TEXT
		}, defaultValue, validate
	) where V : Any
}