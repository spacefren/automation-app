package com.jxd20072.automationapp.ui.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.draggable2D
import androidx.compose.foundation.gestures.rememberDraggable2DState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.core.data.model.NodeDef
import com.jxd20072.automationapp.core.data.model.NodeType
import com.jxd20072.automationapp.core.data.model.VariableDef
import com.jxd20072.automationapp.core.nodes.NodeData
import com.jxd20072.automationapp.core.nodes.NodeDescriptionPiece
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.EditorNodeDragState
import com.jxd20072.automationapp.ui.editor.viewmodel.utils.EditorProblem
import com.jxd20072.automationapp.ui.shared.components.DropdownMenu
import com.jxd20072.automationapp.ui.shared.modifiers.borderWithShadow
import com.jxd20072.automationapp.ui.shared.modifiers.conditional
import com.jxd20072.automationapp.ui.shared.modifiers.getPaddingForNestedLevel
import com.jxd20072.automationapp.ui.theme.AppTheme
import com.jxd20072.automationapp.ui.theme.LocalTheme
import com.jxd20072.automationapp.ui.theme.NestedLevel
import com.jxd20072.automationapp.ui.theme.getNextNestedLevel
import com.jxd20072.automationapp.utils.getNodeData

fun getBackgroundForDepth(depth: Int, theme: AppTheme): Color {
    return when {
        depth % 2 == 0 -> theme.colorScheme.surface
        else -> theme.colorScheme.tertiary
    }
}

fun getSurfaceForDepth(depth: Int, theme: AppTheme): Color {
    return when {
        depth % 2 == 0 -> theme.colorScheme.background
        else -> theme.colorScheme.surface
    }
}

fun getNestedLevelForDepth(depth: Int): NestedLevel {
    return when (depth) {
        0 -> NestedLevel.ONE
        1 -> NestedLevel.TWO
        else -> NestedLevel.THREE
    }
}

@Composable
fun NodeDragHandle(
    onDragStart: ((
        dragPosition: Offset,
    ) -> Unit),
) {
    var dragHandlePosition by remember { mutableStateOf(Offset(0f, 0f)) }

    Icon(
        painter = painterResource(R.drawable.round_drag_indicator_24),
        tint = LocalTheme.colorScheme.onSurface,
        contentDescription = "dragIndicator",
        modifier = Modifier
            .size(20.dp)
            .onGloballyPositioned { layout ->
                dragHandlePosition = layout.positionInRoot()
            }
            .draggable2D(
                state = rememberDraggable2DState { },
                onDragStarted = {
                    onDragStart(
                        dragHandlePosition + it
                    )
                }
            )
    )
}

@Composable
fun NodeHeaderIcon(
    nodeDef: NodeDef
) {
    val nodeData = getNodeData(nodeDef)

    val icon: Int = when {
        nodeDef.id == "0" -> R.drawable.round_play_arrow_24
        nodeDef.nodeType == NodeType.FUNCTION -> R.drawable.round_functions_24
        nodeData != null -> nodeData.category.icon
        else -> R.drawable.round_error_24
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

@Composable
fun Node(
    nodeDef: NodeDef,
    variables: Set<VariableDef>,
    depth: Int,
    problems: Set<EditorProblem.WithNode>,
    dragState: EditorNodeDragState,
    editNode: (nodeId: String) -> Unit,
    onDropLineEnter: (targetIndex: Int, parentId: String?) -> Unit,
    onDropLineExit: () -> Unit,
    onDragStart: ((
        nodeId: String,
        position: Offset,
        offset: Offset,
    ) -> Unit),
) {
    var rootNodePosition by remember { mutableStateOf(Offset(0f, 0f)) }
    var nodeData: NodeData? = null

    val displayName = if (nodeDef.nodeType == NodeType.FUNCTION) {
        listOf(NodeDescriptionPiece.Text(nodeDef.name))
    } else {
        nodeData = getNodeData(nodeDef)

        if (nodeData != null) {
            nodeData.editorActionDescription(
                nodeData.inputDefinitions.mapValues { it.value.defaultValue } + nodeDef.input,
                variables
            )
        } else {
            listOf(NodeDescriptionPiece.Text(nodeDef.name))
        }
    }.map {
        if (it is NodeDescriptionPiece.Text)
            it.value.split(' ').map { word -> NodeDescriptionPiece.Text(word) }
        else listOf(it)
    }.flatten()

    val nestedLevel = getNestedLevelForDepth(depth)
    val padding = getPaddingForNestedLevel(LocalTheme, getNextNestedLevel(nestedLevel))

    DropdownMenu(
        onNormalClick = { editNode(nodeDef.id) },
        longPress = true,
        options = emptyList()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .onGloballyPositioned { rootNodePosition = it.positionInRoot() }
                .alpha(
                    if (nodeDef.id == (dragState as? EditorNodeDragState.Dragging)?.draggingNode?.id) .7f
                    else 1f
                )
                .fillMaxWidth()
                .borderWithShadow(LocalTheme, nestedLevel)
                .background(getBackgroundForDepth(depth, LocalTheme))
                .padding(horizontal = padding)
                .padding(top = padding)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NodeDragHandle { dragPosition ->
                        onDragStart(
                            nodeDef.id,
                            rootNodePosition,
                            dragPosition - rootNodePosition
                        )
                    }
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel3),
                        itemVerticalAlignment = Alignment.CenterVertically
                    ) {
                        NodeHeaderIcon(nodeDef)
                        displayName.forEach {
                            Text(
                                text = it.value,
                                style = when (it) {
                                    is NodeDescriptionPiece.Text -> LocalTheme.typography.header
                                    else -> LocalTheme.typography.body
                                },
                                color = LocalTheme.colorScheme.onBackground,
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .conditional(it !is NodeDescriptionPiece.Text, {
                                        borderWithShadow(
                                            LocalTheme,
                                            getNextNestedLevel(nestedLevel)
                                        )
                                            .background(getSurfaceForDepth(depth, LocalTheme))
                                            .padding(LocalTheme.layoutSystem.spacing.paddingLevel3)
                                    })
                            )
                        }
                    }
                }
            }

            if (nodeDef.nodeType != NodeType.ACTION || nodeData?.canHaveContent != false) {
                EditorNodeDropTarget(
                    gap = padding,
                    draggingData = dragState,
                    index = 0,
                    parentId = nodeDef.id,
                    dropScope = NodeDropScope.CONTENT,
                    onEnter = { onDropLineEnter(0, nodeDef.id) },
                    onExit = onDropLineExit
                )

                nodeDef.content.forEachIndexed { index, node ->
                    Node(
                        nodeDef = node,
                        variables = variables,
                        depth = depth + 1,
                        problems = problems,
                        dragState = dragState,
                        editNode = editNode,
                        onDropLineEnter = onDropLineEnter,
                        onDropLineExit = onDropLineExit,
                        onDragStart = onDragStart
                    )

                    EditorNodeDropTarget(
                        gap = padding,
                        index = index + 1,
                        parentId = nodeDef.id,
                        draggingData = dragState,
                        dropScope = NodeDropScope.CONTENT,
                        onEnter = { onDropLineEnter(index + 1, nodeDef.id) },
                        onExit = onDropLineExit
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(padding))
            }
        }
    }
}