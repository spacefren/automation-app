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
import androidx.compose.material3.MaterialTheme
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
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.ui.shared.IconButton

@Composable
fun getAutomationIconPainter(name: String): Painter {
	val context = LocalContext.current

	return rememberAsyncImagePainter(
		model = ImageRequest.Builder(context)
			.data("file:///android_asset/icons/$name.svg")
			.decoderFactory(SvgDecoder.Factory())
			.build(),
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
	Box(
		modifier = modifier
			.height(65.dp)
			.fillMaxWidth()
	) {
		Row(
			modifier = Modifier
				.shadow(5.dp, RoundedCornerShape(8.dp))
				.background(MaterialTheme.colorScheme.surface)
				.padding(10.dp),
			horizontalArrangement = Arrangement.End,
			verticalAlignment = Alignment.CenterVertically
		) {
			Column(
				modifier = Modifier
					.weight(1f)
					.fillMaxHeight()
			) {
				Row(
					modifier = Modifier
						.fillMaxWidth()
						.weight(1f),
					verticalAlignment = Alignment.Top
				) {
					Image(
						painter = getAutomationIconPainter(icon),
						contentDescription = "",
						colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
						modifier = Modifier
							.size(22.dp)
							.padding(top = 2.dp)
					)
					Spacer(modifier = Modifier.width(8.dp))
					Text(
						text = name,
						color = MaterialTheme.colorScheme.onBackground,
						fontSize = 18.sp,
						modifier = Modifier
							.height(22.dp)
					)
				}
				Text(
					text = "Last ran: $lastUsed",
					color = MaterialTheme.colorScheme.onSurface,
					fontSize = 13.sp,
					lineHeight = 13.sp,
					modifier = Modifier
						.padding(start = 2.dp) // Align with the icon's live area
				)
			}

			Spacer(modifier = Modifier.width(10.dp))

			IconButton(
				color = MaterialTheme.colorScheme.tertiary,
				iconColor = MaterialTheme.colorScheme.onTertiary,
				drawableRes = R.drawable.round_edit_24,
				cornerRadius = 5.dp,
				padding = 6.dp,
				onClick = editAutomation,
				modifier = Modifier
					.size(45.dp)
			)

			Spacer(modifier = Modifier.width(10.dp))

			IconButton(
				color = MaterialTheme.colorScheme.secondary,
				iconColor = MaterialTheme.colorScheme.onSecondary,
				drawableRes = R.drawable.round_play_arrow_24,
				cornerRadius = 5.dp,
				padding = 4.dp,
				onClick = runAutomation,
				modifier = Modifier
					.size(45.dp)
			)
		}
	}
}
