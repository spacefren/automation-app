package com.jxd20072.automationapp.ui.editor.viewmodel.bottomSheet

import androidx.compose.runtime.Immutable
import com.jxd20072.automationapp.core.data.model.ActionNodeDef
import com.jxd20072.automationapp.core.nodes.actionNodes
import com.jxd20072.automationapp.core.nodes.actions.ActionNodeData
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
		val actionNode: ActionNodeDef,
		val actionData: ActionNodeData
	) : NodeEditState
}

@Singleton
class NodeEditManager @Inject constructor(
	private val automationManager: AutomationManager
) {
	private val _editingNodeState = MutableStateFlow<NodeEditState>(NodeEditState.NotEditing)
	val editingNodeState = _editingNodeState.asStateFlow()

	fun startEditNode(nodeID: String) {
		val actionNode = automationManager.findAction(nodeID) ?: return
		val actionData = actionNodes[actionNode.name]?.data ?: return

		_editingNodeState.update {
			NodeEditState.Editing(
				actionNode = actionNode,
				actionData = actionData
			)
		}
	}

	fun resetDefaultConfig() {
		val state = editingNodeState.value
		if (state !is NodeEditState.Editing) return

		automationManager.editAction(
			state.actionNode.id, state.actionNode.copy(
			input = state.actionData.inputDefinitions.mapValues { it.value.defaultValue }
		))
	}
}