package com.jxd20072.automationapp.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.ui.shared.BaseTextInput
import com.jxd20072.automationapp.ui.shared.IconButton
import com.jxd20072.automationapp.ui.shared.Layer
import com.jxd20072.automationapp.ui.shared.ValidationResult

@Composable
fun AddAutomationScreen(
	selectedIcon: Int,
	onNameChanged: (name: String) -> Unit,
	onIconChangeRequest: () -> Unit,
	onCreateAutomation: (name: String) -> Unit,
) {
	var chosenName by rememberSaveable { mutableStateOf("") }

	fun validateText(text: String): ValidationResult {
		return if (text == "" || text == "invalid") {
			ValidationResult.Error("Invalid name: idk bro")
		} else {
			ValidationResult.Success
		}
	}

	Column(verticalArrangement = Arrangement.spacedBy(15.dp)) {
		Row(
			horizontalArrangement = Arrangement.SpaceAround,
			verticalAlignment = Alignment.Bottom
		) {
			Text(
				text = "New automation",
				color = MaterialTheme.colorScheme.onBackground,
				fontSize = 27.sp,
				modifier = Modifier.weight(1f)
			)
			IconButton(
				color = MaterialTheme.colorScheme.secondary,
				iconColor = MaterialTheme.colorScheme.onSecondary,
				icon = R.drawable.check,
				size = 45.dp,
				cornerRadius = 8.dp,
				padding = 5.dp
			) {
				if (validateText(chosenName) is ValidationResult.Success) {
					onCreateAutomation(chosenName)
				}
			}
		}
		Row(
			horizontalArrangement = Arrangement.spacedBy(10.dp)
		) {
			IconButton(
				color = MaterialTheme.colorScheme.surface,
				iconColor = MaterialTheme.colorScheme.onSurface,
				size = 45.dp,
				icon = selectedIcon,
				cornerRadius = 11.dp,
				padding = 8.dp
			) {
				onIconChangeRequest()
			}
			BaseTextInput(
				placeholderText = "Name",
				imeAction = ImeAction.Done,
				focused = false,
				text = chosenName,
				validateInput = { text -> validateText(text) }
			) { text ->
				chosenName = text
				onNameChanged(chosenName)
			}
		}
		Spacer(modifier = Modifier.height(100.dp))
	}
}

@Composable
fun IconSelectScreen(
	onIconSelected: (icon: Int) -> Unit
) {
	Column(verticalArrangement = Arrangement.spacedBy(15.dp)) {
		Text(
			text = "Change icon",
			color = MaterialTheme.colorScheme.onBackground,
			fontSize = 27.sp,
		)

		LazyVerticalGrid(
			columns = GridCells.Adaptive(43.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp),
			horizontalArrangement = Arrangement.spacedBy(8.dp)
		) {
			items(10) {
				IconButton(
					color = MaterialTheme.colorScheme.surface,
					iconColor = MaterialTheme.colorScheme.onSurface,
					icon = R.drawable.function,
					cornerRadius = 8.dp,
					padding = 8.dp,
					size = 40.dp,
					modifier = Modifier.aspectRatio(1f)
				) {
					onIconSelected(R.drawable.check)
				}
			}
		}
		Spacer(modifier = Modifier.height(10.dp))
	}
}

enum class AddAutomationSheetScreen {
	AUTOMATION_ADD, ICON_SELECT
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAutomationSheet(
	show: Boolean,
	onDismissRequest: () -> Unit,
	modifier: Modifier = Modifier,
	onCreateAutomation: (name: String, icon: Int) -> Unit,
) {
	var screen by remember { mutableStateOf(AddAutomationSheetScreen.AUTOMATION_ADD) }
	val sheetState = rememberModalBottomSheetState()
	var selectedIcon by remember { mutableIntStateOf(R.drawable.function) }
	var name by remember { mutableStateOf("") }

	if (show) {
		selectedIcon = R.drawable.function
		ModalBottomSheet(
			sheetState = sheetState,
			onDismissRequest = onDismissRequest,
			containerColor = MaterialTheme.colorScheme.background,
		) {
			Layer(modifier = modifier) {
				AnimatedContent(
					targetState = screen, label = ""
				) { targetState ->
					when (targetState) {
						AddAutomationSheetScreen.AUTOMATION_ADD -> {
							AddAutomationScreen(
								selectedIcon = selectedIcon,
								onIconChangeRequest = {
									screen = AddAutomationSheetScreen.ICON_SELECT
								},
								onNameChanged = { newName -> name = newName },
								onCreateAutomation = { name ->
									onCreateAutomation(name, selectedIcon)
								}
							)
						}

						AddAutomationSheetScreen.ICON_SELECT -> {
							IconSelectScreen(
								onIconSelected = { icon ->
									selectedIcon = icon
									screen = AddAutomationSheetScreen.AUTOMATION_ADD
								}
							)
						}
					}
				}
			}
		}
	}
}