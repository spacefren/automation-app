package com.jxd20072.automationapp.core.data.repository.automations

import android.util.Log
import androidx.datastore.core.DataStore
import com.jxd20072.automationapp.core.data.AutomationsDataMap
import com.jxd20072.automationapp.core.data.AutomationsMapEntry
import com.jxd20072.automationapp.core.data.copy
import com.jxd20072.automationapp.core.data.local.AutomationsStore
import com.jxd20072.automationapp.core.data.model.ActionNodeDef
import com.jxd20072.automationapp.core.data.model.AutomationDef
import com.jxd20072.automationapp.core.data.model.AutomationDefMetadata
import com.jxd20072.automationapp.core.data.model.FunctionNodeDef
import com.jxd20072.automationapp.core.data.model.VariableDef
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

class AutomationsRepository @Inject constructor(
	private val automationsStore: AutomationsStore,
	private val automationsDataStore: DataStore<AutomationsDataMap>
) {
	private val testingAutomationDef = AutomationDef(
		metadata = AutomationDefMetadata(
			version = 0
		),
		variables = setOf(
			VariableDef.TEXT
				(
				name = "textVariable",
				value = "textVariableValue"
			),
		),
		triggers = emptyList(),
		functions = listOf(
			FunctionNodeDef(
				name = "OnStart",
				input = emptyList(),
				output = emptyList(),
				content = listOf(
//				ActionNodeDef(
//					name = "writeVariable",
//					input = mapOf("variable" to "textVariable", "value" to "changedValue!!")
//				),
					ActionNodeDef(
						name = "log",
						input = listOf(VariableDef.TEXT("Text", "first test"))
					),
					ActionNodeDef(
						name = "log",
						input = listOf(VariableDef.TEXT("Text", "second test"))
					),
					ActionNodeDef(
						name = "log",
						input = listOf(VariableDef.TEXT("Text", "third test"))
					),
					ActionNodeDef(
						name = "log",
						input = listOf(VariableDef.TEXT("Text", "fourth test"))
					),
				),
			),
			FunctionNodeDef(
				name = "myFunction",
				input = emptyList(),
				output = emptyList(),
				content = listOf(
					ActionNodeDef(
						name = "log",
						input = listOf(VariableDef.TEXT("Text", "1"))
					),
					ActionNodeDef(
						name = "log",
						input = listOf(VariableDef.TEXT("Text", "2"))
					),
					ActionNodeDef(
						name = "log",
						input = listOf(VariableDef.TEXT("Text", "3"))
					),
					ActionNodeDef(
						name = "log",
						input = listOf(VariableDef.TEXT("Text", "4"))
					),
				)
			),
		),
	)
	private val _automationsDataFlow = MutableSharedFlow<AutomationsDataMap>(replay = 1)
	val automationsDataFlow: SharedFlow<AutomationsDataMap> = _automationsDataFlow.asSharedFlow()

	private suspend fun startPolling() {
		while (true) {
			_automationsDataFlow.emit(getAutomationDataMap())
			delay(10_000)
		}
	}

	fun initialize() {
		CoroutineScope(Dispatchers.IO).launch {
			startPolling()
		}
	}

	suspend fun createAutomation(
		name: String,
		icon: String,
		tags: List<String>
	): String {
		val id = UUID.randomUUID().toString()

		automationsStore.writeAutomationToFile(
			name = name,
			automationDef = testingAutomationDef
//			automationDef = AutomationDef(
//				metadata = AutomationDefMetadata(version = 1),
//				functions = emptyList(),
//				triggers = emptyList(),
//				variables = emptySet()
//			)
		)

		val entry = AutomationsMapEntry.newBuilder()
			.setName(name)
			.setIcon(icon)
			.addAllTags(tags)
			.setCreationDate(Instant.now().toString())
			.setLastSaved(Instant.now().toString())
			.build()

		updateAutomationDataMap { automationsData ->
			automationsData.copy {
				automations[id] = entry
			}
		}

		return id
	}

	private suspend fun updateAutomationDataMap(
		transform: (AutomationsDataMap) -> AutomationsDataMap
	) {
		automationsDataStore.updateData(transform = transform)
		_automationsDataFlow.emit(getAutomationDataMap())
	}

	private suspend fun getAutomationDataMap(): AutomationsDataMap {
		return automationsDataStore.data.first()
	}

	suspend fun getAutomationMapEntry(id: String): AutomationsMapEntry? {
		return automationsDataStore.data.first().automationsMap[id]
	}

	suspend fun getAutomationDraft(id: String): AutomationDraft? {
		val mapEntry = getAutomationMapEntry(id) ?: return null
		val automationDef = automationsStore.readAutomation(mapEntry.name) ?: return null
		return createAutomationDraft(automationDef)
	}

	fun writeAutomation(id: String, draft: AutomationDraft) {
		CoroutineScope(Dispatchers.IO).launch {
			Log.d("APP_A", "saving...");
			val automationData = getAutomationMapEntry(id) ?: return@launch

			automationsStore.writeAutomationToFile(automationData.name, createAutomationDef(draft))

			updateAutomationDataMap { automationsData ->
				automationsData.copy {
					automations[id] = automationData.copy { lastSaved = Instant.now().toString() }
				}
			}
		}
	}
}