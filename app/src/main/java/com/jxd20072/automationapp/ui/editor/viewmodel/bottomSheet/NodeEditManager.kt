package com.jxd20072.automationapp.ui.editor.viewmodel.bottomSheet

import androidx.compose.runtime.Immutable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Immutable
sealed interface NodeEditState {
	data object NotEditing : NodeEditState
	data class Editing(
		val nodeID: String
	) : NodeEditState
}

@Singleton
class NodeEditManager @Inject constructor() {
	private val _editingNodeState = MutableStateFlow<NodeEditState>(NodeEditState.NotEditing)
	val editingNodeState = _editingNodeState.asStateFlow()

	fun startEditNode(nodeID: String) {
		_editingNodeState.update { NodeEditState.Editing(nodeID) }
	}
}