package com.jxd20072.automationapp.ui.userPreferences

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun UserPreferencesScreen(
	userPreferencesViewModel: UserPreferencesViewModel = viewModel()
) {
	val userPreferencesUiState by userPreferencesViewModel.userPreferencesUiState.collectAsStateWithLifecycle()
	Text(
		text = "Auto save: ${
			when (userPreferencesUiState) {
				UserPreferencesUiState.Loading -> "Loading..."
				is UserPreferencesUiState.Success -> (userPreferencesUiState as UserPreferencesUiState.Success).settings.autoSave
			}
		}",
		modifier = Modifier
			.padding(top = 710.dp)
			.height(50.dp)
			.fillMaxWidth()
			.background(Color.Blue)
			.clickable {
				println("yea")
				userPreferencesViewModel.setAutoSaveEnabled(!(userPreferencesUiState as UserPreferencesUiState.Success).settings.autoSave)
			}
	)
}