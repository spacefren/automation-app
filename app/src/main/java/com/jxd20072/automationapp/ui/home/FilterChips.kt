package com.jxd20072.automationapp.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
private fun FilterChip(
	name: String,
	enabled: Boolean,
	modifier: Modifier = Modifier,
	onActiveChanged: (active: Boolean) -> Unit
) {
	val interactionSource = remember { MutableInteractionSource() }

	Box(
		modifier = modifier
			.fillMaxHeight()
			.shadow(6.dp, RoundedCornerShape(7.dp))
			.background(if (enabled) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surface)
			.clickable(
				interactionSource = interactionSource,
				indication = null
			) {
				onActiveChanged(!enabled)
			}
			.padding(horizontal = 6.dp)
	) {
		Text(
			text = name,
			fontSize = 13.sp,
			color =
			if (enabled) MaterialTheme.colorScheme.onBackground
			else MaterialTheme.colorScheme.onSurface,
			modifier = Modifier
				.align(Alignment.Center)
		)
	}
}

@Composable
fun FilterChips(
	filters: Map<String, Boolean>,
	sidePadding: PaddingValues,
	modifier: Modifier = Modifier,
	onFilterChange: (filter: String, enabled: Boolean) -> Unit
) {
	val scrollState = rememberScrollState(0)

	Row(
		horizontalArrangement = Arrangement.spacedBy(10.dp),
		modifier = modifier
			.horizontalScroll(scrollState)
			.height(25.dp)
	) {
		Spacer(modifier = Modifier.width(sidePadding.calculateStartPadding(LayoutDirection.Ltr) - 10.dp))

		filters.map { (name, enabled) ->
			FilterChip(name = name, enabled = enabled) { hasEnabled ->
				onFilterChange(name, hasEnabled)
			}
		}

		Spacer(modifier = Modifier.width(sidePadding.calculateEndPadding(LayoutDirection.Ltr) - 10.dp))
	}
}