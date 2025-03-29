package com.jxd20072.automationapp.ui.editor.editorBottomSheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jxd20072.automationapp.ui.shared.SearchBar
import com.jxd20072.automationapp.ui.theme.LocalTheme

@Composable
fun EditorNodeSelectScreen(
	nodeSearchQuery: String,
	isSheetOpen: Boolean,
	autoOpenKeyboard: Boolean,
	onNodeSearch: (query: String) -> Unit
) {
	Column(
		verticalArrangement = Arrangement.spacedBy(15.dp),
		modifier = Modifier
			.imePadding()
			.padding(bottom = LocalTheme.layoutSystem.spacing.paddingLevel1)
	) {
		Text(
			text = "Select action",
			color = LocalTheme.colorScheme.onBackground,
			fontSize = 27.sp,
		)

		SearchBar(
			searchText = "Search actions or categories",
			queryText = nodeSearchQuery,
			focused = if (autoOpenKeyboard) isSheetOpen else false
		) { query ->
			onNodeSearch(query)
		}
	}
}