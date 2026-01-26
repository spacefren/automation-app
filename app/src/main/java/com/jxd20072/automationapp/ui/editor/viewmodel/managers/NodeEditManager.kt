package com.jxd20072.automationapp.ui.editor.viewmodel.managers

import androidx.compose.runtime.Immutable
import com.jxd20072.automationapp.core.data.model.ConfigurationValue
import com.jxd20072.automationapp.core.data.model.NodeDef
import com.jxd20072.automationapp.core.data.model.NodeType
import com.jxd20072.automationapp.core.nodes.NodeData
import com.jxd20072.automationapp.core.nodes.actionNodes
import com.jxd20072.automationapp.core.nodes.triggerNodes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Immutable
sealed interface NodeEditState {
    object NotEditing : NodeEditState
    data class Editing(
        val nodeDef: NodeDef,
        val nodeData: NodeData,
    ) : NodeEditState
}

@Singleton
class NodeEditManager @Inject constructor(
    private val automationManager: AutomationManager
) {
    private val editingNodeID = MutableStateFlow<String?>(null)

    val editingAutomation = automationManager.automationUiState.map { automationUiState ->
        if (automationUiState is AutomationUiState.Success) automationUiState.automationDraft
        else null
    }

    val editingNodeState = combine(
        editingAutomation,
        editingNodeID
    ) { automationDraft, nodeID ->
        if (automationDraft == null) return@combine NodeEditState.NotEditing;

        val allNodes = automationDraft.topLevel +
                automationDraft.topLevel.map { it.content }.flatten()
        val nodeDef = allNodes.find { it.id == nodeID }
        val nodeData = when (nodeDef?.nodeType) {
            NodeType.ACTION -> actionNodes[nodeDef.name]?.data
            NodeType.TRIGGER -> triggerNodes[nodeDef.name]?.data
            else -> null
        }
        if (nodeDef == null || nodeData == null) return@combine NodeEditState.NotEditing
        NodeEditState.Editing(
            nodeDef = nodeDef,
            nodeData = nodeData.copy(
                inputDefinitions = nodeData.inputDefinitions.filter { (name, inputDef) ->
                    inputDef.shouldShow(nodeDef.input, automationDraft)
                }
            )
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