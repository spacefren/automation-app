package com.jxd20072.automationapp.core.data.repository

import com.jxd20072.automationapp.core.data.local.AutomationsDataSource
import com.jxd20072.automationapp.core.data.model.Automation
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

class AutomationsRepository @Inject constructor(
	private val automationsDataSource: AutomationsDataSource
) {
	private val automations = flowOf(
		listOf(
			Automation(
				name = "name",
				id = UUID.randomUUID(),
				icon = "functions",
				created = LocalDateTime.now(),
				lastUsed = LocalDateTime.now(),
				tags = setOf("hi", "this is awesome")
			)
		)
	)

	suspend fun addAutomation(name: String, icon: String) {

		automations.collect { value ->
			value + listOf(
				Automation(
					name = name,
					id = UUID.randomUUID(),
					icon = icon,
					created = LocalDateTime.now(),
					lastUsed = LocalDateTime.now(),
					tags = setOf("hi", "this is awesome")
				)
			)
		}
	}

	fun getAutomations(): List<Automation> {
		return listOf()
	}
}