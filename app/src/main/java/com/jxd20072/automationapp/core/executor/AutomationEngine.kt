package com.jxd20072.automationapp.core.executor

import android.content.Context
import android.util.Log
import com.jxd20072.automationapp.core.data.model.AutomationDef
import com.jxd20072.automationapp.core.data.repository.AutomationsRepository
import jakarta.inject.Inject
import kotlinx.coroutines.launch

class AutomationEngine @Inject constructor(
    private val context: Context,
    private val automationsRepository: AutomationsRepository
) {
    fun executeAutomation(automation: AutomationDef, id: String): AutomationExecutor {
        val executor = AutomationExecutor(automation, context)

        val logCollectorJob = executor.executorScope.launch {
            executor.logs.collect { log ->
                Log.d(
                    "APP_AutomationLogs",
                    "[${log.level}] [${log.time}] ${log.message}"
                )

                automationsRepository.addLog(id, log)
            }
        }

        executor.start {
            logCollectorJob.cancel()
        }

        return executor
    }

    suspend fun executeAutomation(automationId: String): AutomationExecutor? {
        val automation = automationsRepository.getAutomationDef(automationId) ?: return null
        return executeAutomation(automation, automationId)
    }
}