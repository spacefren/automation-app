package com.jxd20072.automationapp.ui.editor.viewmodel.bottomSheet

import androidx.compose.runtime.Immutable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Immutable
sealed interface EditorBottomSheetCollapsedState {
	data object Empty : EditorBottomSheetCollapsedState
	data class DropPanels(
		val showDelete: Boolean = false,
	) : EditorBottomSheetCollapsedState
}

@Singleton
class CollapsedSheetManager @Inject constructor() {
	private val _collapsedScreenState = MutableStateFlow<EditorBottomSheetCollapsedState>(
		EditorBottomSheetCollapsedState.Empty
	)
	val collapsedScreenState = _collapsedScreenState.asStateFlow()

	fun hideCollapsedScreen() {
		_collapsedScreenState.update { EditorBottomSheetCollapsedState.Empty }
	}

	fun showDropPanels(showDelete: Boolean) {
		_collapsedScreenState.update {
			EditorBottomSheetCollapsedState.DropPanels(
				showDelete = showDelete
			)
		}
	}
}