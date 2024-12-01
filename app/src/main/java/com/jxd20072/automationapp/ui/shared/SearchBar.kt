package com.jxd20072.automationapp.ui.shared

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import com.jxd20072.automationapp.R

@Composable
fun SearchBar(
	searchText: String,
	modifier: Modifier = Modifier
) {

	BaseTextInput(
		placeholderText = searchText,
		imeAction = ImeAction.Search,
		icon = R.drawable.search,
		modifier = modifier
	)
}