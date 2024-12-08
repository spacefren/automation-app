package com.jxd20072.automationapp.core.data.model

import java.time.LocalDateTime
import java.util.UUID

data class Automation(
	val name: String,
	val id: UUID,
	val icon: String,
	val created: LocalDateTime,
	val lastUsed: LocalDateTime,
	val tags: Set<String>,
)
