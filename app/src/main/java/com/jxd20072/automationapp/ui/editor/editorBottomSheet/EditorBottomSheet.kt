package com.jxd20072.automationapp.ui.editor.editorBottomSheet

import androidx.compose.animation.AnimatedContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.jxd20072.automationapp.ui.editor.EditorBottomSheetScreen
import com.jxd20072.automationapp.ui.editor.EditorBottomSheetState
import com.jxd20072.automationapp.ui.shared.ModalBottomSheet
import com.jxd20072.automationapp.ui.shared.ModalBottomSheetState

@Composable
fun EditorBottomSheet(
	onDismiss: () -> Unit,
	onNodeSearch: (String) -> Unit,
	bottomSheetState: EditorBottomSheetState,
	autoOpenKeyboard: Boolean,
) {
	val state = remember { ModalBottomSheetState() }
	state.isOpen = bottomSheetState.isOpen

	ModalBottomSheet(
		state = state,
		onDismissRequest = {
			onDismiss()
			true
		}
	) {
		AnimatedContent(bottomSheetState.currentScreen) { currentScreen ->
			when (currentScreen) {
				EditorBottomSheetScreen.NODE_SELECT -> {
					EditorNodeSelectScreen(
						onNodeSearch = onNodeSearch,
						nodeSearchQuery = bottomSheetState.nodeSearchQuery,
						isSheetOpen = bottomSheetState.isOpen,
						autoOpenKeyboard = autoOpenKeyboard
					)
				}

				else -> {}
			}
		}
	}
}