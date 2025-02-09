package com.jxd20072.automationapp.ui.home

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jxd20072.automationapp.core.data.repository.automations.AutomationsRepository
import com.jxd20072.automationapp.ui.home.addAutomationSheet.iconCategories
import com.jxd20072.automationapp.ui.shared.TextInputValidationResult
import com.jxd20072.automationapp.ui.utils.TimeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
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
	val searchQuery: String = "",

	val addAutomationDrawerState: AddAutomationDrawerUiState =
		AddAutomationDrawerUiState()
)

@Immutable
data class AutomationCardState(
	val name: String,
	val icon: String,
	val id: String,
	val tags: List<String>,
	val lastUsed: Instant?
)

@HiltViewModel
class HomeViewModel @Inject constructor(
	private val automationsRepository: AutomationsRepository,
) : ViewModel() {

	private val _homeUiState = MutableStateFlow(HomeScreenUiState())
	val homeUiState = _homeUiState.asStateFlow()

	private val timeManager = TimeManager(viewModelScope, 5000)
	val currentTime = timeManager.currentTime

	override fun onCleared() {
		super.onCleared()
		timeManager.cleanup()
	}

	init {
		updateAllFilters()

		viewModelScope.launch {
			automationsRepository.automationsDataFlow.collect { automationsData ->
				_homeUiState.update {
					it.copy(
						automations = automationsData.automationsMap.map { automation ->
							val automationData = automationsRepository
								.getAutomationMapEntry(automation.key)

							AutomationCardState(
								name = automation.value.name,
								icon = automation.value.icon,
								id = automation.key,
								tags = automationData?.tagsList ?: emptyList(),
								lastUsed = if (automationData != null && automationData.lastRan != "") {
									Instant.parse(automationData.lastRan)
								} else null,
							)
						})
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

	fun validateAddAutomationDrawerName(text: String): TextInputValidationResult {
		return when {
			text == "" -> TextInputValidationResult.Error("Name cannot be empty.")
			text.length > 20 -> TextInputValidationResult.Error("Name is too long.")
			homeUiState.value.automations.any { it.name.trim() == text.trim() } ->
				TextInputValidationResult.Error("Name is already taken by an automation.")

			else -> TextInputValidationResult.Success
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

	fun addAutomation(name: String, icon: String, onCreate: (id: String) -> Unit) {
		viewModelScope.launch {
			val id = automationsRepository.createAutomation(
				name = name,
				icon = icon,
				tags = emptyList()
			)
			onCreate(id)
		}
	}
}
