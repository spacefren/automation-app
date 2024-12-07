package com.jxd20072.automationapp.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jxd20072.automationapp.R

@Composable
fun FloatingNewButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
	Row(
		modifier = modifier
			.height(60.dp)
			.offset(-(20).dp, -(30).dp)
			.shadow(26.dp, RoundedCornerShape(8.dp))
			.clickable { onClick() }
			.background(MaterialTheme.colorScheme.primary)
			.padding(15.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(5.dp)
	) {
		Icon(
			painter = painterResource(R.drawable.round_add_24),
			contentDescription = "new automation",
			tint = MaterialTheme.colorScheme.onPrimary,
			modifier = Modifier.size(30.dp)
		)

		Text(
			text = "New",
			fontSize = 20.sp,
			color = MaterialTheme.colorScheme.onPrimary
		)
	}
}