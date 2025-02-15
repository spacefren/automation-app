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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.compose.ui.unit.sp
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.core.data.repository.automations.ActionNodeDraft
import com.jxd20072.automationapp.core.nodes.actionNodes
import com.jxd20072.automationapp.ui.editor.EditorDraggableScope
import com.jxd20072.automationapp.ui.editor.EditorNodeDragData
import com.jxd20072.automationapp.ui.utils.conditional

enum class TopLevelNodeType { ENTRYPOINT, TRIGGER, FUNCTION }
enum class ChildNodeType { ACTION }

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
					tint = MaterialTheme.colorScheme.onSurface,
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
			tint = MaterialTheme.colorScheme.onBackground,
			contentDescription = "dragIndicator",
			modifier = Modifier
				.size(26.dp)
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
							.shadow(5.dp, RoundedCornerShape(5.dp))
							.background(MaterialTheme.colorScheme.surface)
							.padding(horizontal = 6.dp, vertical = 3.dp)
					) {
						Text(
							text = piece.removePrefix("*"),
							color = MaterialTheme.colorScheme.onBackground,
							fontStyle = FontStyle.Italic,
							fontSize = 16.sp,
						)
					}
				} else {
					piece.split(" ").forEach { word ->
						Text(
							text = word,
							color = MaterialTheme.colorScheme.onBackground,
							fontSize = 16.sp,
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
	val paddingLevels = listOf(10.dp, 8.dp)

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
				if (enabledByDragging == true) 10.dp else 5.dp,
				RoundedCornerShape(
					when (level) {
						1 -> 8.dp
						2 -> 4.dp
						else -> 5.dp
					}
				)
			)
			.background(
				when (level) {
					1 -> MaterialTheme.colorScheme.surface
					2 -> MaterialTheme.colorScheme.tertiary
					else -> Color.Red
				}
			)
			.onGloballyPositioned { layout ->
				onLayout(layout)
			}
			.conditional(!ignoreBottomPadding, ifTrue = {
				padding(paddingLevels[level - 1])
			}, ifFalse = {
				val padding = paddingLevels[level - 1]
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
	draggingData: EditorNodeDragData,
	onDragStart: (
		id: String,
		nodePosition: Offset,
		pressedPosition: Offset,
		scope: EditorDraggableScope
	) -> Unit,
	onStopDrag: () -> Unit,
	onDropZoneEnter: (targetIndex: Int, parentId: String?) -> Unit,
	onDropZoneExit: () -> Unit,
	modifier: Modifier = Modifier,
	content: List<ActionNodeDraft>
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
					onDragStart(id, rootPosition, it, EditorDraggableScope.FUNCTION)
				})
			)
			content.forEachIndexed { index, actionNode ->
				EditorNodeDropTarget(
					gap = 10.dp,
					draggingData = draggingData,
					scope = EditorDraggableScope.ACTION,
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
				gap = 10.dp,
				draggingData = draggingData,
				scope = EditorDraggableScope.ACTION,
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
	actionNode: ActionNodeDraft,
	draggableState: Draggable2DState,
	onStopDrag: (() -> Unit)? = null,
	onDragStart: ((
		id: String,
		position: Offset,
		startedOffset: Offset,
		scope: EditorDraggableScope
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
						?.invoke(actionNode.id, rootPosition, it, EditorDraggableScope.ACTION)
					dragging = true
				}
			)
		}
	}
}
