package com.jxd20072.automationapp.core.data.local

import android.content.Context
import com.jxd20072.automationapp.core.data.model.Automation
import com.jxd20072.automationapp.core.data.model.AutomationMetadata
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.IOException
import javax.inject.Inject

class AutomationsStore @Inject constructor(
	private val context: Context
) {
	private fun readAutomationFile(file: File): Automation {
		return Json.decodeFromString<Automation>(file.readText())
	}

	private fun getAutomationsDirectory(): File {
		val externalStorageDir = context.getExternalFilesDir(null)
		val automationsDir = File(externalStorageDir, "automations")
		if (!automationsDir.exists()) {
			automationsDir.mkdirs()
		}
		return automationsDir
	}

	fun getAutomations(): List<Automation> {
		val directory = getAutomationsDirectory()
		return directory.listFiles()
			?.filter { it.isFile }
			?.map { file -> readAutomationFile(file) }
			?: emptyList()
	}

	fun createAutomationFile(metadata: AutomationMetadata): Boolean {
		return try {
			val automationsDirectory = getAutomationsDirectory()

			val file = File(automationsDirectory, "${metadata.name}.json")
			val fileContent = Json.encodeToString(
				Automation(
					metadata = metadata,
					variables = emptySet(),
					functions = emptyList(),
					triggers = emptyList(),
				)
			)
			file.writeText(fileContent)

			true
		} catch (e: IOException) {
			e.printStackTrace()
			false
		}
	}
}