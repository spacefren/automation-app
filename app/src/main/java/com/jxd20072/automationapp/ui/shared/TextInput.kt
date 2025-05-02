package com.jxd20072.automationapp.ui.shared

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.ui.theme.LocalTheme

sealed class TextInputValidationResult {
	data object Success : TextInputValidationResult()
	data class Error(val message: String) : TextInputValidationResult()
}

@Composable
fun BaseTextInput(
	placeholderText: String,
	text: String,
	imeAction: ImeAction,
	icon: Int? = null,
	focused: Boolean = false,
	onlyFocusIfEmpty: Boolean = false,
	modifier: Modifier = Modifier,
	validateInput: ((text: String) -> TextInputValidationResult)? = null,
	onTextChanged: ((text: String) -> Unit),
) {
	val focusRequester = remember { FocusRequester() }
	var errorText by remember { mutableStateOf("") }
	val focusManager = LocalFocusManager.current
	val closeInteractionSource = remember { MutableInteractionSource() }

	fun evaluateInput(text: String) {
		if (validateInput != null) {
			val validationResult = validateInput(text)

			errorText = if (validationResult is TextInputValidationResult.Error) {
				validationResult.message
			} else {
				""
			}
		}
	}

	val iconSize = with(LocalDensity.current) {
		LocalTheme.typography.header.fontSize.toDp()
	}

	Column(
		modifier = modifier
	) {
		Box(
			modifier = Modifier
				.height(45.dp)
				.shadow(
					LocalTheme.layoutSystem.shadows.primary,
					RoundedCornerShape(LocalTheme.layoutSystem.shapes.cornerRadiusLevel1)
				)
				.background(LocalTheme.colorScheme.surface)
				.clickable(interactionSource = null, indication = ripple()) {
					focusRequester.requestFocus()
				}
				.padding(LocalTheme.layoutSystem.spacing.paddingLevel3)
		) {
			Row(
				horizontalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel3),
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier.padding(LocalTheme.layoutSystem.spacing.paddingLevel3)
			) {
				if (icon != null) {
					Icon(
						painter = painterResource(icon),
						contentDescription = "search",
						tint = LocalTheme.colorScheme.onSurface,
						modifier = Modifier
							.size(iconSize)
					)
				}
				Box(
					modifier = Modifier
						.weight(1f)
				) {
					BasicTextField(
						value = text,
						onValueChange = { t ->
							onTextChanged(t)
							evaluateInput(t)
						},
						singleLine = true,
						enabled = true,
						cursorBrush = SolidColor(LocalTheme.colorScheme.onSurface),
						textStyle = LocalTheme.typography.header.copy(
							color = LocalTheme.colorScheme.onBackground
						),
						keyboardOptions = KeyboardOptions(imeAction = imeAction),
						keyboardActions = KeyboardActions(onAny = {
							focusManager.clearFocus()
						}),
						modifier = Modifier
							.fillMaxSize()
							.wrapContentHeight(align = Alignment.CenterVertically)
							.focusRequester(focusRequester)
					)

					if (text == "") {
						Text(
							text = placeholderText,
							color = LocalTheme.colorScheme.onSurface,
							style = LocalTheme.typography.header,
							modifier = Modifier
								.fillMaxHeight()
								.wrapContentHeight(align = Alignment.CenterVertically)
						)
					}
				}
				Spacer(modifier = Modifier.width(5.dp))

				if (text != "") {
					Icon(
						painter = painterResource(R.drawable.round_close_24),
						contentDescription = "search",
						tint = LocalTheme.colorScheme.onSurface,
						modifier = Modifier
							.size(iconSize)
							.clickable(interactionSource = closeInteractionSource, indication = null) {
								onTextChanged("")
								focusManager.clearFocus()
								evaluateInput("")
							}
					)
				}
			}
		}
		AnimatedVisibility(errorText !== "") {
			Column() {
				Spacer(modifier = Modifier.height(2.dp))
				Text(
					text = errorText,
					color = LocalTheme.colorScheme.error,
					fontSize = 14.sp
				)
			}
		}
	}

	LaunchedEffect(focused) {
		if (focused) {
			if (onlyFocusIfEmpty && text.isNotEmpty()) return@LaunchedEffect
			focusRequester.requestFocus()
		} else {
			focusManager.clearFocus()
		}
	}
}