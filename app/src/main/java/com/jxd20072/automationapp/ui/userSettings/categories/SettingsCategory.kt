package com.jxd20072.automationapp.ui.userSettings.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsCategory(
	name: String,
	modifier: Modifier = Modifier,
	content: @Composable ColumnScope.() -> Unit
) {
	Column(
		verticalArrangement = Arrangement.spacedBy(15.dp)
	) {
		Text(
			text = name,
			color = MaterialTheme.colorScheme.secondary,
			fontSize = 18.sp
		)
		content()
	}
}