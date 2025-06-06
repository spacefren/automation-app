package com.jxd20072.automationapp.ui.editor.viewmodel

import androidx.compose.runtime.Immutable
import com.jxd20072.automationapp.core.data.AutomationsMapEntry
import com.jxd20072.automationapp.core.data.model.AutomationDef
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Immutable
sealed interface AutomationUiState {
	data object Loading : AutomationUiState
	data class Success(
		val automationId: String,
		val automationData: AutomationsMapEntry,
		val automationDraft: AutomationDef,
	) : AutomationUiState
}

class AutomationManager {
	private val _automationUiState = MutableStateFlow<AutomationUiState>(AutomationUiState.Loading)
	val automationUiState = _automationUiState.asStateFlow()


}