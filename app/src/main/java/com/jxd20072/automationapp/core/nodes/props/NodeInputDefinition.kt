package com.jxd20072.automationapp.core.nodes.props

import com.jxd20072.automationapp.core.data.model.NodeInputEntry

data class NodeInputDefinition<T>(
	val description: String,
	val defaultValue: T,
	val validate: ((T) -> Boolean)? = null,
) where T : NodeInputEntry<*>