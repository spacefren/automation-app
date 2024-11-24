package com.jxd20072.automationapp.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.ui.shared.IconButton
import com.jxd20072.automationapp.ui.utils.getRelativeTime
import java.time.LocalDateTime

@Composable
fun AutomationCard(
	name: String,
	icon: Int,
	lastUsed: LocalDateTime,
	modifier: Modifier = Modifier
) {
	Row(
		modifier = modifier
			.fillMaxWidth()
			.shadow(6.dp, RoundedCornerShape(11.dp))
			.background(MaterialTheme.colorScheme.surface)
			.height(65.dp)
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
				Icon(
					painter = painterResource(icon),
					contentDescription = "",
					tint = MaterialTheme.colorScheme.onBackground,
					modifier = Modifier
						.size(20.dp)
						.padding(top = 3.dp)
				)
				Spacer(modifier = Modifier.width(5.dp))
				Text(
					text = name,
					color = MaterialTheme.colorScheme.onBackground,
					fontSize = 18.sp,
					modifier = Modifier
						.height(22.dp)
				)
			}
			Text(
				text = "Last used: ${getRelativeTime(lastUsed)}",
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
			size = 45.dp,
			icon = R.drawable.edit,
			cornerRadius = 5.dp,
			elevation = 6.dp,
		) {}

		Spacer(modifier = Modifier.width(10.dp))

		IconButton(
			color = MaterialTheme.colorScheme.secondary,
			iconColor = MaterialTheme.colorScheme.onSecondary,
			size = 45.dp,
			icon = R.drawable.play_arrow,
			cornerRadius = 5.dp,
			elevation = 6.dp,
		) {}
	}
}