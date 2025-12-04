package com.jxd20072.automationapp.ui.editor.nodeTree

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.draggable2D
import androidx.compose.foundation.gestures.rememberDraggable2DState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.core.data.model.NodeDef
import com.jxd20072.automationapp.core.nodes.actionNodes
import com.jxd20072.automationapp.ui.editor.viewmodel.EditorNodeDragState
import com.jxd20072.automationapp.ui.theme.LocalTheme
import com.jxd20072.automationapp.ui.utils.conditional

enum class TopLevelNodeType { ENTRYPOINT, FUNCTION, TRIGGER }

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun NodeHeaderIcons(
    onDragStart: ((startedPosition: Offset) -> Unit)? = null,
    forceShowDragHandle: Boolean = false,
    icon: Int
) {
    var dragHandlePosition by remember { mutableStateOf(Offset(0f, 0f)) }

    val shouldShowDragHandle =
        onDragStart != null
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (shouldShowDragHandle || forceShowDragHandle) {
            Box(
                Modifier.width(10.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.round_drag_indicator_24),
                    tint = LocalTheme.colorScheme.onSurface,
                    contentDescription = "dragIndicator",
                    modifier = Modifier
                        .size(20.dp)
                        .requiredWidth(IntrinsicSize.Max)
                        .onGloballyPositioned { layout ->
                            dragHandlePosition = layout.positionInRoot()
                        }
                        .conditional(shouldShowDragHandle, ifTrue = {
                            draggable2D(
                                state = rememberDraggable2DState { },
                                onDragStarted = {
                                    onDragStart!!(dragHandlePosition + it)
                                },
                            )
                        })
                )
            }
        }
        Icon(
            painter = painterResource(icon),
            tint = LocalTheme.colorScheme.onBackground,
            contentDescription = "dragIndicator",
            modifier = Modifier
                .size(22.dp)
                .aspectRatio(.7f)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun NodeHeader(
    displayName: List<String>,
    nodeId: String,
    icon: Int,
    level: Int,
    onDragStart: ((startedPosition: Offset) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        NodeHeaderIcons(
            onDragStart = onDragStart,
            icon = icon
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalArrangement = Arrangement.Top
        ) {
            displayName.forEach { piece ->
                if (piece.startsWith("*")) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(LocalTheme.layoutSystem.shapes.cornerRadiusLevel3))
                            .background(
                                when (level) {
                                    1 -> LocalTheme.colorScheme.background
                                    2 -> LocalTheme.colorScheme.surface
                                    else -> LocalTheme.colorScheme.tertiary
                                }
                            )
                            .padding(LocalTheme.layoutSystem.spacing.paddingLevel3)
                    ) {
                        Text(
                            text = piece.removePrefix("*"),
                            color = LocalTheme.colorScheme.onBackground,
                            style = LocalTheme.typography.body.copy(
                                fontStyle = FontStyle.Italic
                            ),
                        )
                    }
                } else {
                    piece.split(" ").forEach { word ->
                        Text(
                            text = word,
                            color = LocalTheme.colorScheme.onBackground,
                            style = LocalTheme.typography.header,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NodeContainer(
    modifier: Modifier = Modifier,
    level: Int,
    disabledForDragging: Boolean = false,
    enabledByDragging: Boolean? = false,
    onLayout: (layout: LayoutCoordinates) -> Unit,
    ignoreBottomPadding: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val padding = when (level) {
        1 -> LocalTheme.layoutSystem.spacing.paddingLevel2
        2 -> LocalTheme.layoutSystem.spacing.paddingLevel3
        else -> 0.dp
    }

    Box(
        content = content,
        modifier = modifier
            .graphicsLayer(
                alpha = if (!disabledForDragging) 1f else .3f
            )
            .conditional(enabledByDragging == false, ifTrue = {
                fillMaxWidth()
            })
            .shadow(
                if (enabledByDragging == true) LocalTheme.layoutSystem.shadows.primary
                else LocalTheme.layoutSystem.shadows.secondary,
                RoundedCornerShape(
                    when (level) {
                        1 -> LocalTheme.layoutSystem.shapes.cornerRadiusLevel1
                        2 -> LocalTheme.layoutSystem.shapes.cornerRadiusLevel2
                        else -> LocalTheme.layoutSystem.shapes.cornerRadiusLevel3
                    }
                )
            )
            .background(
                when (level) {
                    1 -> LocalTheme.colorScheme.surface
                    2 -> LocalTheme.colorScheme.tertiary
                    else -> Color.Red
                }
            )
            .onGloballyPositioned { layout ->
                onLayout(layout)
            }
            .conditional(!ignoreBottomPadding, ifTrue = {
                padding(padding)
            }, ifFalse = {
                padding(start = padding, end = padding, top = padding)
            })
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TopLevelNode(
    type: TopLevelNodeType,
    id: String,
    displayName: List<String>,
    draggingData: EditorNodeDragState,
    onDragStart: (
        id: String,
        nodePosition: Offset,
        pressedPosition: Offset,
    ) -> Unit,
    onDropZoneEnter: (targetIndex: Int, parentId: String?) -> Unit,
    onDropZoneExit: () -> Unit,
    modifier: Modifier = Modifier,
    content: List<NodeDef>
) {
    val draggingNodeID =
        if (draggingData is EditorNodeDragState.Dragging) draggingData.draggingNode.id
        else null
    val isDragging = draggingNodeID == id
    var rootPosition by remember { mutableStateOf(Offset(0f, 0f)) }

    NodeContainer(
        modifier = modifier,
        level = 1,
        onLayout = { layout ->
            rootPosition = layout.positionInRoot()
        },
        disabledForDragging = isDragging,
        ignoreBottomPadding = true
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NodeHeader(
                displayName = displayName,
                nodeId = id,
                level = 1,
                icon = when (type) {
                    TopLevelNodeType.TRIGGER -> R.drawable.round_electric_bolt_24
                    TopLevelNodeType.ENTRYPOINT -> R.drawable.round_play_arrow_24
                    TopLevelNodeType.FUNCTION -> R.drawable.round_functions_24
                },
                onDragStart = {
                    onDragStart(id, rootPosition, it)
                }
            )
            content.forEachIndexed { index, actionNode ->
                EditorNodeDropTarget(
                    gap = LocalTheme.layoutSystem.spacing.paddingLevel2,
                    draggingData = draggingData,
                    dropScope = NodeDropScope.CONTENT,
                    onEnter = { onDropZoneEnter(index, id) },
                    onExit = onDropZoneExit
                )

                ActionNode(
                    actionNode = actionNode,
                    onDragStart = onDragStart,
                    disabledForDragging = actionNode.id == draggingNodeID,
                    modifier = Modifier
                )
            }
            EditorNodeDropTarget(
                gap = LocalTheme.layoutSystem.spacing.paddingLevel2,
                draggingData = draggingData,
                dropScope = NodeDropScope.CONTENT,
                onEnter = { onDropZoneEnter(content.size, id) },
                onExit = onDropZoneExit
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ActionNode(
    modifier: Modifier = Modifier,
    disabledForDragging: Boolean,
    actionNode: NodeDef,
    onDragStart: ((
        id: String,
        position: Offset,
        startedOffset: Offset,
    ) -> Unit)? = null,
) {
    var rootPosition by remember { mutableStateOf(Offset(0f, 0f)) }

    val actionData = actionNodes[actionNode.name]?.data
    if (actionData != null) {
        NodeContainer(
            modifier = modifier,
            level = 2,
            onLayout = { layout ->
                rootPosition = layout.positionInRoot()
            },
            disabledForDragging = disabledForDragging
        ) {
            NodeHeader(
                displayName = actionData.editorActionDescription(
                    actionData.inputDefinitions.mapValues { it.value.defaultValue } + actionNode.input
                ),
                level = 2,
                nodeId = actionNode.id,
                icon = actionData.icon,
                onDragStart = {
                    onDragStart?.invoke(actionNode.id, rootPosition, it)
                }
            )
        }
    }
}
