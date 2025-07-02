package com.jxd20072.automationapp.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.core.data.AutomationsMapEntry
import com.jxd20072.automationapp.ui.home.getAutomationIconPainter
import com.jxd20072.automationapp.ui.shared.IconButton
import com.jxd20072.automationapp.ui.shared.IconLabelButton
import com.jxd20072.automationapp.ui.theme.LocalTheme
import com.jxd20072.automationapp.ui.utils.getRelativeTime
import java.time.Instant

@Composable
fun EditorTopBar(
	automationData: AutomationsMapEntry,
	currentTime: Instant,
	modifier: Modifier = Modifier,
	returnHome: (saveChanges: Boolean) -> Unit,
	openVariablesScreen: () -> Unit,
	openLogsScreen: () -> Unit,
	saveAutomation: () -> Unit,
	runAutomation: () -> Unit,
) {
	val iconSize = with(LocalDensity.current) {
		LocalTheme.typography.subTitle.fontSize.toDp()
	}

	val elapsedTime =
		if (automationData.lastSaved != "")
			getRelativeTime(Instant.parse(automationData.lastSaved), currentTime)
		else null

	Column(
		verticalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel2),
		modifier = modifier
			.fillMaxWidth()
			.background(LocalTheme.colorScheme.surface)
			.windowInsetsPadding(WindowInsets.statusBars)
			.padding(LocalTheme.layoutSystem.spacing.paddingLevel1)
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween,
			modifier = Modifier
				.fillMaxWidth()
		) {
			Column(
				verticalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel3)
			) {
				Row(
					horizontalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel3),
					verticalAlignment = Alignment.CenterVertically
				) {
					Icon(
						painter = painterResource(R.drawable.round_arrow_back_24),
						contentDescription = "return to home screen",
						tint = LocalTheme.colorScheme.onBackground,
						modifier = Modifier
							.size(iconSize)
							.clickable(interactionSource = null, indication = null) {
								returnHome(true)
							}
					)
					Icon(
						painter = getAutomationIconPainter(automationData.icon),
						contentDescription = "icon: ${automationData.icon}",
						tint = LocalTheme.colorScheme.onBackground,
						modifier = Modifier
							.size(iconSize - 2.dp)
					)
					Text(
						text = automationData.name,
						color = LocalTheme.colorScheme.onBackground,
						style = LocalTheme.typography.subTitle
					)
				}
				Text(
					text = "Last saved: ${elapsedTime ?: "never"}",
					color = LocalTheme.colorScheme.onSurface,
					style = LocalTheme.typography.body
				)
			}
			Row(
				horizontalArrangement = Arrangement.spacedBy(10.dp)
			) {
				IconButton(
					drawableRes = R.drawable.round_save_24,
					color = LocalTheme.colorScheme.tertiary,
					iconColor = LocalTheme.colorScheme.onSurface,
					padding = 6.dp,
					cornerRadius = LocalTheme.layoutSystem.shapes.cornerRadiusLevel1,
					onClick = saveAutomation,
					size = 45.dp,
				)

				IconButton(
					drawableRes = R.drawable.round_play_arrow_24,
					color = LocalTheme.colorScheme.secondary,
					iconColor = LocalTheme.colorScheme.onPrimary,
					padding = 6.dp,
					cornerRadius = LocalTheme.layoutSystem.shapes.cornerRadiusLevel1,
					onClick = runAutomation,
					size = 45.dp,
				)
			}
		}
		Row(
			horizontalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel2)
		) {
			IconLabelButton(
				label = "Variables",
				textStyle = LocalTheme.typography.body,
				drawableRes = R.drawable.round_data_object_24,
				color = LocalTheme.colorScheme.tertiary,
				iconColor = LocalTheme.colorScheme.onSecondary,
				cornerRadius = 5.dp,
				iconSize = 20.dp,
				onClick = openVariablesScreen
			)

			IconLabelButton(
				label = "Logs",
				textStyle = LocalTheme.typography.body,
				drawableRes = R.drawable.round_align_left_24,
				color = LocalTheme.colorScheme.tertiary,
				iconColor = LocalTheme.colorScheme.onSecondary,
				cornerRadius = 5.dp,
				iconSize = 20.dp,
				onClick = openLogsScreen
			)
		}
	}
}
