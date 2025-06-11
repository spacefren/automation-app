package com.jxd20072.automationapp.core.data.repository.automations

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import com.jxd20072.automationapp.core.data.AutomationsDataMap
import com.jxd20072.automationapp.core.data.AutomationsMapEntry
import com.jxd20072.automationapp.core.data.copy
import com.jxd20072.automationapp.core.data.local.AutomationsStore
import com.jxd20072.automationapp.core.data.model.ActionNodeDef
import com.jxd20072.automationapp.core.data.model.AutomationDef
import com.jxd20072.automationapp.core.data.model.AutomationDefMetadata
import com.jxd20072.automationapp.core.data.model.ConfigurationEntry
import com.jxd20072.automationapp.core.data.model.FunctionNodeDef
import com.jxd20072.automationapp.core.data.model.VariableDef
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

class AutomationsRepository @Inject constructor(
	private val automationsStore: AutomationsStore,
	private val automationsDataStore: DataStore<AutomationsDataMap>,
	private val context: Context
) {
	private val testingAutomationDef = AutomationDef(
		metadata = AutomationDefMetadata(
			version = 0
		),
		variables = mapOf(
			"test" to VariableDef.TEXT("textVariableValue"),
		),
		triggers = emptyList(),
		entryPoint = listOf(
//				ActionNodeDef(
//					name = "writeVariable",
//					input = mapOf("variable" to "textVariable", "value" to "changedValue!!")
//				),
			ActionNodeDef(
				name = "log",
				input = mapOf("Text" to ConfigurationEntry.TEXT("first test"))
			),
			ActionNodeDef(
				name = "log",
				input = mapOf("Text" to ConfigurationEntry.TEXT("second test"))
			),
			ActionNodeDef(
				name = "log",
				input = mapOf("Text" to ConfigurationEntry.TEXT("third test"))
			),
			ActionNodeDef(
				name = "log",
				input = mapOf("Text" to ConfigurationEntry.TEXT("fourth test"))
			),
		),
		functions = listOf(
			FunctionNodeDef(
				name = "myFunction",
				content = listOf(
					ActionNodeDef(
						name = "log",
						input = mapOf("Text" to ConfigurationEntry.TEXT("1"))
					),
					ActionNodeDef(
						name = "log",
						input = mapOf("Text" to ConfigurationEntry.TEXT("2"))
					),
					ActionNodeDef(
						name = "log",
						input = mapOf("Text" to ConfigurationEntry.TEXT("3"))
					),
					ActionNodeDef(
						name = "log",
						input = mapOf("Text" to ConfigurationEntry.TEXT("4"))
					),
				)
			),
		),
	)
	private val _automationsDataFlow =
		MutableStateFlow<AutomationsDataMap>(AutomationsDataMap.getDefaultInstance())
	val automationsDataFlow: StateFlow<AutomationsDataMap> = _automationsDataFlow.asStateFlow()

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

	suspend fun getAutomationDef(id: String): AutomationDef? {
		val mapEntry = getAutomationMapEntry(id) ?: return null
		val automationDef = automationsStore.readAutomation(mapEntry.name) ?: return null
		return automationDef
	}

	suspend fun writeAutomation(id: String, automation: AutomationDef) {
		if (getAutomationDef(id) == automation) return
		val automationData = getAutomationMapEntry(id) ?: return

		Log.d("APP_A", "saving...")

		automationsStore.writeAutomationToFile(automationData.name, automation)

		updateAutomationDataMap { automationsData ->
			automationsData.copy {
				automations[id] = automationData.copy { lastSaved = Instant.now().toString() }
			}
		}
	}
}