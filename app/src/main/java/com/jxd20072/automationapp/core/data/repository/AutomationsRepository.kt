package com.jxd20072.automationapp.core.data.repository

import com.jxd20072.automationapp.core.data.local.AutomationsStore
import com.jxd20072.automationapp.core.data.model.Automation
import com.jxd20072.automationapp.core.data.model.AutomationMetadata
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

class AutomationsRepository @Inject constructor(
	private val automationsStore: AutomationsStore,
) {

	val automationsFlow: Flow<List<Automation>> = flow {
		while (true) {
			println("UPDATING AUTOMATIONS")
			emit(getAutomations())
			delay(5_000L)
		}
	}

	fun createAutomation(name: String, icon: String, tags: List<String>) {
		automationsStore.createAutomationFile(
			metadata = AutomationMetadata(
				name = name,
				icon = icon,
				created = Instant.now(),
				updated = Instant.now(),
				lastRan = null,
				tags = tags,
				id = UUID.randomUUID()
			)
		)
	}

	private fun getAutomations(): List<Automation> {
		return automationsStore.getAutomations()
	}
}