package com.jxd20072.automationapp.core.data.repository

import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.core.data.local.AutomationsDataSource
import com.jxd20072.automationapp.core.data.model.Automation
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDateTime
import javax.inject.Inject

class AutomationsRepository @Inject constructor(
	private val automationsDataSource: AutomationsDataSource
) {
	private val automations = flowOf(
		listOf(
			Automation(
				name = "name",
				id = "",
				icon = R.drawable.function,
				created = LocalDateTime.now(),
				lastUsed = LocalDateTime.now(),
				tags = setOf("hi", "this is awesome")
			)
		)
	)

	suspend fun addAutomation(name: String, icon: Int) {

		automations.collect { value ->
			value + listOf(
				Automation(
					name = name,
					id = "",
					icon = icon,
					created = LocalDateTime.now(),
					lastUsed = LocalDateTime.now(),
					tags = setOf("hi", "this is awesome")
				)
			)
		}
		println("Automation added! $name")
	}

	fun getAutomations(): List<Automation> {
		return listOf()
	}
}