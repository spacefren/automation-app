package com.jxd20072.automationapp.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
	saveAutomation: () -> Unit,
	runAutomation: () -> Unit,
) {

	val elapsedTime =
		if (automationData.lastSaved != "")
			getRelativeTime(Instant.parse(automationData.lastSaved), currentTime)
		else null

	Column(
		verticalArrangement = Arrangement.spacedBy(10.dp),
		modifier = modifier
			.fillMaxWidth()
			.background(LocalTheme.colorScheme.surface)
			.windowInsetsPadding(WindowInsets.statusBars)
			.padding(top = LocalTheme.layoutSystem.spacing.paddingLevel1)
			.padding(horizontal = LocalTheme.layoutSystem.spacing.paddingLevel1)
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween,
			modifier = Modifier
				.fillMaxWidth()
		) {
			Column(
				verticalArrangement = Arrangement.spacedBy(5.dp)
			) {
				Row(
					horizontalArrangement = Arrangement.spacedBy(10.dp),
					verticalAlignment = Alignment.CenterVertically
				) {
					Icon(
						painter = painterResource(R.drawable.round_arrow_back_24),
						contentDescription = "return to home screen",
						tint = LocalTheme.colorScheme.onBackground,
						modifier = Modifier
							.size(22.dp)
							.clickable(interactionSource = null, indication = null) {
								returnHome(true)
							}
					)
					Icon(
						painter = getAutomationIconPainter(automationData.icon),
						contentDescription = "icon: ${automationData.icon}",
						tint = LocalTheme.colorScheme.onBackground,
						modifier = Modifier.size(20.dp)
					)
					Text(
						text = automationData.name,
						color = LocalTheme.colorScheme.onBackground,
						fontSize = 20.sp
					)
				}
				Text(
					text = "Last saved: ${elapsedTime ?: "never"}",
					color = LocalTheme.colorScheme.onSurface,
					fontSize = 15.sp
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
					cornerRadius = 5.dp,
					onClick = saveAutomation,
					modifier = Modifier
						.size(45.dp),
				)

				IconButton(
					drawableRes = R.drawable.round_play_arrow_24,
					color = LocalTheme.colorScheme.secondary,
					iconColor = LocalTheme.colorScheme.onPrimary,
					padding = 6.dp,
					cornerRadius = 5.dp,
					onClick = runAutomation,
					modifier = Modifier
						.size(45.dp)
				)
			}
		}
		Row(
			horizontalArrangement = Arrangement.spacedBy(10.dp)
		) {
			IconLabelButton(
				label = "Triggers",
				fontSize = 15.sp,
				drawableRes = R.drawable.round_electric_bolt_24,
				color = LocalTheme.colorScheme.tertiary,
				iconColor = LocalTheme.colorScheme.onSecondary,
				cornerRadius = 5.dp,
				iconSize = 20.dp
			) { }

			IconLabelButton(
				label = "Variables",
				fontSize = 15.sp,
				drawableRes = R.drawable.round_data_object_24,
				color = LocalTheme.colorScheme.tertiary,
				iconColor = LocalTheme.colorScheme.onSecondary,
				cornerRadius = 5.dp,
				iconSize = 20.dp
			) { }

			IconLabelButton(
				label = "Logs",
				fontSize = 15.sp,
				drawableRes = R.drawable.baseline_format_align_left_24,
				color = LocalTheme.colorScheme.tertiary,
				iconColor = LocalTheme.colorScheme.onSecondary,
				cornerRadius = 5.dp,
				iconSize = 20.dp
			) { }
		}
		Spacer(modifier = Modifier.height(5.dp))
	}
}
