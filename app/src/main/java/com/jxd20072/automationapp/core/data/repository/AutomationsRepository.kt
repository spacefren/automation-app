package com.jxd20072.automationapp.core.data.repository

import androidx.datastore.core.DataStore
import com.jxd20072.automationapp.core.data.AutomationsDataMap
import com.jxd20072.automationapp.core.data.AutomationsMapEntry
import com.jxd20072.automationapp.core.data.copy
import com.jxd20072.automationapp.core.data.local.AutomationsStore
import com.jxd20072.automationapp.core.data.model.ActionNodeDef
import com.jxd20072.automationapp.core.data.model.AutomationDef
import com.jxd20072.automationapp.core.data.model.AutomationDefMetadata
import com.jxd20072.automationapp.core.data.model.EntryPointNodeDef
import com.jxd20072.automationapp.core.data.model.VariableDef
import com.jxd20072.automationapp.core.executor.executeAutomation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

private const val TAG = "APP_AutomationsRepository"

class AutomationsRepository @Inject constructor(
	private val automationsStore: AutomationsStore,
	private val automationsDataStore: DataStore<AutomationsDataMap>
) {
	init {
		executeAutomation(
			AutomationDef(
				metadata = AutomationDefMetadata(
					version = 0
				),
				entryPoint = EntryPointNodeDef(
					content = listOf(
//						ActionNodeDef(
//							name = "writeVariable",
//							input = mapOf("variable" to "textVariable", "value" to "changedValue!!")
//						),
						ActionNodeDef(
							name = "log",
							input = mapOf("text" to "variable test: @textVariable")
						),
					)
				),
				variables = setOf(
					VariableDef.TEXT
						(
						name = "textVariable",
						value = "textVariableValue"
					),
				),
				triggers = emptyList(),
				functions = emptyList()
			)
		)
	}

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
			metadata = AutomationDefMetadata(
				version = 1
			)
		)

		val entry = AutomationsMapEntry.newBuilder()
			.setName(name)
			.setIcon(icon)
			.addAllTags(tags)
			.setCreationDate(Instant.now().toString())
			.setLastSaved(Instant.now().toString())
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

	suspend fun getAutomation(id: String): AutomationDef? {
		val mapEntry = getAutomationMapEntry(id) ?: return null
		return automationsStore.readAutomation(mapEntry.name)
	}
}