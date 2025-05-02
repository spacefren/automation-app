package com.jxd20072.automationapp.ui.home.addAutomationSheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.ui.shared.BaseTextInput
import com.jxd20072.automationapp.ui.shared.IconButton
import com.jxd20072.automationapp.ui.shared.TextInputValidationResult
import com.jxd20072.automationapp.ui.shared.clickableWithIndication
import com.jxd20072.automationapp.ui.theme.LocalTheme

@Composable
fun AddAutomationScreen(
	selectedIcon: String,
	name: String,
	isSheetOpen: Boolean,
	autoOpenKeyboard: Boolean,
	validateAutomationName: (name: String) -> TextInputValidationResult,
	onNameChanged: (name: String) -> Unit,
	onIconChangeRequest: () -> Unit,
	onCreateAutomation: () -> Unit,
) {
	Column(
		verticalArrangement = Arrangement.spacedBy(15.dp),
		modifier = Modifier
			.imePadding()
			.padding(LocalTheme.layoutSystem.spacing.paddingLevel1)
	) {
		Row(
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.Bottom,
			modifier = Modifier
				.fillMaxWidth()
		) {
			Text(
				text = "New automation",
				color = LocalTheme.colorScheme.onBackground,
				style = LocalTheme.typography.subTitle,
			)
			Icon(
				painter = painterResource(R.drawable.round_check_24),
				contentDescription = "confirm",
				tint = LocalTheme.colorScheme.onBackground,
				modifier = Modifier
					.size(28.dp)
					.clickableWithIndication {
						if (validateAutomationName(name) is TextInputValidationResult.Success) {
							onCreateAutomation()
						}
					}
			)
		}
		Row(
			horizontalArrangement = Arrangement.spacedBy(10.dp)
		) {
			IconButton(
				color = LocalTheme.colorScheme.surface,
				iconColor = LocalTheme.colorScheme.onSurface,
				iconName = selectedIcon,
				cornerRadius = 8.dp,
				padding = 8.dp,
				size = 45.dp
			) {
				onIconChangeRequest()
			}
			BaseTextInput(
				placeholderText = "Name",
				imeAction = ImeAction.Done,
				onlyFocusIfEmpty = true,
				focused = if (autoOpenKeyboard) isSheetOpen else false,
				text = name,
				validateInput = { text -> validateAutomationName(text) }
			) { text ->
				onNameChanged(text)
			}
		}
		Spacer(modifier = Modifier.height(100.dp))
	}
}