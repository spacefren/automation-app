package com.jxd20072.automationapp.ui.editor

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jxd20072.automationapp.core.data.AutomationsMapEntry
import com.jxd20072.automationapp.core.data.model.Automation
import com.jxd20072.automationapp.core.data.repository.AutomationsRepository
import com.jxd20072.automationapp.ui.utils.TimeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class EditorUiState(
	val automationData: AutomationsMapEntry? = null,
	val automation: Automation? = null,
)

@HiltViewModel
class EditorViewModel @Inject constructor(
	private val automationsRepository: AutomationsRepository,
	timeManager: TimeManager
) : ViewModel() {
	private val _editorUiState = MutableStateFlow(EditorUiState())
	val editorUiState = _editorUiState.asStateFlow()

	val currentTime = timeManager.currentTime

	fun loadAutomation(id: String) {
		viewModelScope.launch {
			val automation = automationsRepository.getAutomation(id)
			val automationData = automationsRepository.getAutomationMapEntry(id)
			if (automation != null && automationData != null) {
				_editorUiState.update {
					EditorUiState(
						automationData = automationData,
						automation = automation
					)
				}
			}
		}
	}
}