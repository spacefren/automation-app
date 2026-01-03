package com.jxd20072.automationapp.ui.editor.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.jxd20072.automationapp.core.data.model.AutomationDef
import com.jxd20072.automationapp.core.data.model.NodeType
import com.jxd20072.automationapp.core.nodes.NodeData
import com.jxd20072.automationapp.core.nodes.triggerNodes
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.EditorNodeDragState
import com.jxd20072.automationapp.ui.theme.LocalTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EditorNodeTree(
    automationDraft: AutomationDef,
    draggingData: EditorNodeDragState,
    onNodeDrag: (
        id: String,
        startedPosition: Offset,
        startedOffset: Offset,
    ) -> Unit,
    onDropLineEnter: (targetIndex: Int, parentId: String?) -> Unit,
    onDropLineExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(horizontal = LocalTheme.layoutSystem.spacing.paddingLevel1)
    ) {
        itemsIndexed(
            automationDraft.topLevel,
            key = { _, item -> item.id }
        ) { index, node ->
            var triggerData: NodeData? = null
            if (node.nodeType == NodeType.TRIGGER) {
                triggerData = triggerNodes[node.name]?.data ?: return@itemsIndexed
            }

            EditorNodeDropTarget(
                gap = 20.dp,
                draggingData = draggingData,
                dropScope = NodeDropScope.ENTRY,
                onEnter = { onDropLineEnter(index, null) },
                onExit = onDropLineExit
            )

            TopLevelNode(
                type = when {
                    node.nodeType == NodeType.TRIGGER -> TopLevelNodeType.TRIGGER
                    node.nodeType == NodeType.FUNCTION && node.id == "0" -> TopLevelNodeType.ENTRYPOINT
                    else -> TopLevelNodeType.FUNCTION
                },
                id = node.id,
                displayName = when (node.nodeType) {
                    NodeType.TRIGGER -> triggerData!!.editorActionDescription(
                        triggerData.inputDefinitions.mapValues { it.value.defaultValue } + node.input
                    )

                    else -> listOf(node.name)
                },
                content = node.content,
                onDragStart = onNodeDrag,
                draggingData = draggingData,
                onDropZoneEnter = onDropLineEnter,
                onDropZoneExit = onDropLineExit
            )
        }

        item {
            EditorNodeDropTarget(
                gap = LocalTheme.layoutSystem.spacing.paddingLevel1,
                draggingData = draggingData,
                dropScope = NodeDropScope.ENTRY,
                onEnter = { onDropLineEnter(automationDraft.topLevel.size, null) },
                onExit = onDropLineExit
            )
        }

        item { Spacer(modifier = Modifier.height(50.dp)) }
    }
}