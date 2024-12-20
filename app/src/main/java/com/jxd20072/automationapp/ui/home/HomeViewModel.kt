package com.jxd20072.automationapp.ui.home

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jxd20072.automationapp.core.data.repository.AutomationsRepository
import com.jxd20072.automationapp.ui.home.addAutomationSheet.iconCategories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

enum class AddAutomationDrawerScreen {
	AUTOMATION_ADD, ICON_SELECT
}

@Immutable
data class AddAutomationDrawerUiState(
	val isDrawerOpen: Boolean = false,
	val screen: AddAutomationDrawerScreen = AddAutomationDrawerScreen.AUTOMATION_ADD,

	val selectedName: String = "",
	val selectedIcon: String = "wave-square",

	val icons: Map<String, List<String>> = iconCategories,
	val filteredIcons: Map<String, List<String>> = icons,
	val iconSearchQuery: String = "",
)

@Immutable
data class HomeScreenUiState(
	val automations: List<AutomationCardState> = emptyList(),
	val filteredAutomations: List<AutomationCardState> = emptyList(),

	val filters: Map<String, Boolean> = emptyMap(),
	val currentTime: Instant = Instant.now(),
	val searchQuery: String = "",

	val addAutomationDrawerState: AddAutomationDrawerUiState =
		AddAutomationDrawerUiState()
)

@Immutable
data class AutomationCardState(
	val name: String,
	val icon: String,
	val id: UUID,
	val tags: List<String>,
	val lastUsed: Instant?
)

@HiltViewModel
class HomeViewModel @Inject constructor(
	private val automationsRepository: AutomationsRepository
) : ViewModel() {

	private val _homeUiState = MutableStateFlow(HomeScreenUiState())
	val homeUiState: StateFlow<HomeScreenUiState> = _homeUiState.asStateFlow()

	private val currentTimeFlow: Flow<Instant> = flow {
		while (true) {
			println("refreshing localTime")
			delay(10_000)
			emit(Instant.now())
		}
	}

	init {
		updateAllFilters()

		viewModelScope.launch {
			currentTimeFlow.collect { currentTime ->
				_homeUiState.update {
					it.copy(currentTime = currentTime)
				}
			}
		}

		viewModelScope.launch {
			automationsRepository.automationsFlow.collect { automations ->
				_homeUiState.update {
					it.copy(
						automations = automations.map { automation ->
							AutomationCardState(
								name = automation.metadata.name,
								icon = automation.metadata.icon,
								id = automation.metadata.id,
								tags = automation.metadata.tags,
								lastUsed = automation.metadata.lastRan
							)
						},
						filteredAutomations = getFilteredAutomations(it)
					)
				}
				updateAllFilters()
			}
		}
	}

	private fun getFilteredAutomations(currentState: HomeScreenUiState) =
		currentState.automations.filter { automation ->
			(
				currentState.filters.all { filter -> !filter.value }
					|| automation.tags
					.any { tag -> currentState.filters[tag] == true }
				) && (
				currentState.searchQuery == ""
					|| automation.name.lowercase()
					.contains(currentState.searchQuery.lowercase())
				)
		}

	private fun filterAutomations() {
		_homeUiState.update { currentState ->
			currentState.copy(
				filteredAutomations = getFilteredAutomations(currentState)
			)
		}
	}

	fun changeFilter(filter: String, enabled: Boolean) {
		_homeUiState.update { currentState ->
			currentState.copy(
				filters = currentState.filters + mapOf(filter to enabled)
			)
		}
		filterAutomations()
	}

	private fun getAllFilters(currentState: HomeScreenUiState) =
		currentState.copy(
			filters = currentState.filters +
				currentState.automations.flatMap { it.tags }.distinct()
					.associateWith { false }
		)

	private fun updateAllFilters() {
		_homeUiState.update { currentState -> getAllFilters(currentState) }
		filterAutomations()
	}

	fun changeSearchQuery(query: String) {
		_homeUiState.update { currentState ->
			currentState.copy(
				searchQuery = query
			)
		}
		filterAutomations()
	}

	private fun resetAddAutomationDrawerState() {
		_homeUiState.update { currentState ->
			currentState.copy(
				addAutomationDrawerState = AddAutomationDrawerUiState()
			)
		}
	}

	fun changeAddAutomationDrawerVisibility(open: Boolean) {
		if (open) {
			resetAddAutomationDrawerState()
		}

		_homeUiState.update { currentState ->
			currentState.copy(
				addAutomationDrawerState = currentState.addAutomationDrawerState.copy(
					isDrawerOpen = open
				)
			)
		}
	}

	fun changeAddAutomationDrawerScreen(screen: AddAutomationDrawerScreen) {
		_homeUiState.update { currentState ->
			currentState.copy(
				addAutomationDrawerState = currentState.addAutomationDrawerState.copy(
					screen = screen
				)
			)
		}
	}

	fun changeAddAutomationDrawerName(name: String) {
		_homeUiState.update { currentState ->
			currentState.copy(
				addAutomationDrawerState = currentState.addAutomationDrawerState.copy(
					selectedName = name
				)
			)
		}
	}

	fun changeAddAutomationDrawerIcon(icon: String) {
		_homeUiState.update { currentState ->
			currentState.copy(
				addAutomationDrawerState = currentState.addAutomationDrawerState.copy(
					selectedIcon = icon
				)
			)
		}
	}

	fun changeIconSelectorSearchQuery(query: String) {
		_homeUiState.update { currentState ->
			currentState.copy(
				addAutomationDrawerState = currentState.addAutomationDrawerState.copy(
					iconSearchQuery = query
				)
			)
		}
		filterIcons()
	}

	private fun filterIcons() {
		_homeUiState.update { currentState ->
			val searchQuery =
				currentState.addAutomationDrawerState.iconSearchQuery.lowercase()
			currentState.copy(
				addAutomationDrawerState = currentState.addAutomationDrawerState.copy(
					filteredIcons = currentState.addAutomationDrawerState.icons.mapValues { entry ->
						entry.value.filter { icon -> icon.lowercase().contains(searchQuery) }
					}.filter { entry -> entry.value.isNotEmpty() }
				)
			)
		}
	}

	fun addAutomation(name: String, icon: String) {
		automationsRepository.createAutomation(name = name, icon = icon, tags = emptyList())

//		_homeUiState.update { currentState ->
//			currentState.copy(
//				automations = currentState.automations + AutomationCardState(
//					name = name,
//					id = UUID.randomUUID(),
//					icon = icon,
//					lastUsed = Instant.now(),
//					tags = emptyList()
//				)
//			)
//		}
	}
}
