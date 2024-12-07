package com.jxd20072.automationapp.ui.shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import com.jxd20072.automationapp.R

@Composable
fun SearchBar(
	searchText: String,
	modifier: Modifier = Modifier
) {
	var text by remember { mutableStateOf("") }

	BaseTextInput(
		placeholderText = searchText,
		text = text,
		imeAction = ImeAction.Search,
		icon = R.drawable.round_search_24,
		modifier = modifier
	) { newText ->
		text = newText
	}
}