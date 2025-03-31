package com.jxd20072.automationapp.ui.editor

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberDraggable2DState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.ui.editor.editorBottomSheet.EditorBottomSheet
import com.jxd20072.automationapp.ui.editor.nodeTree.DraggingNode
import com.jxd20072.automationapp.ui.editor.nodeTree.EditorNodeTree
import com.jxd20072.automationapp.ui.shared.Layer
import com.jxd20072.automationapp.ui.shared.LocalSnackbarState
import com.jxd20072.automationapp.ui.shared.SnackbarActionData
import com.jxd20072.automationapp.ui.shared.SnackbarData
import com.jxd20072.automationapp.ui.shared.SnackbarType
import com.jxd20072.automationapp.ui.theme.LocalTheme
import com.jxd20072.automationapp.ui.userSettings.UserSettingsUiState
import com.jxd20072.automationapp.ui.userSettings.UserSettingsViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AutomationEditor(
	editorViewModel: EditorViewModel = hiltViewModel(),
	settingsViewModel: UserSettingsViewModel = hiltViewModel(),
	returnHome: (saveChanges: Boolean) -> Unit
) {
	val localSnackbarState = LocalSnackbarState.current
	val currentTime by editorViewModel.currentTime.collectAsState()
	val editorUiState by editorViewModel.editorUiState.collectAsState()
	val settingsUiState by settingsViewModel.userSettingsUiState.collectAsState()

	if (editorUiState is EditorUiState.Success) {
		val automationDraft = (editorUiState as EditorUiState.Success).automationDraft
		val automationData = (editorUiState as EditorUiState.Success).automationData
		val draggingData = (editorUiState as EditorUiState.Success).draggingData
		val bottomSheetState = (editorUiState as EditorUiState.Success).bottomSheetState

		val draggableState = rememberDraggable2DState { delta ->
			editorViewModel.changeDraggingPosition(delta)
		}

		Layer(
			modifier = Modifier
				.fillMaxSize()
				.background(LocalTheme.colorScheme.background)
		) {
			Column {
				EditorTopBar(
					automationData = automationData,
					currentTime = currentTime,
					returnHome = returnHome,
					saveAutomation = { editorViewModel.trySaveAutomation(true) },
					runAutomation = {
						localSnackbarState.showSnackbar(
							SnackbarData(
							text = "This is a test",
							type = SnackbarType.INFO,
							icon = R.drawable.round_play_arrow_24,
							action = SnackbarActionData(
								name = "This is a button",
								onClick = { }
							)
						))
					},
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
		DraggingNode(
			draggingData = draggingData
		)
		EditorFloatingAddButton(
			onCreateNodePressed = editorViewModel::startNodeCreationFlow,
			onCreateFunctionPressed = { }
		)
		EditorBottomSheet(
			onDismiss = editorViewModel::onBottomSheetDismissed,
			bottomSheetState = bottomSheetState,
			onNodeSearch = editorViewModel::changeNodeSearchQuery,
			isDragging = draggingData is EditorNodeDragData.Dragging,
			onStartDrag = editorViewModel::onNodeCreateDrag,
			onStopDrag = editorViewModel::onStopNodeCreateDrag,
			draggableState = draggableState,
			autoOpenKeyboard = settingsUiState is UserSettingsUiState.Success
				&& (settingsUiState as UserSettingsUiState.Success).settings.autoOpenKeyboard.value,
		)
	}
}