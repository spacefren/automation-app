package com.jxd20072.automationapp.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.ui.shared.BaseTextInput
import com.jxd20072.automationapp.ui.shared.IconButton
import com.jxd20072.automationapp.ui.shared.ValidationResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAutomationSheet(
	show: Boolean,
	sheetState: SheetState,
	modifier: Modifier = Modifier,
	onDismissRequest: () -> Unit,
	onCreateAutomation: (name: String) -> Unit,
) {
	if (show) {
		ModalBottomSheet(
			onDismissRequest = onDismissRequest,
			sheetState = sheetState,
			containerColor = MaterialTheme.colorScheme.background,
		) {
			Column(verticalArrangement = Arrangement.spacedBy(15.dp), modifier = modifier) {
				Row() {
					Text(
						text = "New automation",
						color = MaterialTheme.colorScheme.onBackground,
						fontSize = 27.sp
					)
				}
				Row(
					horizontalArrangement = Arrangement.spacedBy(10.dp)
				) {
					IconButton(
						color = MaterialTheme.colorScheme.surface,
						iconColor = MaterialTheme.colorScheme.onSurface,
						size = 45.dp,
						icon = R.drawable.function,
						cornerRadius = 11.dp,
						elevation = 10.dp,
						padding = 8.dp
					) { }
					BaseTextInput(
						placeholderText = "Name",
						imeAction = ImeAction.Done,
						autoFocus = true,
						validateInput = { text ->
							if (text == "" || text == "invalid") {
								return@BaseTextInput ValidationResult.Error("Invalid name: idk bro")
							}
							ValidationResult.Success
						}
					) {}
				}
				Spacer(modifier = Modifier.height(10.dp))
				Spacer(modifier = Modifier.height(100.dp))
			}
		}
	}
}