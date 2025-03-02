package com.jxd20072.automationapp.ui.home.addAutomationSheet

import androidx.compose.animation.AnimatedContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.jxd20072.automationapp.ui.home.AddAutomationDrawerScreen
import com.jxd20072.automationapp.ui.home.AddAutomationDrawerUiState
import com.jxd20072.automationapp.ui.shared.Layer
import com.jxd20072.automationapp.ui.shared.ModalBottomSheet
import com.jxd20072.automationapp.ui.shared.ModalBottomSheetState
import com.jxd20072.automationapp.ui.shared.TextInputValidationResult

@Composable
fun AddAutomationDrawer(
	drawerUiState: AddAutomationDrawerUiState,
	autoOpenKeyboard: Boolean,
	onDismissRequest: () -> Unit,
	onScreenChanged: (screen: AddAutomationDrawerScreen) -> Unit,
	validateAutomationName: (name: String) -> TextInputValidationResult,
	onNameChanged: (name: String) -> Unit,
	onIconChanged: (icon: String) -> Unit,
	onIconSearch: (query: String) -> Unit,
	modifier: Modifier = Modifier,
	onCreateAutomation: () -> Unit,
) {
	val sheetState = remember { ModalBottomSheetState() }

	sheetState.isOpen = drawerUiState.isDrawerOpen
	ModalBottomSheet(
		state = sheetState,
		onDismissRequest = {
			onDismissRequest()
			true
		},
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
							validateAutomationName = validateAutomationName,
							onIconChangeRequest = {
								onScreenChanged(AddAutomationDrawerScreen.ICON_SELECT)
							},
							name = drawerUiState.selectedName,
							onNameChanged = { newName ->
								onNameChanged(newName)
							},
							onCreateAutomation = onCreateAutomation,
							isSheetOpen = drawerUiState.isDrawerOpen,
							autoOpenKeyboard = autoOpenKeyboard
						)
					}

					AddAutomationDrawerScreen.ICON_SELECT -> {
						IconSelectScreen(
							filteredIcons = drawerUiState.filteredIcons,
							iconSearchQuery = drawerUiState.iconSearchQuery,
							onIconSearch = onIconSearch
						) { icon ->
							onIconChanged(icon)
							onScreenChanged(AddAutomationDrawerScreen.AUTOMATION_ADD)
						}
					}
				}
			}
		}
	}
}