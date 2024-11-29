package com.jxd20072.automationapp.core.data.model

import java.time.LocalDateTime

data class Automation(
	val name: String,
	val id: String,
	val icon: Int,
	val created: LocalDateTime,
	val lastUsed: LocalDateTime,
	val tags: Set<String>,
)
