package com.jxd20072.automationapp.ui.editor.viewmodel.managers

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jxd20072.automationapp.core.data.model.ConfigurationValue
import com.jxd20072.automationapp.core.data.model.NodeDef
import com.jxd20072.automationapp.core.data.repository.PermissionsRepository
import com.jxd20072.automationapp.core.nodes.NodeData
import com.jxd20072.automationapp.ui.editor.viewmodel.utils.EditorProblem
import com.jxd20072.automationapp.utils.getNodeData
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

@Immutable
sealed interface NodeEditState {
    data object Loading : NodeEditState
    data class Success(
        val nodeId: String,
        val nodeDef: NodeDef,
        val nodeData: NodeData,
        val nodeProblems: Set<EditorProblem>,
    ) : NodeEditState
}

@HiltViewModel(assistedFactory = NodeEditViewModel.Factory::class)
class NodeEditViewModel @AssistedInject constructor(
    @Assisted val editorSession: EditorSession,
    @Assisted val editingNodeId: String,
    private val permissionsRepository: PermissionsRepository,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(editingNodeId: String, editorSession: EditorSession): NodeEditViewModel
    }

    private val _nodeEditScreenState = MutableStateFlow<NodeEditState>(NodeEditState.Loading)
    val nodeEditScreenState = _nodeEditScreenState.asStateFlow()

    val automationUiState = editorSession.automationUiState.map { automationUiState ->
        automationUiState as? AutomationUiState.Success
    }

    init {
        viewModelScope.launch {
            automationUiState.collect { automationUiState ->
                if (automationUiState !is AutomationUiState.Success) return@collect

                val automationDraft = automationUiState.automationDraft

                val nodeDef = editorSession.findNode(editingNodeId)
                val nodeData = if (nodeDef != null) getNodeData(nodeDef) else null
                if (nodeDef == null || nodeData == null) return@collect

                _nodeEditScreenState.update {
                    NodeEditState.Success(
                        nodeId = editingNodeId,
                        nodeDef = nodeDef,
                        nodeData = nodeData.copy(
                            inputDefinitions = nodeData.inputDefinitions.filter { (name, inputDef) ->
                                inputDef.shouldShow(nodeDef.input, automationDraft)
                            }
                        ),
                        nodeProblems = automationUiState.problems
                            .filter {
                                if (it is EditorProblem.NodeProblem) return@filter it.nodeId == nodeDef.id
                                if (it is EditorProblem.NodeInputProblem) return@filter it.nodeId == nodeDef.id
                                if (it is EditorProblem.MissingPermission) return@filter it.nodeId == nodeDef.id
                                false
                            }
                            .toSet()
                    )
                }
            }
        }
    }

    fun resetDefaultConfig() {
        val nodeDef = editorSession.getNode(editingNodeId) ?: return

        editorSession.editNode(
            nodeDef.id, nodeDef.copy(
                input = emptyMap(),
                output = emptyMap()
            )
        )
    }

    fun setNodeConfig(name: String, config: ConfigurationValue<*>) {
        val nodeDef = editorSession.getNode(editingNodeId) ?: return

        editorSession.editNode(
            nodeDef.id, nodeDef.copy(
                input = nodeDef.input + mapOf(name to config)
            )
        )
    }

    fun promptSelectVariable(
        variableType: KClass<ConfigurationValue<*>>?,
        onSelect: (String) -> Unit,
    ) {
        editorSession.bottomSheetManager.navigateVariables(
            if (variableType != null) setOf(variableType)
            else emptySet()
        ) {
            onSelect(it)
            editorSession.bottomSheetManager.navigateBack()
        }
    }

    fun onPermissionGranted() {
        editorSession.onPermissionUpdate()
    }
}