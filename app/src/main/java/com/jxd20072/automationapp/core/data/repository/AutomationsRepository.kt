package com.jxd20072.automationapp.core.data.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import com.jxd20072.automationapp.core.data.AutomationLogEntry
import com.jxd20072.automationapp.core.data.AutomationsDataMap
import com.jxd20072.automationapp.core.data.AutomationsMapEntry
import com.jxd20072.automationapp.core.data.copy
import com.jxd20072.automationapp.core.data.local.AutomationStore
import com.jxd20072.automationapp.core.data.model.AutomationDef
import com.jxd20072.automationapp.core.data.model.AutomationDefMetadata
import com.jxd20072.automationapp.core.data.model.ConfigurationValue
import com.jxd20072.automationapp.core.data.model.NodeDef
import com.jxd20072.automationapp.core.data.model.NodeType
import com.jxd20072.automationapp.core.data.model.VariableDef
import com.jxd20072.automationapp.core.data.model.VariableDefValue
import com.jxd20072.automationapp.core.executor.AutomationExecutor
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

class AutomationsRepository @Inject constructor(
    private val automationsStore: AutomationStore,
    private val context: Context,
    private val automationsDataStore: DataStore<AutomationsDataMap>
) {

    private val testingAutomationDef = AutomationDef(
        metadata = AutomationDefMetadata(
            version = 0
        ),
        variables = setOf(
            VariableDef(name = "firstVar", value = VariableDefValue.TEXT("textVariableValue")),
            VariableDef(name = "secondVar", value = VariableDefValue.NUMBER("10")),
            VariableDef(name = "justThird", value = VariableDefValue.BOOL(true))
        ),
        topLevel = listOf(
            NodeDef(
                name = "wait",
                nodeType = NodeType.TRIGGER,
                input = mapOf("Duration" to ConfigurationValue.NUMBER("3000")),
                content = listOf(
                    NodeDef(
                        name = "log",
                        nodeType = NodeType.ACTION,
                        input = mapOf("Text" to ConfigurationValue.TEXT("I waited for this")),
                    ),
                )
            ),
            NodeDef(
                name = "onStart",
                id = "0",
                nodeType = NodeType.FUNCTION,
                content = listOf(
                    NodeDef(
                        name = "log",
                        nodeType = NodeType.ACTION,
                        input = mapOf("Text" to ConfigurationValue.TEXT("I didn't wait for this")),
                    ),
                )
            ),
            NodeDef(
                name = "myFunction",
                nodeType = NodeType.FUNCTION,
                content = listOf(
                    NodeDef(
                        name = "log",
                        nodeType = NodeType.ACTION,
                        input = mapOf("Text" to ConfigurationValue.TEXT("1")),
                    ),
                    NodeDef(
                        name = "log",
                        nodeType = NodeType.ACTION,
                        input = mapOf("Text" to ConfigurationValue.TEXT("2")),
                    ),
                )
            ),
        ),
    )

    val automationsDataFlow = automationsDataStore.data

    private val ongoingAutomations = mutableSetOf<AutomationExecutor>()

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
    }

    private suspend fun updateAutomationData(
        id: String,
        transform: (AutomationsMapEntry) -> AutomationsMapEntry
    ) {
        updateAutomationDataMap { automationsDataMap ->
            val automationData =
                automationsDataMap.automationsMap[id]
                    ?: return@updateAutomationDataMap automationsDataMap
            automationsDataMap.copy {
                automations[id] = transform(automationData)
            }
        }
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

    suspend fun executeAutomation(automationID: String): AutomationExecutor? {
        val automation = getAutomationDef(automationID) ?: return null

        val executor = AutomationExecutor(automation, context)
        ongoingAutomations.add(executor)

        val logCollectorJob = executor.executorScope.launch {
            executor.logs.collect { log ->
                Log.d(
                    "APP_AutomationLogs",
                    "[${log.level}] [${log.time}] ${log.message}"
                )

                addLog(automationID, log)
            }
        }

        executor.start {
            ongoingAutomations.removeIf { it.id == executor.id }
            logCollectorJob.cancel()
        }

        return executor
    }

    fun getAutomationExecutor(id: String): AutomationExecutor? {
        return ongoingAutomations.find { it.id == id }
    }

    suspend fun addLog(automationID: String, log: AutomationLogEntry) {
        updateAutomationData(automationID) { automationData ->
            automationData
                .toBuilder()
                .addLogs(log)
                .build()
        }
    }

    suspend fun removeLog(automationID: String, logID: String) {
        updateAutomationData(automationID) { automationData ->
            automationData
                .toBuilder()
                .removeLogs(automationData.logsList.indexOfFirst { it.id == logID })
                .build()
        }
    }
}