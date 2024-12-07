package com.jxd20072.automationapp.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.jxd20072.automationapp.ui.shared.SearchBar
import com.jxd20072.automationapp.ui.shared.ValidationResult

@Composable
fun AddAutomationScreen(
	selectedIcon: String,
	name: String,
	onNameChanged: (name: String) -> Unit,
	onIconChangeRequest: () -> Unit,
	onCreateAutomation: (name: String) -> Unit,
) {

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
				drawableRes = R.drawable.round_check_24,
				cornerRadius = 8.dp,
				padding = 5.dp,
				modifier = Modifier.size(45.dp)
			) {
				if (validateText(name) is ValidationResult.Success) {
					onCreateAutomation(name)
				}
			}
		}
		Row(
			horizontalArrangement = Arrangement.spacedBy(10.dp)
		) {
			IconButton(
				color = MaterialTheme.colorScheme.surface,
				iconColor = MaterialTheme.colorScheme.onSurface,
				iconName = selectedIcon,
				cornerRadius = 8.dp,
				padding = 8.dp,
				modifier = Modifier.size(45.dp)
			) {
				onIconChangeRequest()
			}
			BaseTextInput(
				placeholderText = "Name",
				imeAction = ImeAction.Done,
				focused = false,
				text = name,
				validateInput = { text -> validateText(text) }
			) { text ->
				onNameChanged(text)
			}
		}
		Spacer(modifier = Modifier.height(100.dp))
	}
}

val icons = listOf(
	// Numbers and Mathematical Symbols
	"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
	"hashtag", "percent", "not-equal", "plus", "minus",
	"square-root-variable", "superscript", "subscript",

	// User and Identity Icons
	"user", "user-group", "user-nurse", "user-secret",
	"user-shield", "users-rectangle", "user-tag",
	"id-card", "address-card", "fingerprint",

	// Communication and Technology
	"phone", "wifi", "microphone", "headphones",
	"tablet-screen-button", "desktop", "network-wired",
	"microchip", "tower-broadcast", "walkie-talkie",

	// Transportation and Vehicles
	"plane", "train", "van-shuttle", "truck-medical",
	"sailboat", "rocket",

	// Nature and Environment
	"sun", "moon", "cloud-arrow-up", "cloud-arrow-down",
	"wind", "hurricane", "tornado", "volcano", "water",
	"snowflake", "tree", "mountain-sun",

	// Sports and Recreation
	"volleyball", "person-skiing-nordic", "person-swimming",
	"dumbbell", "trophy", "chess-knight",

	// Household and Personal Items
	"wallet", "shirt", "shoe-prints", "couch", "tv",
	"umbrella", "umbrella-beach", "wine-bottle", "wine-glass",
	"mug-hot", "suitcase", "credit-card", "key",

	// Tools and Utilities
	"wrench", "toolbox", "gears", "charging-station",
	"battery-half", "ruler", "trowel", "vr-cardboard",
	"wand-magic-sparkles", "eye-dropper",

	// Medical and Health
	"wheelchair", "universal-access", "syringe",
	"star-of-life", "virus-covid", "health-related icons",

	// Clothing and Accessories
	"crown", "heart", "shield", "star", "star-and-crescent",
	"star-of-david", "paw", "dog", "worm",

	// Electronics and Connectivity
	"paperclip", "link", "share", "inbox", "paper-plane",
	"rss", "at", "web-awesome",

	// Miscellaneous
	"functions", "lightbulb", "music", "guitar",
	"book-bookmark", "scroll", "note-sticky", "timeline",
	"ticket", "bell", "bullhorn", "power-off", "print",
	"robot", "layers"
)

@Composable
fun IconSelectScreen(
	onIconSelected: (icon: String) -> Unit
) {
	Column(verticalArrangement = Arrangement.spacedBy(15.dp)) {
		Text(
			text = "Change icon",
			color = MaterialTheme.colorScheme.onBackground,
			fontSize = 27.sp,
		)

		SearchBar("Search icons")

		LazyVerticalGrid(
			columns = GridCells.Adaptive(43.dp),
			verticalArrangement = Arrangement.spacedBy(8.dp),
			horizontalArrangement = Arrangement.spacedBy(8.dp),
		) {
			items(icons) { icon ->
				IconButton(
					color = MaterialTheme.colorScheme.surface,
					iconColor = MaterialTheme.colorScheme.onSurface,
					iconName = icon,
					cornerRadius = 8.dp,
					padding = 8.dp,
				) { onIconSelected(icon) }
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
	onCreateAutomation: (name: String, icon: String) -> Unit,
) {
	var screen by remember { mutableStateOf(AddAutomationSheetScreen.AUTOMATION_ADD) }
	val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
	var selectedIcon by remember { mutableStateOf("functions") }
	var name by remember { mutableStateOf("") }

	if (show) {
		selectedIcon = "functions"
		screen = AddAutomationSheetScreen.AUTOMATION_ADD
		name = ""

		ModalBottomSheet(
			sheetState = sheetState,
			onDismissRequest = onDismissRequest,
			containerColor = MaterialTheme.colorScheme.background,
			modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
		) {
			Layer(modifier = modifier) {
				AnimatedContent(
					targetState = screen, label = "",
				) { targetState ->
					when (targetState) {
						AddAutomationSheetScreen.AUTOMATION_ADD -> {
							AddAutomationScreen(
								selectedIcon = selectedIcon,
								onIconChangeRequest = {
									screen = AddAutomationSheetScreen.ICON_SELECT
								},
								name = name,
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