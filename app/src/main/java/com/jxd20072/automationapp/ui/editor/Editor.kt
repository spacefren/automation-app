package com.jxd20072.automationapp.ui.editor

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberDraggable2DState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.jxd20072.automationapp.ui.editor.nodeTree.DraggingNode
import com.jxd20072.automationapp.ui.editor.nodeTree.EditorNodeTree
import com.jxd20072.automationapp.ui.shared.Layer

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AutomationEditor(
	editorViewModel: EditorViewModel = hiltViewModel(),
	returnHome: (saveChanges: Boolean) -> Unit
) {
	val editorUiState by editorViewModel.editorUiState.collectAsState()
	val currentTime by editorViewModel.currentTime.collectAsState()

	if (editorUiState is EditorUiState.Success) {
		val automationDraft = (editorUiState as EditorUiState.Success).automationDraft
		val automationData = (editorUiState as EditorUiState.Success).automationData
		val draggingData = (editorUiState as EditorUiState.Success).draggingData

		val draggableState = rememberDraggable2DState { delta ->
			editorViewModel.changeDraggingPosition(delta)
		}

		Layer(
			modifier = Modifier
				.fillMaxSize()
				.background(MaterialTheme.colorScheme.background)
		) {
			DraggingNode(
				draggingData = draggingData
			)
			Column {
				EditorTopBar(
					automationData = automationData,
					currentTime = currentTime,
					returnHome = returnHome,
					saveAutomation = { },
					runAutomation = { },
				)
				EditorNodeTree(
					automationDraft = automationDraft,
					onNodeDrag = editorViewModel::onNodeDrag,
					onStopDrag = editorViewModel::onStopDrag,
					draggableState = draggableState,
					draggingData = draggingData,
					onDropZoneEnter = editorViewModel::onNodeDropZoneEnter,
					onDropZoneExit = editorViewModel::onNodeDropZoneExit
				)
			}
		}
	}
}