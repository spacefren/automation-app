package com.jxd20072.automationapp.core.nodes.props

import com.jxd20072.automationapp.core.data.model.VariableDefType

data class NodeInputDefinition(
	val name: String,
	val description: String,
	val type: VariableDefType,
	val defaultValue: String,
	val validate: ((String) -> Boolean)? = null,
)