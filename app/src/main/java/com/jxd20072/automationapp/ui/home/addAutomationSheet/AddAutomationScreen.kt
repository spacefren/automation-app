package com.jxd20072.automationapp.ui.home.addAutomationSheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.ui.shared.BaseTextInput
import com.jxd20072.automationapp.ui.shared.IconButton
import com.jxd20072.automationapp.ui.shared.ValidationResult

@Composable
fun AddAutomationScreen(
	selectedIcon: String,
	name: String,
	onNameChanged: (name: String) -> Unit,
	onIconChangeRequest: () -> Unit,
	onCreateAutomation: () -> Unit,
) {

	fun validateText(text: String): ValidationResult {
		return when {
			text == "" -> ValidationResult.Error("Name cannot be empty.")
			text.length > 25 -> ValidationResult.Error("Name is too long.")
			else -> ValidationResult.Success
		}
	}

	Column(verticalArrangement = Arrangement.spacedBy(15.dp)) {
		Row(
			horizontalArrangement = Arrangement.SpaceAround,
			verticalAlignment = Alignment.Bottom
		) {
			Text(
				text = "New automation",
				color = MaterialTheme.colorScheme.onBackground,
				fontSize = 27.sp,
				modifier = Modifier.weight(1f)
			)
			IconButton(
				color = MaterialTheme.colorScheme.secondary,
				iconColor = MaterialTheme.colorScheme.onSecondary,
				drawableRes = R.drawable.round_check_24,
				cornerRadius = 8.dp,
				padding = 5.dp,
				modifier = Modifier.size(45.dp)
			) {
				if (validateText(name) is ValidationResult.Success) {
					onCreateAutomation()
				}
			}
		}
		Row(
			horizontalArrangement = Arrangement.spacedBy(10.dp)
		) {
			IconButton(
				color = MaterialTheme.colorScheme.surface,
				iconColor = MaterialTheme.colorScheme.onSurface,
				iconName = selectedIcon,
				cornerRadius = 8.dp,
				padding = 8.dp,
				modifier = Modifier.size(45.dp)
			) {
				onIconChangeRequest()
			}
			BaseTextInput(placeholderText = "Name",
				imeAction = ImeAction.Done,
				focused = false,
				text = name,
				validateInput = { text -> validateText(text) }) { text ->
				onNameChanged(text)
			}
		}
		Spacer(modifier = Modifier.height(100.dp))
	}
}