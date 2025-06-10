package com.jxd20072.automationapp.core.nodes.props

import com.jxd20072.automationapp.core.data.model.ConfigurationEntry

sealed class ValidationResult {
	data object Success : ValidationResult()
	data class Error(val message: String) : ValidationResult()
}


data class NodeInputDefinition<T>(
	val description: String,
	val defaultValue: T,
	val validate: ((T) -> ValidationResult)? = null,
) where T : ConfigurationEntry<*>