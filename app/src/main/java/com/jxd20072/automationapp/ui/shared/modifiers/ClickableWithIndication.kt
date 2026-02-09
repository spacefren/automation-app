package com.jxd20072.automationapp.ui.shared.modifiers

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.clickableWithIndication(
    onClick: ((offset: Offset) -> Unit) = { },
    onLongPress: ((offset: Offset) -> Unit) = { },
): Modifier {
    var isPressed by remember { mutableStateOf(false) }
    var size by remember { mutableStateOf(IntSize.Zero) }

    val scale by animateFloatAsState(
        if (isPressed && size.width > 0)
            (size.width - LocalDensity.current.run { 7.dp.toPx() }) / size.width
        else
            1f
    )

    return Modifier
        .onSizeChanged { size = it }
        .pointerInput(Unit) {
            detectTapGestures(
                onTap = { onClick(it) },
                onLongPress = { onLongPress(it) },
                onPress = {
                    isPressed = true
                    try {
                        awaitRelease()
                    } finally {
                        isPressed = false
                    }
                }
            )
        }
        .graphicsLayer(
            scaleX = scale,
            scaleY = scale
        )
        .then(this)
}

@Composable
fun Modifier.clickableWithIndication(
    onClick: (offset: Offset) -> Unit
) = clickableWithIndication(onClick = onClick, onLongPress = { })