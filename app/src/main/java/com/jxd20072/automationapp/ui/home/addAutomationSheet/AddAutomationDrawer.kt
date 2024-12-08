package com.jxd20072.automationapp.ui.home.addAutomationSheet

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jxd20072.automationapp.ui.home.AddAutomationDrawerScreen
import com.jxd20072.automationapp.ui.home.AddAutomationDrawerUiState
import com.jxd20072.automationapp.ui.shared.Layer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAutomationDrawer(
	drawerUiState: AddAutomationDrawerUiState,
	modifier: Modifier = Modifier,
	onDismissRequest: () -> Unit,
	onScreenChanged: (screen: AddAutomationDrawerScreen) -> Unit,
	onNameChanged: (name: String) -> Unit,
	onIconChanged: (icon: String) -> Unit,
	onCreateAutomation: () -> Unit,
) {
	val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

	if (drawerUiState.isDrawerOpen) {
		ModalBottomSheet(
			sheetState = sheetState,
			onDismissRequest = onDismissRequest,
			containerColor = MaterialTheme.colorScheme.background,
			modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
		) {
			Layer(
				modifier = modifier
			) {
				AnimatedContent(
					targetState = drawerUiState.screen, label = "",
				) { targetState ->
					when (targetState) {
						AddAutomationDrawerScreen.AUTOMATION_ADD -> {
							AddAutomationScreen(
								selectedIcon = drawerUiState.selectedIcon,
								onIconChangeRequest = {
									onScreenChanged(AddAutomationDrawerScreen.ICON_SELECT)
								},
								name = drawerUiState.selectedName,
								onNameChanged = { newName ->
									onNameChanged(newName)
								},
								onCreateAutomation = onCreateAutomation
							)
						}

						AddAutomationDrawerScreen.ICON_SELECT -> {
							IconSelectScreen(onIconSelected = { icon ->
								onIconChanged(icon)
								onScreenChanged(AddAutomationDrawerScreen.AUTOMATION_ADD)
							})
						}
					}
				}
			}
		}
	}
}