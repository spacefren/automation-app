package com.jxd20072.automationapp.ui.shared

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import com.jxd20072.automationapp.R

@Composable
fun SearchBar(
	searchText: String,
	queryText: String,
	autoFocus: Boolean,
	modifier: Modifier = Modifier,
	onSearch: (String) -> Unit
) {
	BaseTextInput(
		placeholderText = searchText,
		text = queryText,
		autoFocus = autoFocus,
		imeAction = ImeAction.Search,
		icon = R.drawable.round_search_24,
		modifier = modifier,
		onTextChanged = onSearch
	)
}