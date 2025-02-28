package com.jxd20072.automationapp.core.data.local

import android.content.Context
import com.jxd20072.automationapp.core.data.model.AutomationDef
import kotlinx.serialization.json.Json
import java.io.File
import java.io.IOException
import javax.inject.Inject

class AutomationsStore @Inject constructor(
	private val context: Context
) {
	private fun readAutomationFile(file: File): AutomationDef {
		return Json.decodeFromString<AutomationDef>(file.readText())
	}

	private fun getAutomationsDirectory(): File {
		val externalStorageDir = context.getExternalFilesDir(null)
		val automationsDir = File(externalStorageDir, "automations")
		if (!automationsDir.exists()) {
			automationsDir.mkdirs()
		}
		return automationsDir
	}

	fun readAutomation(fileName: String): AutomationDef? {
		val automationsDir = getAutomationsDirectory()
		val file = automationsDir
			.listFiles()
			?.find { it.isFile && it.name == "$fileName.json" }

		return if (file != null) readAutomationFile(file) else null
	}

	fun writeAutomationToFile(name: String, automationDef: AutomationDef): Boolean {
		return try {
			val automationsDirectory = getAutomationsDirectory()

			val file = File(automationsDirectory, "${name}.json")
			val fileContent = Json.encodeToString(automationDef)
			file.writeText(fileContent)

			true
		} catch (e: IOException) {
			e.printStackTrace()
			false
		}
	}
}