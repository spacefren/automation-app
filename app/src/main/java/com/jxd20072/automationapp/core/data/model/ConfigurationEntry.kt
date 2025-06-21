package com.jxd20072.automationapp.core.data.model

import kotlinx.serialization.Serializable

@Serializable
sealed class ConfigurationEntry<T> {
	abstract val value: T

	@Serializable
	data class TEXT(
		override val value: String
	) : ConfigurationEntry<String>()

	@Serializable
	data class BOOL(
		override val value: Boolean
	) : ConfigurationEntry<Boolean>()

	@Serializable
	data class NUMBER(
		override val value: String
	) : ConfigurationEntry<String>()

	@Serializable
	data class CHOOSE(
		override val value: String
	) : ConfigurationEntry<String>()

	@Serializable
	data class VARIABLE(
		override val value: String
	) : ConfigurationEntry<String>()
}