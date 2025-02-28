package com.jxd20072.automationapp.ui.shared

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.wear.compose.material.Text
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.ui.theme.LocalTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

enum class SnackbarType {
	INFO, WARNING, ERROR
}

data class SnackbarActionData(
	val name: String,
	val onClick: () -> Unit
)

data class SnackbarData(
	val text: String,
	val type: SnackbarType,
	val icon: Int = when (type) {
		SnackbarType.INFO -> R.drawable.round_info_24
		SnackbarType.WARNING -> R.drawable.round_warning_24
		SnackbarType.ERROR -> R.drawable.round_error_24
	},
	val duration: Long = 10L,
	val id: String = UUID.randomUUID().toString(),
	val action: SnackbarActionData? = null
)

class SnackbarHostState() {
	var activeSnackbars by mutableStateOf(emptyList<SnackbarData>())

	suspend fun showSnackbar(toastData: SnackbarData) {
		activeSnackbars = activeSnackbars + toastData
		delay(toastData.duration)
		dismissSnackbar(toastData.id)
	}

	fun dismissSnackbar(id: String) {
		activeSnackbars = activeSnackbars.filter { it.id != id }
	}
}

val LocalSnackbarState = compositionLocalOf { SnackbarHostState() }

@Composable
fun SnackbarHost() {
	val localSnackbarState = LocalSnackbarState.current

	Box(
		modifier = Modifier
			.zIndex(100f)
			.padding(horizontal = LocalTheme.layoutSystem.spacing.paddingLevel1)
			.fillMaxSize()
			.windowInsetsPadding(WindowInsets.safeDrawing)
	) {
		LazyColumn(
			userScrollEnabled = false,
			reverseLayout = true,
			verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Bottom),
			modifier = Modifier
				.fillMaxWidth()
				.animateContentSize(alignment = Alignment.BottomCenter)
				.align(Alignment.BottomCenter)
		) {
			items(localSnackbarState.activeSnackbars, { it.id }) { snackbarData ->
				Snackbar(
					snackbarData = snackbarData,
					modifier = Modifier.animateItem(fadeOutSpec = null)
				)
			}
			if (localSnackbarState.activeSnackbars.isNotEmpty()) {
				item { Spacer(modifier = Modifier.height(70.dp)) }
			}
		}
	}
}

@Composable
fun Snackbar(
	modifier: Modifier = Modifier,
	snackbarData: SnackbarData
) {
	val localSnackbarState = LocalSnackbarState.current
	val interactionSource = remember { MutableInteractionSource() }
	val coroutineScope = rememberCoroutineScope()

	var pressing by remember { mutableStateOf(false) }
	val scale by animateFloatAsState(if (pressing) .95f else 1f)
	val remainingTime = remember { Animatable(1f) }

	LaunchedEffect(Unit) {
		remainingTime.animateTo(
			0f, tween(
				durationMillis = snackbarData.duration.toInt(),
				easing = LinearEasing
			)
		)
	}

	LaunchedEffect(interactionSource) {
		interactionSource.interactions.collect { interaction ->
			pressing = interaction is PressInteraction.Press
		}
	}

	Box(
		modifier = modifier
			.fillMaxWidth()
			.graphicsLayer(scaleX = scale, scaleY = scale)
			.clickable(interactionSource, null) {
				localSnackbarState.dismissSnackbar(snackbarData.id)
			}
			.shadow(
				LocalTheme.layoutSystem.shadows.primary,
				RoundedCornerShape(LocalTheme.layoutSystem.shapes.cornerRadiusLevel1)
			)
			.background(
				when (snackbarData.type) {
					SnackbarType.INFO -> LocalTheme.colorScheme.surface
					SnackbarType.WARNING -> LocalTheme.colorScheme.warningContainer
					SnackbarType.ERROR -> LocalTheme.colorScheme.errorContainer
				}
			)
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(5.dp),
			modifier = Modifier.padding(LocalTheme.layoutSystem.spacing.paddingLevel2)
		) {
			Icon(
				painter = painterResource(snackbarData.icon),
				contentDescription = "",
				tint = LocalTheme.colorScheme.onBackground,
				modifier = Modifier.size(20.dp)
			)
			Text(
				text = snackbarData.text,
				fontSize = 17.sp,
				modifier = Modifier.weight(1f)
			)
			if (snackbarData.action != null) {
				Button(
					color = when (snackbarData.type) {
						SnackbarType.INFO -> LocalTheme.colorScheme.tertiary
						SnackbarType.WARNING -> LocalTheme.colorScheme.warningSecondary
						SnackbarType.ERROR -> LocalTheme.colorScheme.errorSecondary
					},
					cornerRadius = 5.dp,
					onClick = {
						snackbarData.action.onClick()
						coroutineScope.launch {
							pressing = true
							delay(100)
							localSnackbarState.dismissSnackbar(snackbarData.id)
						}
					},
					modifier = Modifier
				) {
					Text(
						text = snackbarData.action.name,
						fontSize = 17.sp,
						lineHeight = 1.sp,
						modifier = Modifier.padding(LocalTheme.layoutSystem.spacing.paddingLevel2)
					)
				}
			}
		}
		Box(
			modifier = Modifier
				.fillMaxWidth(remainingTime.value)
				.height(3.dp)
				.align(Alignment.BottomStart)
				.clip(RoundedCornerShape(LocalTheme.layoutSystem.shapes.cornerRadiusLevel3))
				.background(
					when (snackbarData.type) {
						SnackbarType.INFO -> LocalTheme.colorScheme.secondary
						SnackbarType.WARNING -> LocalTheme.colorScheme.warning
						SnackbarType.ERROR -> LocalTheme.colorScheme.error
					}
				)
		)
	}
}