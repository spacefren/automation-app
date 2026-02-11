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
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.EditorNodeDragState
import com.jxd20072.automationapp.ui.editor.viewmodel.utils.EditorProblem
import com.jxd20072.automationapp.ui.theme.LocalTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EditorNodeTree(
    automationDraft: AutomationDef,
    editorProblems: Set<EditorProblem>,
    draggingData: EditorNodeDragState,
    onNodeDrag: (
        id: String,
        startedPosition: Offset,
        startedOffset: Offset,
    ) -> Unit,
    onDropLineEnter: (targetIndex: Int, parentId: String?) -> Unit,
    onDropLineExit: () -> Unit,
    editNode: (nodeId: String) -> Unit,
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


            EditorNodeDropTarget(
                gap = 20.dp,
                draggingData = draggingData,
                dropScope = NodeDropScope.ENTRY,
                onEnter = { onDropLineEnter(index, null) },
                onExit = onDropLineExit
            )

            TopLevelNode(
                editorProblems = editorProblems,
                nodeDef = node,
                automationVariables = automationDraft.variables,
                onDragStart = onNodeDrag,
                draggingData = draggingData,
                onDropZoneEnter = onDropLineEnter,
                onDropZoneExit = onDropLineExit,
                editNode = editNode
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