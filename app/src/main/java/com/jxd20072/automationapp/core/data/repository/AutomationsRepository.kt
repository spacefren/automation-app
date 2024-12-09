package com.jxd20072.automationapp.core.data.repository

import com.jxd20072.automationapp.core.data.local.AutomationsStore
import com.jxd20072.automationapp.core.data.model.AutomationMetadata
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject

class AutomationsRepository @Inject constructor(
	private val automationsStore: AutomationsStore,
) {

	val automationsFlow: Flow<List<String>> = flow {
		while (true) {
			println("UPDATING AUTOMATIONS")
			emit(getAutomations())
			delay(5_000L)
		}
	}

	fun createAutomation(name: String, icon: String, tags: List<String>) {
		automationsStore.createAutomationFile(
			name = name,
			metadata = AutomationMetadata(
				icon = icon,
				lastRan = null,
				tags = tags,
				id = UUID.randomUUID()
			)
		)
	}

	private fun getAutomations(): List<String> {
		return automationsStore.getAutomations().map { file -> file.name }
	}
}