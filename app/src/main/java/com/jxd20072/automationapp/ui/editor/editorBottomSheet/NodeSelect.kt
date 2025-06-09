package com.jxd20072.automationapp.ui.editor.editorBottomSheet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Draggable2DState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.core.nodes.actions.ActionNodeData
import com.jxd20072.automationapp.ui.editor.nodeTree.NodeHeaderIcons
import com.jxd20072.automationapp.ui.editor.viewmodel.bottomSheet.CategoryListEntry
import com.jxd20072.automationapp.ui.shared.SearchBar
import com.jxd20072.automationapp.ui.shared.clickableWithIndication
import com.jxd20072.automationapp.ui.theme.LocalTheme
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.truncate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NodeCard(
	nodeData: ActionNodeData,
	draggableState: Draggable2DState,
	modifier: Modifier = Modifier,
	onDragStart: (startedPosition: Offset, startedOffset: Offset) -> Unit,
	onStopDrag: () -> Unit,
	onClick: (nodeData: ActionNodeData) -> Unit,
) {
	var rootPosition by remember { mutableStateOf(Offset(0f, 0f)) }

	Column(
		verticalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel2),
		modifier = modifier
			.clickableWithIndication { onClick(nodeData) }
			.shadow(
				LocalTheme.layoutSystem.shadows.secondary,
				RoundedCornerShape(LocalTheme.layoutSystem.shapes.cornerRadiusLevel2)
			)
			.background(LocalTheme.colorScheme.tertiary)
			.padding(LocalTheme.layoutSystem.spacing.paddingLevel2)
	) {
		Row(
			horizontalArrangement = Arrangement.SpaceBetween,
			modifier = Modifier
				.fillMaxWidth()
				.onGloballyPositioned {
					rootPosition = it.positionInRoot()
				}
		) {
			NodeHeaderIcons(
				draggableState = draggableState,
				onDragStart = {
					onDragStart(rootPosition, it)
				},
				onStopDrag = onStopDrag,
				icon = nodeData.icon
			)
			Image(
				painter = painterResource(R.drawable.round_info_24),
				contentDescription = "help",
				colorFilter = ColorFilter.tint(LocalTheme.colorScheme.onSurface),
				modifier = Modifier
					.size(18.dp)
					.clickable {}
			)
		}
		Text(
			text = nodeData.displayName,
			color = LocalTheme.colorScheme.onBackground,
			style = LocalTheme.typography.header,
			maxLines = 2,
		)
	}
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun NodeCategoryContainer(
	categoryListEntry: CategoryListEntry,
	draggableState: Draggable2DState,
	onStartDrag: (
		nodeData: ActionNodeData,
		startedPosition: Offset,
		startedOffset: Offset,
	) -> Unit,
	onStopDrag: () -> Unit,
	onClick: (nodeData: ActionNodeData) -> Unit,
	modifier: Modifier = Modifier
) {
	var expanded by remember { mutableStateOf(false) }
	val dropdownRotation by animateFloatAsState(if (expanded) 180f else 270f)

	val elementSeparation = LocalTheme.layoutSystem.spacing.paddingLevel2.value
	val availableWidth = LocalWindowInfo.current.containerSize.width -
		2 * LocalTheme.layoutSystem.spacing.paddingLevel1.value -
		2 * elementSeparation
	val elementSize = 90
	val elementCount = categoryListEntry.nodes.size

	val columns = min(
		elementCount,
		truncate((availableWidth + elementSeparation) / (elementSize + elementSeparation)).toInt()
	)
	val rows = ceil(elementCount.toFloat() / columns.toFloat()).toInt()
	val verticalSpace = rows * (elementSize + elementSeparation) - elementSeparation

	Column(
		modifier = modifier
			.clickableWithIndication { expanded = !expanded }
			.shadow(
				LocalTheme.layoutSystem.shadows.primary,
				RoundedCornerShape(LocalTheme.layoutSystem.shapes.cornerRadiusLevel1)
			)
			.fillMaxWidth()
			.background(LocalTheme.colorScheme.surface)
			.padding(elementSeparation.dp)
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier
				.fillMaxWidth()
		) {
			Image(
				painter = painterResource(R.drawable.round_arrow_dropdown_24),
				contentDescription = "dropdown icon",
				colorFilter = ColorFilter.tint(LocalTheme.colorScheme.onBackground),
				modifier = Modifier
					.size(20.dp)
					.rotate(dropdownRotation)
			)
			Spacer(modifier = Modifier.width(5.dp))
			Text(
				text = categoryListEntry.categoryName.replaceFirstChar { it.uppercase() },
				style = LocalTheme.typography.header,
				color = LocalTheme.colorScheme.onBackground
			)
			Spacer(modifier = Modifier.weight(1f))
			Image(
				painter = painterResource(R.drawable.round_bug_24),
				contentDescription = "category icon",
				colorFilter = ColorFilter.tint(LocalTheme.colorScheme.onBackground),
				modifier = Modifier.size(22.dp)
			)
		}
		AnimatedVisibility(
			visible = expanded,
			modifier = Modifier.fillMaxWidth()
		) {
			FlowRow(
				horizontalArrangement = Arrangement.spacedBy(elementSeparation.dp),
				verticalArrangement = Arrangement.spacedBy(elementSeparation.dp),
				modifier = Modifier
					.fillMaxWidth()
					.padding(top = elementSeparation.dp)
			) {
				categoryListEntry.nodes.forEach { node ->
					NodeCard(
						nodeData = node.data,
						draggableState = draggableState,
						onDragStart = { startedPosition, offset ->
							onStartDrag(node.data, startedPosition, offset)
						},
						onStopDrag = onStopDrag,
						onClick = onClick,
						modifier = Modifier
							.weight(1f)
							.fillMaxRowHeight(1f)
					)
				}
			}
		}
	}
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EditorNodeSelectScreen(
	nodeSearchQuery: String,
	autoOpenKeyboard: Boolean,
	isCollapsed: Boolean,
	onStartDrag: (
		nodeData: ActionNodeData,
		startedPosition: Offset,
		startedOffset: Offset,
	) -> Unit,
	onStopDrag: () -> Unit,
	onClick: (nodeData: ActionNodeData) -> Unit,
	draggableState: Draggable2DState,
	nodes: List<CategoryListEntry>,
	onNodeSearch: (query: String) -> Unit
) {
	val screenHeight = LocalWindowInfo.current.containerSize.height
	val screenHeightDp = with(LocalDensity.current) { screenHeight.toDp() }
	val maxHeight by animateDpAsState(
		if (isCollapsed) 0.dp else screenHeightDp,
		spring(stiffness = Spring.StiffnessMediumLow)
	)

	Box(
		modifier = Modifier.heightIn(max = maxHeight)
	) {
		Column(
			verticalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel1),
			modifier = Modifier
				.imePadding()
				.padding(LocalTheme.layoutSystem.spacing.paddingLevel1)
		) {
			Text(
				text = "Select action",
				color = LocalTheme.colorScheme.onBackground,
				style = LocalTheme.typography.subTitle
			)
			SearchBar(
				searchText = "Search actions or categories",
				queryText = nodeSearchQuery,
				focused = autoOpenKeyboard
			) { query ->
				onNodeSearch(query)
			}
			LazyColumn(
				verticalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel2),
				modifier = Modifier
					.fillMaxSize()
			) {
				items(nodes) {
					NodeCategoryContainer(
						categoryListEntry = it,
						onStartDrag = onStartDrag,
						onStopDrag = onStopDrag,
						draggableState = draggableState,
						onClick = onClick,
						modifier = Modifier.animateItem()
					)
				}
			}
		}
	}
}