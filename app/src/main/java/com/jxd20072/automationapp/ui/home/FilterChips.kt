package com.jxd20072.automationapp.ui.home

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jxd20072.automationapp.ui.shared.Button
import com.jxd20072.automationapp.ui.theme.LocalTheme

@Composable
private fun FilterChip(
	name: String,
	enabled: Boolean,
	modifier: Modifier = Modifier,
	onActiveChanged: (active: Boolean) -> Unit
) {
	Button(
		color = if (enabled) LocalTheme.colorScheme.secondary else LocalTheme.colorScheme.surface,
		cornerRadius = LocalTheme.layoutSystem.shapes.cornerRadiusLevel1,
		onClick = { onActiveChanged(!enabled) },
		modifier = modifier
			.shadow(LocalTheme.layoutSystem.shadows.secondary)
			.padding(horizontal = LocalTheme.layoutSystem.spacing.paddingLevel3)
	) {
		Text(
			text = name,
			fontSize = 13.sp,
			color = if (enabled) LocalTheme.colorScheme.onBackground else LocalTheme.colorScheme.onSurface,
			modifier = Modifier.align(Alignment.Center)
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

	if (filters.isNotEmpty()) {
		Row(
			horizontalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel2),
			modifier = modifier
				.horizontalScroll(scrollState)
				.height(25.dp)
		) {
			Spacer(
				modifier = Modifier.width(sidePadding.calculateStartPadding(LayoutDirection.Ltr) - 10.dp)
			)

			filters.map { (name, enabled) ->
				FilterChip(name = name, enabled = enabled) { hasEnabled ->
					onFilterChange(name, hasEnabled)
				}
			}

			Spacer(
				modifier = Modifier.width(sidePadding.calculateEndPadding(LayoutDirection.Ltr) - 10.dp)
			)
		}
	}
}