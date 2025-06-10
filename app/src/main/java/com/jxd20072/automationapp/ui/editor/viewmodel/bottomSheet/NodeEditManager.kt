package com.jxd20072.automationapp.ui.editor.viewmodel.bottomSheet

import androidx.compose.runtime.Immutable
import com.jxd20072.automationapp.core.data.model.ConfigurationEntry
import com.jxd20072.automationapp.core.nodes.actionNodes
import com.jxd20072.automationapp.ui.editor.viewmodel.AutomationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Immutable
sealed interface NodeEditState {
	data object NotEditing : NodeEditState
	data class Editing(
		val nodeID: String,
	) : NodeEditState
}

@Singleton
class NodeEditManager @Inject constructor(
	private val automationManager: AutomationManager
) {
	private val _editingNodeState = MutableStateFlow<NodeEditState>(NodeEditState.NotEditing)
	val editingNodeState = _editingNodeState.asStateFlow()

	fun startEditNode(nodeID: String) {
		_editingNodeState.update {
			NodeEditState.Editing(
				nodeID = nodeID
			)
		}
	}

	fun resetDefaultConfig() {
		val state = editingNodeState.value
		if (state !is NodeEditState.Editing) return

		val actionNode = automationManager.findAction(state.nodeID) ?: return
		val actionData = actionNodes[actionNode.name]?.data ?: return

		automationManager.editAction(
			actionNode.id, actionNode.copy(
			input = emptyMap()
		))
	}

	fun setConfig(name: String, config: ConfigurationEntry<*>) {
		val state = editingNodeState.value
		if (state !is NodeEditState.Editing) return

		val actionNode = automationManager.findAction(state.nodeID) ?: return
		val actionData = actionNodes[actionNode.name]?.data ?: return

		automationManager.editAction(
			actionNode.id, actionNode.copy(
				input = actionNode.input + mapOf(name to config)
			)
		)
	}
}