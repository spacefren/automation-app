package com.jxd20072.automationapp.ui.home

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel : ViewModel() {
	private val _state = MutableStateFlow(HomeScreenUiState())
	val state: StateFlow<HomeScreenUiState> = _state.asStateFlow()

	fun onFilterChanged(filter: String, enabled: Boolean) {
		_state.update { currentState ->
			currentState.copy(
				filters = currentState.filters + mapOf(filter to enabled)
			)
		}
	}
}

@Immutable
data class HomeScreenUiState(
	val filters: Map<String, Boolean> = mapOf(
		"Music" to true,
		"Photos" to true,
		"Fitness" to false,
		"Work" to false,
		"ThisIsAReallyLongOne" to false,
		"LookItsAnotherOne!" to false
	),
)