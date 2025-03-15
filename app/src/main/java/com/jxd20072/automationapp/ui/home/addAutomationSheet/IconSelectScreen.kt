package com.jxd20072.automationapp.ui.home.addAutomationSheet

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jxd20072.automationapp.ui.shared.IconButton
import com.jxd20072.automationapp.ui.shared.SearchBar
import com.jxd20072.automationapp.ui.theme.LocalTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun IconSelectScreen(
	filteredIcons: Map<String, List<String>>,
	iconSearchQuery: String,
	onIconSearch: (query: String) -> Unit,
	onIconSelected: (icon: String) -> Unit
) {
	Column(verticalArrangement = Arrangement.spacedBy(15.dp)) {
		Text(
			text = "Change icon",
			color = LocalTheme.colorScheme.onBackground,
			fontSize = 27.sp,
		)

		SearchBar(searchText = "Search icons", queryText = iconSearchQuery) { query ->
			onIconSearch(query)
		}

		LazyColumn(
			verticalArrangement = Arrangement.spacedBy(20.dp),
		) {
			items(filteredIcons.keys.toList(), key = { it }) { category ->
				Column(
					modifier = Modifier.animateItem(
						fadeInSpec = spring(stiffness = Spring.StiffnessMedium),
						fadeOutSpec = spring(stiffness = Spring.StiffnessMedium)
					)
				) {
					Text(
						text = category,
						color = LocalTheme.colorScheme.onSecondary,
						fontSize = 17.sp
					)
					Spacer(modifier = Modifier.height(8.dp))
					FlowRow(
						verticalArrangement = Arrangement.spacedBy(8.dp),
						horizontalArrangement = Arrangement.spacedBy(8.dp),
						modifier = Modifier.fillMaxWidth()
					) {
						filteredIcons[category]!!.forEach { icon ->
							IconButton(
								color = LocalTheme.colorScheme.surface,
								iconColor = LocalTheme.colorScheme.onSurface,
								iconName = icon,
								cornerRadius = 8.dp,
								padding = 8.dp,
								modifier = Modifier.size(43.dp)
							) { onIconSelected(icon) }
						}
					}
				}
				Spacer(modifier = Modifier.height(15.dp))
			}
			item { Spacer(modifier = Modifier.height(15.dp)) }
		}
	}
}