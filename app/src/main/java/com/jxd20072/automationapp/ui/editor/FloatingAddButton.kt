package com.jxd20072.automationapp.ui.editor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.ui.shared.IconButton
import com.jxd20072.automationapp.ui.shared.IconLabelButton
import com.jxd20072.automationapp.ui.theme.LocalTheme
import com.jxd20072.automationapp.ui.utils.conditional
import kotlinx.coroutines.delay

@Composable
fun FloatingSubButton(
	name: String,
	icon: Int,
	columnIndex: Int,
	columnItemCount: Int,
	show: Boolean,
	onClick: () -> Unit = { },
) {
	var shouldShow by remember { mutableStateOf(false) }

	LaunchedEffect(show) {
		if (show) {
			delay(100L * columnIndex)
			shouldShow = true
		} else {
			delay((columnItemCount - 1 - columnIndex) * 100L)
			shouldShow = false
		}
	}

	AnimatedVisibility(
		visible = shouldShow,
		enter = slideInVertically { it / 4 } + fadeIn(),
		exit = slideOutVertically { -it / 4 } + fadeOut()
	) {
		IconLabelButton(
			label = name,
			fontSize = 17.sp,
			drawableRes = icon,
			color = LocalTheme.colorScheme.tertiary,
			iconColor = LocalTheme.colorScheme.onBackground,
			padding = LocalTheme.layoutSystem.spacing.paddingLevel2,
			cornerRadius = LocalTheme.layoutSystem.shapes.cornerRadiusLevel1,
			iconSize = 20.dp,
			onClick = onClick
		)
	}
}

@Composable
fun EditorFloatingAddButton(
	onCreateNodePressed: () -> Unit,
	onCreateFunctionPressed: () -> Unit
) {
	var showSubButtons by remember { mutableStateOf(false) }
	val backgroundAlpha by animateFloatAsState(if (showSubButtons) .5f else 0f)

	Box(
		modifier = Modifier
			.fillMaxSize()
			.background(
				Brush.verticalGradient(listOf(Color.Transparent, Color(0f, 0f, 0f, backgroundAlpha)))
			)
			.conditional(showSubButtons, ifTrue = {
				pointerInput(Unit) {
					detectTapGestures(onPress = { showSubButtons = false })
				}
			})
	) {
		Box(
			modifier = Modifier
				.align(Alignment.BottomEnd)
				.offset(x = (-20).dp, y = (-30).dp)
		) {
			Column(
				horizontalAlignment = Alignment.End,
				verticalArrangement = Arrangement.spacedBy(10.dp)
			) {
				FloatingSubButton(
					name = "Function",
					icon = R.drawable.round_functions_24,
					columnIndex = 1,
					columnItemCount = 2,
					show = showSubButtons,
					onClick = {
						onCreateFunctionPressed()
						showSubButtons = false
					}
				)
				FloatingSubButton(
					name = "Action",
					icon = R.drawable.round_play_arrow_24,
					columnIndex = 0,
					columnItemCount = 2,
					show = showSubButtons,
					onClick = {
						onCreateNodePressed()
						showSubButtons = false
					}
				)
				IconButton(
					color = LocalTheme.colorScheme.secondary,
					iconColor = LocalTheme.colorScheme.onSecondary,
					drawableRes = R.drawable.round_add_24,
					cornerRadius = LocalTheme.layoutSystem.shapes.cornerRadiusLevel1,
					padding = LocalTheme.layoutSystem.spacing.paddingLevel2,
					modifier = Modifier.height(50.dp)
				) {
					showSubButtons = !showSubButtons
				}
			}
		}
	}
}