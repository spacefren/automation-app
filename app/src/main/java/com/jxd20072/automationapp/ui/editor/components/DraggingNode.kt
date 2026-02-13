package com.jxd20072.automationapp.ui.editor.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.jxd20072.automationapp.core.data.model.NodeType
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.EditorNodeDragState
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.EditorNodeDropData
import com.jxd20072.automationapp.ui.shared.modifiers.borderWithShadow
import com.jxd20072.automationapp.ui.shared.modifiers.getPaddingForNestedLevel
import com.jxd20072.automationapp.ui.theme.LocalTheme
import com.jxd20072.automationapp.ui.theme.getNextNestedLevel
import kotlin.math.abs

enum class NodeDropScope {
    ENTRY, CONTENT
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DraggingNode(
    draggingData: EditorNodeDragState,
    modifier: Modifier = Modifier
) {
    if (draggingData is EditorNodeDragState.Dragging) {
        val nestedLevel = getNestedLevelForDepth(draggingData.nodeDepth)

        Box(
            modifier = modifier
                .zIndex(40f)
                .graphicsLayer {
                    translationX = draggingData.nodePosition.x
                    translationY = draggingData.nodePosition.y
                }
        ) {
            Row(
                modifier = Modifier
                    .borderWithShadow(LocalTheme, nestedLevel)
                    .background(getBackgroundForDepth(draggingData.nodeDepth, LocalTheme))
                    .padding(getPaddingForNestedLevel(LocalTheme, getNextNestedLevel(nestedLevel)))
            ) {
                NodeDragHandle { }
                NodeHeaderIcon(draggingData.draggingNode)
            }
        }
    }
}

@Composable
fun EditorNodeDropTarget(
    gap: Dp,
    index: Int,
    parentId: String?,
    dropScope: NodeDropScope,
    draggingData: EditorNodeDragState,
    onDetect: (distance: Float) -> Unit,
    onExit: () -> Unit,
) {
    Spacer(modifier = Modifier.height(gap / 2))

    if (draggingData is EditorNodeDragState.Dragging) {
        val checkHovering = { position: Offset ->
            val pos = draggingData.nodePosition.y
            val offset = draggingData.offset.y
            val distance = abs(pos + offset - position.y)
            (when (dropScope) {
                NodeDropScope.ENTRY ->
                    draggingData.draggingNode.nodeType == NodeType.FUNCTION
                            || draggingData.draggingNode.nodeType == NodeType.TRIGGER

                NodeDropScope.CONTENT -> draggingData.draggingNode.nodeType == NodeType.ACTION
            } && distance < 40) to distance
        }

        var positionInRoot by remember { mutableStateOf(Offset(0f, 0f)) }
        val (shouldShow, distance) = checkHovering(positionInRoot)

        if (shouldShow) {
            onDetect(distance)
        } else {
            onExit()
        }

        val shouldShowFr = draggingData.dropData is EditorNodeDropData.Moving
                && draggingData.dropData.targetIndex == index
                && draggingData.dropData.parentId == parentId

        val height by animateDpAsState(if (shouldShowFr) 4.dp else 0.dp)
        Box(
            modifier = Modifier
                .padding(vertical = height / 2)
                .height(height)
                .fillMaxWidth(.8f)
                .background(
                    LocalTheme.colorScheme.secondary,
                    RoundedCornerShape(LocalTheme.layoutSystem.shapes.cornerRadiusLevel2)
                )
                .onGloballyPositioned { layout -> positionInRoot = layout.positionInRoot() }
        )
    }

    Spacer(modifier = Modifier.height(gap / 2))
}