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
	fun readAutomationFile(name: String): String? {
		val file = File(context.getExternalFilesDir(null), name)

		return if (file.exists()) file.readText() else null
	}

	fun getAutomations(): List<File> {
		val directory = getAutomationsDirectory()
		return directory.listFiles()?.filter { it.isFile } ?: emptyList()
	}

	private fun getAutomationsDirectory(): File {
		val externalStorageDir = context.getExternalFilesDir(null)
		val automationsDir = File(externalStorageDir, "automations")
		if (!automationsDir.exists()) {
			automationsDir.mkdirs()
		}
		return automationsDir
	}

	fun createAutomationFile(name: String, metadata: AutomationMetadata): Boolean {
		return try {
			val automationsDirectory = getAutomationsDirectory()

			val file = File(automationsDirectory, "$name.json")
			val fileContent = Json.encodeToString(Automation(metadata = metadata))
			file.writeText(fileContent)

			true
		} catch (e: IOException) {
			e.printStackTrace()
			false
		}
	}
}