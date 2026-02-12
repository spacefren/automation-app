package com.jxd20072.automationapp.ui.editor.editorBottomSheet

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.EditorNodeDragState
import com.jxd20072.automationapp.ui.shared.getTextSize
import com.jxd20072.automationapp.ui.shared.modifiers.conditional
import com.jxd20072.automationapp.ui.shared.modifiers.dashedBorder
import com.jxd20072.automationapp.ui.theme.LocalTheme

@Composable
fun NodeDropZone(
    dragData: EditorNodeDragState,
    onDropZoneEnter: () -> Unit,
    onDropZoneExit: () -> Unit,
    mainColor: Color,
    accentColor: Color,
    text: String,
    icon: Int,
    modifier: Modifier = Modifier
) {
    var isHovering by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(if (isHovering) 1f else .95f)
    var positionInRoot by remember { mutableStateOf(Offset(0f, 0f)) }
    var size by remember { mutableStateOf(IntSize(0, 0)) }
    var textWidth by remember { mutableFloatStateOf(0f) }

    @Composable
    fun checkHovering(): Boolean {
        return if (dragData is EditorNodeDragState.Dragging) {
            val dragPos = dragData.nodePosition

            dragPos.y > positionInRoot.y && dragPos.y < positionInRoot.y + size.height
                    && dragPos.x > positionInRoot.x && dragPos.x < positionInRoot.x + size.width
        } else false
    }

    isHovering = checkHovering()

    LaunchedEffect(isHovering) {
        if (isHovering) {
            onDropZoneEnter()
        } else {
            onDropZoneExit()
        }
    }

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .fillMaxSize()
            .onGloballyPositioned { layout ->
                val rootPos = layout.positionInRoot()
                positionInRoot = Offset(rootPos.x, rootPos.y)
                size = layout.size
            }
            .shadow(
                LocalTheme.layoutSystem.shadows.primary,
                RoundedCornerShape(LocalTheme.layoutSystem.shapes.cornerRadiusLevel1)
            )
            .background(mainColor)
            .padding(LocalTheme.layoutSystem.spacing.paddingLevel3)
            .dashedBorder(
                color = accentColor,
                shape = RoundedCornerShape(LocalTheme.layoutSystem.shapes.cornerRadiusLevel3),
                strokeWidth = 2.dp,
                dashLength = 20.dp,
                gapLength = 8.dp
            )
            .padding(LocalTheme.layoutSystem.spacing.paddingLevel3)
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = "$text icon",
            colorFilter = ColorFilter.tint(LocalTheme.colorScheme.onBackground),
            modifier = Modifier
                .size(24.dp)
        )
        Spacer(modifier = Modifier.width(LocalTheme.layoutSystem.spacing.paddingLevel2))
        Text(
            text = text,
            color = LocalTheme.colorScheme.onBackground,
            fontSize = 17.sp,
            onTextLayout = {
                textWidth = getTextSize(it)
            },
            modifier = Modifier
                .conditional(textWidth != 0f, ifTrue = {
                    width(with(LocalDensity.current) { textWidth.toDp() })
                })
        )
    }
}