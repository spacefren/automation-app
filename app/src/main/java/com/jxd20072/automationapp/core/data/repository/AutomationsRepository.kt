package com.jxd20072.automationapp.core.data.repository

import androidx.datastore.core.DataStore
import com.jxd20072.automationapp.core.data.AutomationsDataMap
import com.jxd20072.automationapp.core.data.AutomationsMapEntry
import com.jxd20072.automationapp.core.data.copy
import com.jxd20072.automationapp.core.data.local.AutomationsStore
import com.jxd20072.automationapp.core.data.model.Automation
import com.jxd20072.automationapp.core.data.model.AutomationMetadata
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject

class AutomationsRepository @Inject constructor(
	private val automationsStore: AutomationsStore,
	private val automationsDataStore: DataStore<AutomationsDataMap>
) {

	val automationsDataFlow: Flow<AutomationsDataMap> = flow {
		while (true) {
			emit(getAutomationDataMap())
			delay(5_000)
		}
	}

	suspend fun createAutomation(
		name: String,
		icon: String,
		tags: List<String>
	): String {
		val id = UUID.randomUUID().toString()

		automationsStore.createAutomationFile(
			name = name,
			metadata = AutomationMetadata(
				version = 1
			)
		)

		val entry = AutomationsMapEntry.newBuilder()
			.setName(name)
			.setIcon(icon)
			.addAllTags(tags)
			.build()

		automationsDataStore.updateData { automationsData ->
			automationsData.copy {
				automations[id] = entry
			}
		}

		return id
	}

	private suspend fun getAutomationDataMap(): AutomationsDataMap {
		return automationsDataStore.data.first()
	}

	suspend fun getAutomationMapEntry(id: String): AutomationsMapEntry? {
		return automationsDataStore.data.first().automationsMap[id]
	}

	suspend fun getAutomation(id: String): Automation? {
		val mapEntry = getAutomationMapEntry(id) ?: return null
		return automationsStore.readAutomation(mapEntry.name)
	}
}