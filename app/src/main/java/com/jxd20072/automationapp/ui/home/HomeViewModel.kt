package com.jxd20072.automationapp.ui.home

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.core.data.model.Automation
import com.jxd20072.automationapp.core.data.repository.AutomationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@Immutable
data class HomeScreenUiState(
	val automations: List<Automation> = listOf(
		Automation(
			name = "name",
			id = "",
			icon = R.drawable.function,
			created = LocalDateTime.now(),
			lastUsed = LocalDateTime.now(),
			tags = setOf("hi", "this is awesome")
		)
	),
	val filters: Map<String, Boolean> = mapOf(),
	val currentTime: LocalDateTime = LocalDateTime.now()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
	private val automationsRepository: AutomationsRepository
) : ViewModel() {

	private val _state = MutableStateFlow(HomeScreenUiState())
	val state: StateFlow<HomeScreenUiState> = _state.asStateFlow()

	private val currentTimeFlow: Flow<LocalDateTime> = flow {
		while (true) {
			delay(30_000)
			emit(LocalDateTime.now())
		}
	}

	init {
		updateFilters()

		viewModelScope.launch {
			currentTimeFlow.collect { currentTime ->
				_state.update {
					it.copy(currentTime = currentTime)
				}
			}
		}
	}

	fun onFilterChanged(filter: String, enabled: Boolean) {
		_state.update { currentState ->
			currentState.copy(
				filters = currentState.filters + mapOf(filter to enabled)
			)
		}
	}

	fun updateFilters() {
		val currentState = _state.value
		currentState.filters +
						currentState.automations.flatMap { it.tags }.distinct().associateWith { false }
		_state.update { currentState ->
			currentState.copy(
				filters = currentState.filters +
								currentState.automations.flatMap { it.tags }.distinct().associateWith { false }
			)
		}
	}

	fun addAutomation(name: String, icon: Int) {
		automationsRepository.addAutomation(name = name, icon = icon)
		_state.update { currentState ->
			currentState.copy(
				automations = currentState.automations + Automation(
					name = name,
					id = "",
					icon = icon,
					created = LocalDateTime.now(),
					lastUsed = LocalDateTime.now(),
					tags = setOf()
				)
			)
		}

		updateFilters()
	}
}

