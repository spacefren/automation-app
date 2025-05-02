package com.jxd20072.automationapp.ui.userSettings.settingsItems

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.jxd20072.automationapp.ui.theme.LocalTheme
import com.jxd20072.automationapp.ui.userSettings.UserSettingsEntry

@Composable
fun UserSettingsItem(
	item: UserSettingsEntry<Boolean>,
	displayName: String,
	description: String = "",
	modifier: Modifier = Modifier,
	onValueChanged: (Boolean) -> Unit
) {
	val enabled = item.value
	var isPressed by remember { mutableStateOf(false) }

	var containerSize by remember { mutableStateOf(IntSize(0, 0)) }

	val knobScale by animateFloatAsState(
		if (isPressed) .8f else 1f
	)

	@Composable
	fun calculateExtraPadding(): Dp {
		val containerWidthDp = with(LocalDensity.current) { containerSize.width.toDp() }
		val containerHeightDp = with(LocalDensity.current) { containerSize.height.toDp() }
		val padding = LocalTheme.layoutSystem.spacing.paddingLevel3
		val knobSize = containerHeightDp - (padding * 2)
		return if (enabled) containerWidthDp - knobSize - padding * 2 else 0.dp
	}

	val startPadding by animateDpAsState(calculateExtraPadding())

	val knobColor by animateColorAsState(
		if (enabled) LocalTheme.colorScheme.secondary else LocalTheme.colorScheme.tertiary,
	)

	UserSettingsItemContainer(
		displayName = displayName,
		description = description,
		modifier = modifier
	) {
		Box(
			modifier = Modifier
				.onGloballyPositioned {
					containerSize = it.size
				}
				.width(60.dp)
				.fillMaxHeight()
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
				.shadow(
					LocalTheme.layoutSystem.shadows.primary,
					RoundedCornerShape(LocalTheme.layoutSystem.shapes.cornerRadiusLevel1)
				)
				.background(LocalTheme.colorScheme.surface)
				.padding(LocalTheme.layoutSystem.spacing.paddingLevel3)
				.padding(start = startPadding)
		) {
			Box(
				modifier = Modifier
					.fillMaxHeight()
					.aspectRatio(1f)
					.scale(knobScale)
					.shadow(
						LocalTheme.layoutSystem.shadows.secondary,
						RoundedCornerShape(LocalTheme.layoutSystem.shapes.cornerRadiusLevel2)
					)
					.background(knobColor)
			)
		}
	}
}
