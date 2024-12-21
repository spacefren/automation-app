package com.jxd20072.automationapp.ui.editor

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jxd20072.automationapp.core.data.model.Automation
import com.jxd20072.automationapp.core.data.repository.AutomationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
data class EditorUiState(
	val automationID: String? = null,
	val automation: Automation? = null,
)

@HiltViewModel
class EditorViewModel @Inject constructor(
	private val automationsRepository: AutomationsRepository
) : ViewModel() {
	private val _editorUiState = MutableStateFlow(EditorUiState())
	val editorUiState = _editorUiState.asStateFlow()

	fun loadAutomation(id: String) {
		viewModelScope.launch {
			val automation = automationsRepository.getAutomation(id)
			if (automation != null) {
				_editorUiState.update {
					EditorUiState(
						automationID = id,
						automation = automation
					)
				}
			}
		}
	}
}