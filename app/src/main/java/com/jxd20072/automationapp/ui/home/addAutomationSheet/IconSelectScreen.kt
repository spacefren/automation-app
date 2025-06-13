package com.jxd20072.automationapp.ui.home.addAutomationSheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jxd20072.automationapp.ui.shared.IconButton
import com.jxd20072.automationapp.ui.shared.SearchBar
import com.jxd20072.automationapp.ui.theme.LocalTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun IconSelectScreen(
	autoOpenKeyboard: Boolean,
	isSheetOpen: Boolean,
	filteredIcons: List<String>,
	iconSearchQuery: String,
	onIconSearch: (query: String) -> Unit,
	onIconSelected: (icon: String) -> Unit
) {
	Column(
		verticalArrangement = Arrangement.spacedBy(15.dp),
		modifier = Modifier
			.fillMaxSize()
			.padding(top = LocalTheme.layoutSystem.spacing.paddingLevel1)
			.padding(horizontal = LocalTheme.layoutSystem.spacing.paddingLevel1)
	) {
		Text(
			text = "Change icon",
			color = LocalTheme.colorScheme.onBackground,
			style = LocalTheme.typography.subTitle
		)

		SearchBar(
			searchText = "Search icons",
			queryText = iconSearchQuery,
			modifier = Modifier.fillMaxWidth(),
			autoFocus = autoOpenKeyboard
		) { query ->
			onIconSearch(query)
		}

		LazyVerticalGrid(
			columns = GridCells.Fixed(8),
			verticalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel2),
			horizontalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel2),
			contentPadding = PaddingValues(
				bottom = LocalTheme.layoutSystem.spacing.paddingLevel1
					+ WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
			)
		) {
			items(filteredIcons, { it }) { icon ->
				IconButton(
					color = LocalTheme.colorScheme.surface,
					iconColor = LocalTheme.colorScheme.onSurface,
					iconName = icon,
					cornerRadius = 8.dp,
					padding = 8.dp,
					size = 43.dp,
					modifier = Modifier
						.aspectRatio(1f)
						.animateItem()
				) { onIconSelected(icon) }
			}
		}
	}
}