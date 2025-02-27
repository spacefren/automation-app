package com.jxd20072.automationapp.ui.userSettings.settingsItems

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.jxd20072.automationapp.ui.theme.LocalTheme
import com.jxd20072.automationapp.ui.userSettings.UserSettingsEntry

@Composable
fun UserSettingsItem(
	item: UserSettingsEntry<Boolean>,
	displayName: String,
	description: String,
	modifier: Modifier = Modifier,
	onValueChanged: (Boolean) -> Unit
) {
	val enabled = item.value
	var isPressed by remember { mutableStateOf(false) }

	val padding = 5.dp
	val containerWidth = 65.dp
	val containerHeight = 35.dp
	val knobSize = (containerHeight - padding * 2)

	val knobScale by animateFloatAsState(
		if (isPressed) .8f else 1f,
		animationSpec = spring(stiffness = Spring.StiffnessHigh),
		label = "settings-toggle-animation-scale",
	)

	val startPadding by animateDpAsState(
		if (enabled) containerWidth - knobSize - padding else padding,
		label = "settings-toggle-animation-padding"
	)

	val knobColor by animateColorAsState(
		if (enabled) LocalTheme.colorScheme.secondary
		else LocalTheme.colorScheme.tertiary,
		label = "settings-toggle-animation-color"
	)

	UserSettingsItemContainer(
		displayName = displayName,
		description = description,
		modifier = modifier
	) {
		Box(
			modifier = Modifier
				.width(containerWidth)
				.height(containerHeight)
				.pointerInput(enabled) {
					detectTapGestures(
						onTap = {
							onValueChanged(!enabled)
						},
						onPress = {
							isPressed = true
							try {
								awaitRelease()
							} finally {
								isPressed = false
							}
						}
					)
				}
				.shadow(5.dp, RoundedCornerShape(8.dp))
				.background(LocalTheme.colorScheme.surface)
				.padding(vertical = padding)
		) {
			Box(
				modifier = Modifier
					.padding(start = startPadding)
					.size(knobSize)
			) {
				Box(
					modifier = Modifier
						.fillMaxSize()
						.scale(knobScale)
						.shadow(5.dp, RoundedCornerShape(5.dp))
						.background(knobColor)
				)
			}
		}
	}
}
