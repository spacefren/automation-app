package com.jxd20072.automationapp.core.data.model

import com.jxd20072.automationapp.core.data.serializer.InstantSerializer
import com.jxd20072.automationapp.core.data.serializer.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

@Serializable
data class AutomationMetadata(
	val icon: String,
	@Serializable(with = InstantSerializer::class)
	val lastRan: Instant?,
	val tags: List<String>,
	@Serializable(with = UUIDSerializer::class)
	val id: UUID
)

@Serializable
data class Automation(
	val metadata: AutomationMetadata,
)