package com.jxd20072.automationapp.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.svg.SvgDecoder
import com.jxd20072.automationapp.AutomationApplication
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.ui.shared.IconButton
import com.jxd20072.automationapp.ui.shared.SwipeableContainer
import com.jxd20072.automationapp.ui.theme.LocalTheme

@Composable
fun getAutomationIconPainter(name: String): Painter {
	val context = LocalContext.current

	return rememberAsyncImagePainter(
		model = ImageRequest.Builder(context)
			.data("file:///android_asset/icons/$name.svg")
			.decoderFactory(SvgDecoder.Factory())
			.build(),
		imageLoader = (context.applicationContext as AutomationApplication).imageLoader
	)
}

@Composable
fun AutomationCard(
	name: String,
	icon: String,
	lastUsed: String,
	modifier: Modifier = Modifier,
	runAutomation: () -> Unit,
	editAutomation: () -> Unit,
) {
	SwipeableContainer(
		icon1 = R.drawable.round_archive_24,
		backgroundColor1 = LocalTheme.colorScheme.tertiary,
		containerCornerRadius = LocalTheme.layoutSystem.shapes.cornerRadiusLevel1,
		onAction1 = { },
		modifier = modifier
	) {
		Box(
			modifier = Modifier
				.height(65.dp)
				.fillMaxWidth()
		) {
			Row(
				modifier = Modifier
					.shadow(
						LocalTheme.layoutSystem.shadows.primary,
						RoundedCornerShape(LocalTheme.layoutSystem.shapes.cornerRadiusLevel1)
					)
					.background(LocalTheme.colorScheme.surface)
					.padding(LocalTheme.layoutSystem.spacing.paddingLevel2),
				horizontalArrangement = Arrangement.End,
				verticalAlignment = Alignment.CenterVertically
			) {
				Column(
					modifier = Modifier
						.weight(1f)
						.fillMaxHeight()
				) {
					Row(
						verticalAlignment = Alignment.Top,
						modifier = Modifier
							.fillMaxWidth()
							.weight(1f)
					) {
						Image(
							painter = getAutomationIconPainter(icon),
							contentDescription = "",
							colorFilter = ColorFilter.tint(LocalTheme.colorScheme.onBackground),
							modifier = Modifier
								.size(22.dp)
								.padding(top = 2.dp)
						)
						Spacer(modifier = Modifier.width(8.dp))
						Text(
							text = name,
							color = LocalTheme.colorScheme.onBackground,
							fontSize = 18.sp,
							modifier = Modifier.height(22.dp)
						)
					}
					Text(
						text = "Last ran: $lastUsed",
						color = LocalTheme.colorScheme.onSurface,
						fontSize = 13.sp,
						lineHeight = 13.sp,
						modifier = Modifier.padding(start = 2.dp) // Align with the icon's live area
					)
				}

				Spacer(modifier = Modifier.width(LocalTheme.layoutSystem.spacing.paddingLevel2))

				IconButton(
					color = LocalTheme.colorScheme.tertiary,
					iconColor = LocalTheme.colorScheme.onTertiary,
					drawableRes = R.drawable.round_edit_24,
					cornerRadius = LocalTheme.layoutSystem.shapes.cornerRadiusLevel2,
					padding = LocalTheme.layoutSystem.spacing.paddingLevel3,
					onClick = editAutomation,
					size = 45.dp,
				)

				Spacer(modifier = Modifier.width(LocalTheme.layoutSystem.spacing.paddingLevel2))

				IconButton(
					color = LocalTheme.colorScheme.secondary,
					iconColor = LocalTheme.colorScheme.onSecondary,
					drawableRes = R.drawable.round_play_arrow_24,
					cornerRadius = LocalTheme.layoutSystem.shapes.cornerRadiusLevel2,
					padding = LocalTheme.layoutSystem.spacing.paddingLevel3,
					onClick = runAutomation,
					size = 45.dp,
				)
			}
		}
	}
}
