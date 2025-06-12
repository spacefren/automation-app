package com.jxd20072.automationapp.ui.editor.viewmodel.bottomSheet

import androidx.compose.runtime.Immutable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

enum class EditorBottomSheetScreen {
	EMPTY, NODE_SELECT, NODE_EDIT
}

@Immutable
sealed interface EditorBottomSheetState {
	data class Open(
		val screen: EditorBottomSheetScreen,
	) : EditorBottomSheetState

	data object Closed : EditorBottomSheetState
}

@Singleton
class BottomSheetManager @Inject constructor(
	val nodeSelectManager: NodeSelectManager,
	val nodeEditManager: NodeEditManager,
) {
	private val _bottomSheetState = MutableStateFlow<EditorBottomSheetState>(
		EditorBottomSheetState.Closed
	)
	val bottomSheetState = _bottomSheetState.asStateFlow()

	fun closeBottomSheet() {
		_bottomSheetState.update { EditorBottomSheetState.Closed }
	}

	fun openEmpty() {
		_bottomSheetState.update {
			EditorBottomSheetState.Open(
				screen = EditorBottomSheetScreen.EMPTY
			)
		}
	}

	fun openNodeSelect(nodeSelectType: NodeSelectType) {
		nodeSelectManager.loadState(nodeSelectType)
		_bottomSheetState.update {
			EditorBottomSheetState.Open(
				screen = EditorBottomSheetScreen.NODE_SELECT
			)
		}
	}

	fun openNodeEdit(nodeID: String) {
		nodeEditManager.startEditNode(nodeID)
		_bottomSheetState.update {
			EditorBottomSheetState.Open(
				screen = EditorBottomSheetScreen.NODE_EDIT
			)
		}
	}
}