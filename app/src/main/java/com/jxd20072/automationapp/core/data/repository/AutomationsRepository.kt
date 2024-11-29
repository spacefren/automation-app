package com.jxd20072.automationapp.core.data.repository

import com.jxd20072.automationapp.core.data.local.AutomationsDataSource
import javax.inject.Inject

class AutomationsRepository @Inject constructor(
	private val automationsDataSource: AutomationsDataSource
) {
	fun addAutomation(name: String, icon: Int) {
		println("Automation added! $name")
	}
}