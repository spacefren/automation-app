package com.jxd20072.automationapp.ui.home

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jxd20072.automationapp.core.data.model.ValidationResult
import com.jxd20072.automationapp.core.data.repository.AutomationsRepository
import com.jxd20072.automationapp.ui.editor.viewmodel.utils.ProblemSeverity
import com.jxd20072.automationapp.ui.home.addAutomationSheet.automationIcons
import com.jxd20072.automationapp.utils.TimeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.xdrop.fuzzywuzzy.FuzzySearch
import java.time.Instant
import javax.inject.Inject

enum class AddAutomationDrawerScreen {
    BLANK, AUTOMATION_ADD, ICON_SELECT
}

@Immutable
data class AddAutomationDrawerUiState(
    val isDrawerOpen: Boolean = false,
    val screen: AddAutomationDrawerScreen = AddAutomationDrawerScreen.BLANK,

    val selectedName: String = "",
    val selectedIcon: String = "wave-square",

    val icons: List<String> = automationIcons,
    val filteredIcons: List<String> = icons,
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

    private fun getFilteredAutomations(currentState: HomeScreenUiState): List<AutomationCardState> {
        if (currentState.searchQuery.isBlank() && currentState.filters.all { filter -> !filter.value }) {
            return currentState.automations
        }
        return currentState.automations
            .associateWith {
                FuzzySearch.partialRatio(currentState.searchQuery.lowercase(), it.name.lowercase())
            }
            .toList()
            .sortedBy { it.second }
            .filter { it.second > 75 }
            .map { it.first }
            .filter { automation ->
                currentState.filters.all { filter -> !filter.value }
                        || automation.tags.any { tag -> currentState.filters[tag] == true }
            }
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
                    currentState.automations.flatMap { it.tags }.distinct().associateWith { false }
        )

    private fun updateAllFilters() {
        _homeUiState.update { currentState -> getAllFilters(currentState) }
        filterAutomations()
    }

    fun changeSearchQuery(query: String) {
        _homeUiState.update { currentState -> currentState.copy(searchQuery = query) }
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
        _homeUiState.update { currentState ->
            currentState.copy(
                addAutomationDrawerState = currentState.addAutomationDrawerState.copy(
                    isDrawerOpen = open
                )
            )
        }

        if (!open) {
            viewModelScope.launch {
                delay(100) // Wait for animation to finish
                resetAddAutomationDrawerState()
            }
        }
    }

    fun changeAddAutomationDrawerScreen(screen: AddAutomationDrawerScreen) {
        _homeUiState.update { currentState ->
            currentState.copy(
                addAutomationDrawerState = currentState.addAutomationDrawerState.copy(screen = screen)
            )
        }
    }

    fun validateAddAutomationDrawerName(text: String): ValidationResult {
        return when {
            text.isBlank() -> ValidationResult.Error(
                severity = ProblemSeverity.ERROR,
                message = "Name cannot be empty."
            )

            text.length > 20 -> ValidationResult.Error(
                severity = ProblemSeverity.ERROR,
                message = "Name is too long."
            )

            homeUiState.value.automations.any { it.name.trim() == text.trim() } ->
                ValidationResult.Error(
                    severity = ProblemSeverity.ERROR,
                    message = "Name is already taken by an automation."
                )

            else -> ValidationResult.Success
        }
    }

    fun changeAddAutomationDrawerName(name: String) {
        _homeUiState.update { currentState ->
            currentState.copy(
                addAutomationDrawerState = currentState.addAutomationDrawerState.copy(selectedName = name)
            )
        }
    }

    fun changeAddAutomationDrawerIcon(icon: String) {
        _homeUiState.update { currentState ->
            currentState.copy(
                addAutomationDrawerState = currentState.addAutomationDrawerState.copy(selectedIcon = icon)
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
            val searchQuery = currentState.addAutomationDrawerState.iconSearchQuery.lowercase()

            if (searchQuery.isBlank()) {
                return@update currentState.copy(
                    addAutomationDrawerState = currentState.addAutomationDrawerState.copy(
                        filteredIcons = automationIcons
                    )
                )
            }

            currentState.copy(
                addAutomationDrawerState = currentState.addAutomationDrawerState.copy(
                    filteredIcons = FuzzySearch.extractSorted(
                        searchQuery, automationIcons, 75
                    ).map { it.string }
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
