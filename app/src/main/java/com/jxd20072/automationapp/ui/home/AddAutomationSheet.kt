package com.jxd20072.automationapp.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

val iconCategories = mapOf(
	"People and Identity" to listOf(
		"user", "user-group", "user-secret", "user-shield", "user-graduate",
		"address-card", "fingerprint", "street-view", "universal-access"
	),
	"Technology and Communication" to listOf(
		"phone-flip", "mobile-screen-button", "microphone", "video", "headphones",
		"headset", "envelope", "envelope-open", "comment", "message", "bullhorn",
		"desktop", "laptop", "server", "microchip", "robot", "sd-card",
		"cloud-arrow-up", "cloud-arrow-down", "wifi", "terminal", "radio"
	),
	"Navigation and Travel" to listOf(
		"location-arrow", "location-dot", "compass", "compass-drafting",
		"map", "globe", "earth-africa", "earth-americas", "earth-asia",
		"plane", "plane-up", "train-subway", "train-tram", "truck",
		"truck-pickup", "motorcycle", "sailboat"
	),
	"Health and Wellness" to listOf(
		"wheelchair", "medical", "pills", "capsules", "virus-covid",
		"staff-snake", "heart", "heart-pulse", "dumbbell", "stethoscope"
	),
	"Time and Organization" to listOf(
		"clock", "clock-rotate-left", "calendar-day", "hourglass-end",
		"stopwatch", "timeline", "file", "file-arrow-up", "file-arrow-down",
		"folder-open", "folder-tree", "clipboard", "paperclip", "clone"
	),
	"Food and Dining" to listOf(
		"utensils", "pizza-slice", "mug-hot", "cake-candles",
		"glass-water", "coffee"
	),
	"Weather and Nature" to listOf(
		"sun", "moon", "snowflake", "cloud-sun", "cloud-showers-heavy",
		"hurricane", "wind", "tree", "mountain", "seedling", "water", "wave-square"
	),
	"Security and Protection" to listOf(
		"lock", "unlock", "key", "shield-halved", "user-shield"
	),
	"Tools and Utilities" to listOf(
		"wrench", "hammer", "toolbox", "ruler", "paint-roller", "trowel",
		"wand-magic-sparkles", "screwdriver"
	),
	"Arts and Entertainment" to listOf(
		"palette", "camera", "film", "clapperboard", "masks-theater",
		"music", "guitar", "gamepad"
	),
	"Personal Accessories" to listOf(
		"wallet", "suitcase", "umbrella", "umbrella-beach"
	),
	"Numbers" to listOf(
		"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
	),
	"Miscellaneous" to listOf(
		"vr-cardboard", "voicemail", "ticket", "qrcode", "language",
		"toggle-off", "yin-yang", "meteor", "trophy", "power-off",
		"volume-high", "volume-xmark"
	),
)

@OptIn(ExperimentalLayoutApi::class)
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

		LazyColumn(
			verticalArrangement = Arrangement.spacedBy(20.dp),
		) {
			items(iconCategories.keys.toList()) { category ->
				Column {
					Text(text = category, fontSize = 17.sp)
					Spacer(modifier = Modifier.height(8.dp))
					FlowRow(
						verticalArrangement = Arrangement.spacedBy(8.dp),
						horizontalArrangement = Arrangement.spacedBy(8.dp),
						modifier = Modifier
							.fillMaxWidth()
					) {
						iconCategories[category]!!.forEach() { icon ->
							IconButton(
								color = MaterialTheme.colorScheme.surface,
								iconColor = MaterialTheme.colorScheme.onSurface,
								iconName = icon,
								cornerRadius = 8.dp,
								padding = 8.dp,
								modifier = Modifier.size(43.dp)
							) { onIconSelected(icon) }
						}
					}
				}
			}
		}
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
			Layer(
				modifier = modifier
			) {
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