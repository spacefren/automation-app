package com.jxd20072.automationapp.ui.editor.viewmodel.bottomSheet

import androidx.compose.runtime.Immutable
import com.jxd20072.automationapp.core.data.model.ConfigurationValue
import com.jxd20072.automationapp.core.data.model.NodeDef
import com.jxd20072.automationapp.core.data.model.NodeType
import com.jxd20072.automationapp.core.nodes.NodeData
import com.jxd20072.automationapp.core.nodes.actionNodes
import com.jxd20072.automationapp.core.nodes.triggerNodes
import com.jxd20072.automationapp.ui.editor.viewmodel.AutomationManager
import com.jxd20072.automationapp.ui.editor.viewmodel.AutomationUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Immutable
data class NodeEditState(
	val nodeDef: NodeDef,
	val nodeData: NodeData,
)

@Singleton
class NodeEditManager @Inject constructor(
	private val automationManager: AutomationManager
) {
	private val editingNodeID = MutableStateFlow<String?>(null)
	val editingNodeState = combine(
		automationManager.automationUiState.map { automationState ->
			if (automationState !is AutomationUiState.Success) null
			else automationState.automationDraft.topLevel +
				automationState.automationDraft.topLevel.map { it.content }.flatten()
		},
		editingNodeID
	) { nodes, nodeID ->
		val nodeDef = nodes?.find { it.id == nodeID }
		val nodeData = when (nodeDef?.nodeType) {
			NodeType.ACTION -> actionNodes[nodeDef.name]?.data
			NodeType.TRIGGER -> triggerNodes[nodeDef.name]?.data
			else -> null
		}
		if (nodeDef == null || nodeData == null) return@combine null
		NodeEditState(
			nodeDef = nodeDef,
			nodeData = nodeData
		)
	}

	fun startEditNode(nodeID: String) {
		editingNodeID.value = nodeID
	}

	fun resetDefaultConfig() {
		val nodeID = editingNodeID.value ?: return
		val nodeDef = automationManager.findNode(nodeID) ?: return

		automationManager.editNode(
			nodeDef.id, nodeDef.copy(
				input = emptyMap(),
				output = emptyMap()
			)
		)
	}

	fun setConfig(name: String, config: ConfigurationValue<*>) {
		val nodeID = editingNodeID.value ?: return
		val nodeDef = automationManager.findNode(nodeID) ?: return

		automationManager.editNode(
			nodeDef.id, nodeDef.copy(
				input = nodeDef.input + mapOf(name to config)
			)
		)
	}
}