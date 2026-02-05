package com.jxd20072.automationapp.ui.editor.viewmodel.managers

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jxd20072.automationapp.core.data.model.ConfigurationValue
import com.jxd20072.automationapp.core.data.model.NodeDef
import com.jxd20072.automationapp.core.data.model.NodeType
import com.jxd20072.automationapp.core.nodes.NodeData
import com.jxd20072.automationapp.core.nodes.actionNodes
import com.jxd20072.automationapp.core.nodes.triggerNodes
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
sealed interface NodeEditState {
    data object Loading : NodeEditState
    data class Success(
        val nodeId: String,
        val nodeDef: NodeDef,
        val nodeData: NodeData,
    ) : NodeEditState
}

@HiltViewModel(assistedFactory = NodeEditViewModel.Factory::class)
class NodeEditViewModel @AssistedInject constructor(
    @Assisted val editingNodeId: String,
    private val automationManager: AutomationManager,
    private val bottomSheetManager: BottomSheetManager
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(editingNodeId: String): NodeEditViewModel
    }

    private val _nodeEditScreenState = MutableStateFlow<NodeEditState>(NodeEditState.Loading)
    val nodeEditScreenState = _nodeEditScreenState.asStateFlow()

    val editingAutomationDraft = automationManager.automationUiState.map { automationUiState ->
        if (automationUiState is AutomationUiState.Success) automationUiState.automationDraft
        else null
    }

    init {
        Log.d("APP_A", "CREATED")
        viewModelScope.launch {
            editingAutomationDraft.collect { automationDraft ->
                if (automationDraft == null) return@collect

                val automationDraft = automationDraft
                val allNodes = automationDraft.topLevel +
                        automationDraft.topLevel.map { it.content }.flatten()
                val nodeDef = allNodes.find { it.id == editingNodeId }
                val nodeData = when (nodeDef?.nodeType) {
                    NodeType.ACTION -> actionNodes[nodeDef.name]?.data
                    NodeType.TRIGGER -> triggerNodes[nodeDef.name]?.data
                    else -> null
                }
                if (nodeDef == null || nodeData == null) return@collect

                _nodeEditScreenState.update {
                    NodeEditState.Success(
                        nodeId = editingNodeId,
                        nodeDef = nodeDef,
                        nodeData = nodeData.copy(
                            inputDefinitions = nodeData.inputDefinitions.filter { (name, inputDef) ->
                                inputDef.shouldShow(nodeDef.input, automationDraft)
                            }
                        )
                    )
                }
            }
        }
    }

    fun resetDefaultConfig() {
        val nodeDef = automationManager.findNode(editingNodeId) ?: return

        automationManager.editNode(
            nodeDef.id, nodeDef.copy(
                input = emptyMap(),
                output = emptyMap()
            )
        )
    }

    fun setNodeConfig(name: String, config: ConfigurationValue<*>) {
        val nodeDef = automationManager.findNode(editingNodeId) ?: return

        automationManager.editNode(
            nodeDef.id, nodeDef.copy(
                input = nodeDef.input + mapOf(name to config)
            )
        )
    }

    fun promptSelectVariable(onSelect: (String) -> Unit) {
        bottomSheetManager.navigateVariables {
            onSelect(it)
            bottomSheetManager.navigateBack()
        }
    }
}