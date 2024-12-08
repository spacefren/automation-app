package com.jxd20072.automationapp.ui.home

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import java.util.UUID
import javax.inject.Inject

@Immutable
data class HomeScreenUiState(
	val automations: List<Automation> = listOf(),
	val filteredAutomations: List<Automation> = listOf(),
	val filters: Map<String, Boolean> = mapOf(
		"Users" to true,
		"will" to false,
		"be" to false,
		"able" to false,
		"to" to false,
		"create" to false,
		"these" to false,
		"filters" to false,
		"Extra one for scrolling" to false
	),
	val currentTime: LocalDateTime = LocalDateTime.now(),
	val searchQuery: String = "",
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
		updateAllFilters()

		viewModelScope.launch {
			currentTimeFlow.collect { currentTime ->
				_state.update {
					it.copy(currentTime = currentTime)
				}
			}
		}
	}

	private fun filterAutomations() {
		_state.update { currentState ->
			currentState.copy(
				filteredAutomations = currentState.automations.filter { automation ->
					(
									currentState.filters.all { filter -> !filter.value }
													|| automation.tags.any { tag -> currentState.filters[tag] == true }
									) && (
									currentState.searchQuery == ""
													|| automation.name.contains(currentState.searchQuery)
									)
				}
			)
		}
		println(_state.value.filteredAutomations)
	}

	fun onFilterChanged(filter: String, enabled: Boolean) {
		_state.update { currentState ->
			currentState.copy(
				filters = currentState.filters + mapOf(filter to enabled)
			)
		}
		filterAutomations()
	}

	private fun updateAllFilters() {
		_state.update { currentState ->
			currentState.copy(
				filters = currentState.filters +
								currentState.automations.flatMap { it.tags }.distinct().associateWith { false }
			)
		}
		filterAutomations()
	}

	fun onSearch(query: String) {
		_state.update { currentState ->
			currentState.copy(
				searchQuery = query
			)
		}
		filterAutomations()
	}

	fun addAutomation(name: String, icon: String) {
		//automationsRepository.addAutomation(name = name, icon = icon)
		_state.update { currentState ->
			currentState.copy(
				automations = currentState.automations + Automation(
					name = name,
					id = UUID.randomUUID(),
					icon = icon,
					created = LocalDateTime.now(),
					lastUsed = LocalDateTime.now(),
					tags = setOf()
				)
			)
		}
		filterAutomations()
	}
}
