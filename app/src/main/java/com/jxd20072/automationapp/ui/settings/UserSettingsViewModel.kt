package com.jxd20072.automationapp.ui.settings

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jxd20072.automationapp.core.data.model.ConfigurationValue
import com.jxd20072.automationapp.core.data.repository.AutomationsRepository
import com.jxd20072.automationapp.core.data.repository.UserSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds


@Immutable
data class UserEditableSettings(
    val autoSave: ConfigurationValue.BOOL,
    val autoOpenKeyboard: ConfigurationValue.BOOL,
)

@Immutable
sealed interface UserSettingsUiState {
    data object Loading : UserSettingsUiState
    data class Success(val settings: UserEditableSettings) : UserSettingsUiState
}

@HiltViewModel
class UserSettingsViewModel @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository,
    private val automationsRepository: AutomationsRepository
) : ViewModel() {

    init {
        userSettingsRepository.initializeSettings()
    }

    val userSettingsUiState: StateFlow<UserSettingsUiState> =
        userSettingsRepository.userSettings
            .map { userSettings ->
                UserSettingsUiState.Success(
                    settings = createUserEditableSettings(userSettings)
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = WhileSubscribed(5.seconds.inWholeMilliseconds),
                initialValue = UserSettingsUiState.Loading
            )

    fun resetSettingsToDefault() {
        viewModelScope.launch {
            userSettingsRepository.resetSettingsToDefault()
        }
    }

    fun setAutoSaveEnabled(enabled: ConfigurationValue.BOOL) {
        viewModelScope.launch {
            userSettingsRepository.setAutoSaveEnabled(enabled.value)
        }
    }

    fun setAutoOpenKeyboard(enabled: ConfigurationValue.BOOL) {
        viewModelScope.launch {
            userSettingsRepository.setAutoOpenKeyboard(enabled.value)
        }
    }
}
