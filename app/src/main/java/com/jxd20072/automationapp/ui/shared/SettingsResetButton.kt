package com.jxd20072.automationapp.ui.shared

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.ui.theme.LocalTheme
import kotlinx.coroutines.launch

@Composable
fun SettingsResetButton(
	action: () -> Unit
) {
	val coroutineScope = rememberCoroutineScope()
	val resetIconRotation = remember { Animatable(0f) }

	Icon(
		painter = painterResource(R.drawable.round_loop_24),
		contentDescription = "settings",
		tint = LocalTheme.colorScheme.onBackground,
		modifier = Modifier
			.size(28.dp)
			.rotate(resetIconRotation.value)
			.clickableWithIndication {
				action()

				coroutineScope.launch {
					resetIconRotation.animateTo(
						-360f,
						animationSpec = spring(
							dampingRatio = 0.6f,
							stiffness = 80f
						)
					)
					resetIconRotation.snapTo(0f)
				}
			}
	)
}