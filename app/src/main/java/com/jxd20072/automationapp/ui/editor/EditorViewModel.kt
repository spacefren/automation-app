package com.jxd20072.automationapp.ui.editor

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jxd20072.automationapp.core.data.AutomationsMapEntry
import com.jxd20072.automationapp.core.data.repository.automations.AutomationDraft
import com.jxd20072.automationapp.core.data.repository.automations.AutomationsRepository
import com.jxd20072.automationapp.ui.utils.TimeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
sealed interface EditorUiState {
	data object Loading : EditorUiState
	data class Success(
		val automationData: AutomationsMapEntry,
		val automationDraft: AutomationDraft,
	) : EditorUiState
}

@HiltViewModel
class EditorViewModel @Inject constructor(
	private val automationsRepository: AutomationsRepository,
	timeManager: TimeManager
) : ViewModel() {
	private val _editorUiState = MutableStateFlow<EditorUiState>(EditorUiState.Loading)
	val editorUiState = _editorUiState.asStateFlow()

	val currentTime = timeManager.currentTime

	fun loadAutomation(id: String) {
		viewModelScope.launch {
			val automation = automationsRepository.getAutomation(id)
			val automationData = automationsRepository.getAutomationMapEntry(id)
			if (automation != null && automationData != null) {
				_editorUiState.update {
					EditorUiState.Success(
						automationData = automationData,
						automationDraft = automation
					)
				}
			}
		}
	}
}