package com.jxd20072.automationapp.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jxd20072.automationapp.R

@Composable
fun SearchBar(
	searchText: String,
	modifier: Modifier = Modifier
) {
	val focusRequester = remember { FocusRequester() }
	var focused by remember { mutableStateOf(false) }
	var text by remember { mutableStateOf("") }
	val focusManager = LocalFocusManager.current
	val closeInteractionSource = remember { MutableInteractionSource() }

	Box(
		modifier = modifier
			.height(45.dp)
			.shadow(10.dp, RoundedCornerShape(11.dp))
			.background(MaterialTheme.colorScheme.surface)
			.clickable() {
				focusRequester.requestFocus()
			}
			.padding(5.dp)
	) {
		Row(
			horizontalArrangement = Arrangement.Start,
		) {
			Icon(
				painter = painterResource(R.drawable.search),
				contentDescription = "search",
				tint = MaterialTheme.colorScheme.onSurface,
				modifier = Modifier
					.size(35.dp)
					.padding(5.dp)
			)
			Spacer(modifier = Modifier.width(5.dp))
			Box(
				modifier = Modifier
					.weight(1f)
			) {
				BasicTextField(
					value = text,
					onValueChange = { t -> text = t },
					singleLine = true,
					enabled = true,
					cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
					textStyle = TextStyle(
						color = MaterialTheme.colorScheme.onBackground,
						fontSize = 16.sp
					),
					keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
					keyboardActions = KeyboardActions(onSearch = {
						focusManager.clearFocus()
					}),
					modifier = Modifier
						.fillMaxSize()
						.wrapContentHeight(align = Alignment.CenterVertically)
						.focusRequester(focusRequester)
						.onFocusChanged {
							focused = it.hasFocus
						}
				)

				if (text == "") {
					Text(
						text = searchText,
						color = MaterialTheme.colorScheme.onSurface,
						modifier = Modifier
							.fillMaxHeight()
							.wrapContentHeight(align = Alignment.CenterVertically)
					)
				}
			}
			Spacer(modifier = Modifier.width(5.dp))

			if (text != "") {
				Icon(
					painter = painterResource(R.drawable.close),
					contentDescription = "search",
					tint = MaterialTheme.colorScheme.onSurface,
					modifier = Modifier
						.size(35.dp)
						.padding(5.dp)
						.clickable(interactionSource = closeInteractionSource, indication = null) {
							text = ""
							focusManager.clearFocus()
						}
				)
			}
		}
	}
}