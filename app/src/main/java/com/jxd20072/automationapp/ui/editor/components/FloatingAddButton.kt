package com.jxd20072.automationapp.ui.editor.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.ui.shared.components.IconLabelButton
import com.jxd20072.automationapp.ui.shared.modifiers.borderWithShadow
import com.jxd20072.automationapp.ui.shared.modifiers.clickableWithIndication
import com.jxd20072.automationapp.ui.shared.modifiers.conditional
import com.jxd20072.automationapp.ui.shared.modifiers.padding
import com.jxd20072.automationapp.ui.theme.LocalTheme
import com.jxd20072.automationapp.ui.theme.NestedLevel
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
            textStyle = LocalTheme.typography.header,
            drawableRes = icon,
            color = LocalTheme.colorScheme.tertiary,
            iconColor = LocalTheme.colorScheme.onBackground,
            nestedLevel = NestedLevel.ONE,
            iconSize = 20.dp,
            onClick = onClick
        )
    }
}

@Composable
fun EditorFloatingAddButton(
    hidden: Boolean,
    onCreateNodePressed: () -> Unit,
    onCreateFunctionPressed: () -> Unit,
    onCreateTriggerPressed: () -> Unit,
) {
    var showSubButtons by remember { mutableStateOf(false) }
    val backgroundAlpha by animateFloatAsState(if (showSubButtons) .5f else 0f)
    val rotation by animateFloatAsState(if (showSubButtons) 45f else 0f)
    val extraOffset by animateFloatAsState(if (hidden) 75f else 0f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.Transparent,
                        Color(0f, 0f, 0f, backgroundAlpha)
                    )
                )
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
                .offset(x = (-20).dp, y = (-30 + extraOffset).dp)
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FloatingSubButton(
                    name = "Trigger",
                    icon = R.drawable.round_electric_bolt_24,
                    columnIndex = 2,
                    columnItemCount = 3,
                    show = showSubButtons,
                    onClick = {
                        onCreateTriggerPressed()
                        showSubButtons = false
                    }
                )
                FloatingSubButton(
                    name = "Function",
                    icon = R.drawable.round_functions_24,
                    columnIndex = 1,
                    columnItemCount = 3,
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
                    columnItemCount = 3,
                    show = showSubButtons,
                    onClick = {
                        onCreateNodePressed()
                        showSubButtons = false
                    }
                )
                Box(
                    modifier = Modifier
                        .borderWithShadow(LocalTheme, NestedLevel.ONE)
                        .background(LocalTheme.colorScheme.secondary)
                        .padding(LocalTheme, NestedLevel.THREE)
                        .clickableWithIndication {
                            showSubButtons = !showSubButtons
                        }
                ) {
                    Image(
                        painter = painterResource(R.drawable.round_add_24),
                        contentDescription = "new node",
                        colorFilter = ColorFilter.tint(LocalTheme.colorScheme.onSecondary),
                        modifier = Modifier
                            .size(45.dp)
                            .rotate(rotation)
                            .padding(LocalTheme.layoutSystem.spacing.paddingLevel2),
                    )
                }
            }
        }
    }
}