package com.jxd20072.automationapp.ui.editor.nodeTree

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Draggable2DState
import androidx.compose.foundation.gestures.draggable2D
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
import com.jxd20072.automationapp.core.data.model.ActionNodeDef
import com.jxd20072.automationapp.core.nodes.actionNodes
import com.jxd20072.automationapp.ui.editor.viewmodel.EditorDragNode
import com.jxd20072.automationapp.ui.editor.viewmodel.EditorNodeDragState
import com.jxd20072.automationapp.ui.theme.LocalTheme
import com.jxd20072.automationapp.ui.utils.conditional

enum class TopLevelNodeType { ENTRYPOINT, FUNCTION }

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun NodeHeaderIcons(
	draggableState: Draggable2DState? = null,
	onDragStart: ((startedPosition: Offset) -> Unit)? = null,
	onStopDrag: (() -> Unit)? = null,
	forceShowDragHandle: Boolean = false,
	icon: Int
) {
	var dragHandlePosition by remember { mutableStateOf(Offset(0f, 0f)) }

	val shouldShowDragHandle =
		onDragStart != null && onStopDrag != null && draggableState != null
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
								state = draggableState!!,
								onDragStarted = {
									onDragStart!!(dragHandlePosition + it)
								},
								onDragStopped = {
									onStopDrag!!()
								}
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
	draggableState: Draggable2DState,
	onDragStart: ((startedPosition: Offset) -> Unit)? = null,
	onStopDrag: (() -> Unit)? = null,
	modifier: Modifier = Modifier,
) {
	Row(
		horizontalArrangement = Arrangement.spacedBy(6.dp),
		verticalAlignment = Alignment.CenterVertically,
		modifier = modifier.fillMaxWidth()
	) {
		NodeHeaderIcons(
			draggableState = draggableState,
			onDragStart = onDragStart,
			onStopDrag = onStopDrag,
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
							.background(LocalTheme.colorScheme.surface)
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
	draggableState: Draggable2DState,
	draggingData: EditorNodeDragState,
	onDragStart: (
		id: String,
		nodePosition: Offset,
		pressedPosition: Offset,
	) -> Unit,
	onStopDrag: () -> Unit,
	onDropZoneEnter: (targetIndex: Int, parentId: String?) -> Unit,
	onDropZoneExit: () -> Unit,
	modifier: Modifier = Modifier,
	content: List<ActionNodeDef>
) {
	var dragging by remember { mutableStateOf(false) }
	var rootPosition by remember { mutableStateOf(Offset(0f, 0f)) }

	NodeContainer(
		modifier = modifier,
		level = 1,
		onLayout = { layout ->
			rootPosition = layout.positionInRoot()
		},
		disabledForDragging = dragging,
		ignoreBottomPadding = true
	) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			NodeHeader(
				displayName = displayName,
				nodeId = id,
				icon = if (displayName[0] == "OnStart") R.drawable.round_play_arrow_24
				else R.drawable.round_functions_24,
				draggableState = draggableState,
				onStopDrag = if (type == TopLevelNodeType.ENTRYPOINT) null else ({
					dragging = false
					onStopDrag()
				}),
				onDragStart = if (type == TopLevelNodeType.ENTRYPOINT) null else ({
					dragging = true
					onDragStart(id, rootPosition, it)
				})
			)
			content.forEachIndexed { index, actionNode ->
				EditorNodeDropTarget(
					gap = LocalTheme.layoutSystem.spacing.paddingLevel2,
					draggingData = draggingData,
					dragNode = EditorDragNode.Action::class,
					onEnter = { onDropZoneEnter(index, id) },
					onExit = onDropZoneExit
				)

				ActionNode(
					actionNode = actionNode,
					draggableState = draggableState,
					onStopDrag = onStopDrag,
					onDragStart = onDragStart,
					modifier = Modifier
				)
			}
			EditorNodeDropTarget(
				gap = LocalTheme.layoutSystem.spacing.paddingLevel2,
				draggingData = draggingData,
				dragNode = EditorDragNode.Action::class,
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
	actionNode: ActionNodeDef,
	draggableState: Draggable2DState,
	onStopDrag: (() -> Unit)? = null,
	onDragStart: ((
		id: String,
		position: Offset,
		startedOffset: Offset,
	) -> Unit)? = null,
) {
	var dragging by remember { mutableStateOf(false) }
	var rootPosition by remember { mutableStateOf(Offset(0f, 0f)) }

	val actionData = actionNodes[actionNode.name]?.data
	if (actionData != null) {
		NodeContainer(
			modifier = modifier,
			level = 2,
			onLayout = { layout ->
				rootPosition = layout.positionInRoot()
			},
			disabledForDragging = dragging
		) {
			NodeHeader(
				displayName = actionData.editorActionDescription(actionNode.input),
				nodeId = actionNode.id,
				icon = actionData.icon,
				draggableState = draggableState,
				onStopDrag = {
					onStopDrag?.invoke()
					dragging = false
				},
				onDragStart = {
					onDragStart
						?.invoke(actionNode.id, rootPosition, it)
					dragging = true
				}
			)
		}
	}
}
