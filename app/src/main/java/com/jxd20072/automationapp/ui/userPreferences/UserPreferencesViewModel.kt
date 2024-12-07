package com.jxd20072.automationapp.ui.userPreferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jxd20072.automationapp.core.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds


@HiltViewModel
class UserPreferencesViewModel @Inject constructor(
	private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
	val userPreferencesUiState: StateFlow<UserPreferencesUiState> =
		userPreferencesRepository.userPreferences
			.map { userPreferences ->
				UserPreferencesUiState.Success(
					settings = UserEditableSettings(
						autoSave = userPreferences.autoSave
					)
				)
			}
			.stateIn(
				scope = viewModelScope,
				started = WhileSubscribed(5.seconds.inWholeMilliseconds),
				initialValue = UserPreferencesUiState.Loading
			)

	fun setAutoSaveEnabled(enabled: Boolean) {
		viewModelScope.launch {
			userPreferencesRepository.setAutoSaveEnabled(enabled)
		}
	}
}

data class UserEditableSettings(
	val autoSave: Boolean
)

sealed interface UserPreferencesUiState {
	data object Loading : UserPreferencesUiState
	data class Success(val settings: UserEditableSettings) : UserPreferencesUiState
}