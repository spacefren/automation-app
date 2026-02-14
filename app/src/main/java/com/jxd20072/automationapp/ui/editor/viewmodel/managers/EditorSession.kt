package com.jxd20072.automationapp.ui.editor.viewmodel.managers

import androidx.compose.runtime.Immutable
import com.jxd20072.automationapp.core.data.AutomationLogEntry
import com.jxd20072.automationapp.core.data.AutomationsMapEntry
import com.jxd20072.automationapp.core.data.model.AutomationDef
import com.jxd20072.automationapp.core.data.model.NodeDef
import com.jxd20072.automationapp.core.data.model.NodeType
import com.jxd20072.automationapp.core.data.model.Value
import com.jxd20072.automationapp.core.data.model.VariableDef
import com.jxd20072.automationapp.core.data.repository.AppSettingsRepository
import com.jxd20072.automationapp.core.data.repository.AutomationsRepository
import com.jxd20072.automationapp.core.data.repository.PermissionsRepository
import com.jxd20072.automationapp.core.executor.AutomationEngine
import com.jxd20072.automationapp.core.executor.AutomationExecutor
import com.jxd20072.automationapp.core.nodes.NodeData
import com.jxd20072.automationapp.ui.editor.viewmodel.utils.EditorProblem
import com.jxd20072.automationapp.ui.editor.viewmodel.utils.getProblems
import com.jxd20072.automationapp.utils.TimeManager
import com.jxd20072.automationapp.utils.findAndReplaceRecursive
import com.jxd20072.automationapp.utils.findAndTransformRecursive
import com.jxd20072.automationapp.utils.insertAt
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
sealed interface AutomationUiState {
    data object Loading : AutomationUiState
    data class Success(
        val automationId: String,
        val automationData: AutomationsMapEntry,
        val automationDraft: AutomationDef,
        val problems: Set<EditorProblem>
    ) : AutomationUiState
}

class EditorSession @AssistedInject constructor(
    @Assisted private val automationId: String,
    @Assisted private val sessionScope: CoroutineScope,
    private val automationsRepository: AutomationsRepository,
    private val settingsRepository: AppSettingsRepository,
    private val permissionsRepository: PermissionsRepository,
    private val automationEngine: AutomationEngine,
) {
    @AssistedFactory
    interface Factory {
        fun create(automationId: String, sessionScope: CoroutineScope): EditorSession
    }

    val dragManager = DragManager(this)
    val bottomSheetManager = BottomSheetManager()
    private val timeManager = TimeManager(sessionScope, 10000L)

    val currentTime = timeManager.currentTime

    private val _automationUiState = MutableStateFlow<AutomationUiState>(AutomationUiState.Loading)
    val automationUiState = _automationUiState.asStateFlow()

    init {
        loadAutomation(automationId)
    }

    fun loadAutomation(id: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val automation = automationsRepository.getAutomationDef(id)
            val automationData = automationsRepository.getAutomationMapEntry(id)
            if (automation != null && automationData != null) {
                _automationUiState.update {
                    AutomationUiState.Success(
                        automationId = id,
                        automationData = automationData,
                        automationDraft = automation.copy(),
                        problems = getProblems(
                            automation,
                            permissionsRepository.getAllPermissions()
                        )
                    )
                }
            }
        }
    }

    suspend fun observeFileChanges() {
        automationsRepository.automationsDataFlow.collect { automationData ->
            _automationUiState.update { uiState ->
                if (uiState !is AutomationUiState.Success) return@update uiState
                val newData =
                    automationData.automationsMap[uiState.automationId] ?: return@update uiState
                uiState.copy(automationData = newData)
            }
        }
    }

    @OptIn(FlowPreview::class)
    suspend fun observeDraftChanges() {
        automationUiState
            .map { if (it is AutomationUiState.Success) it.automationDraft else null }
            .distinctUntilChanged()
            .debounce(1000)
            .collect { trySaveAutomation() }
    }

    @OptIn(FlowPreview::class)
    suspend fun observeProblems() {
        automationUiState
            .map { if (it is AutomationUiState.Success) it.automationDraft else null }
            .distinctUntilChanged()
            .debounce(200)
            .collect { draft ->
                if (draft == null) return@collect
                _automationUiState.update { previousState ->
                    (previousState as AutomationUiState.Success).copy(
                        problems = getProblems(draft, permissionsRepository.getAllPermissions())
                    )
                }
            }
    }

    fun onPermissionUpdate() {
        _automationUiState.update { state ->
            if (state !is AutomationUiState.Success) return@update state
            state.copy(
                problems = getProblems(
                    state.automationDraft,
                    permissionsRepository.getAllPermissions()
                )
            )
        }
    }

    suspend fun executeAutomation(): AutomationExecutor? {
        val uiState = automationUiState.value
        return if (uiState is AutomationUiState.Success) {
            automationEngine.executeAutomation(uiState.automationId)
        } else null
    }

    fun trySaveAutomation(force: Boolean = false) {
        CoroutineScope(Dispatchers.Main).launch {
            if (force || settingsRepository.getSettings().autoSave) {
                val uiState = automationUiState.value
                if (uiState is AutomationUiState.Success) {
                    automationsRepository.writeAutomation(
                        uiState.automationId,
                        uiState.automationDraft
                    )
                }
            }
        }
    }

    fun getNode(id: String): NodeDef? {
        return findNodeWithDepth(id)?.first
    }

    fun findNodeWithDepth(
        initialTree: List<NodeDef>? = null,
        search: (nodeDef: NodeDef) -> Boolean,
    ): Pair<NodeDef, Int>? {
        val tree = initialTree ?: (automationUiState.value as? AutomationUiState.Success)
            ?.automationDraft?.topLevel
        ?: emptyList()

        return tree.firstOrNull(search)?.let { Pair(it, 0) }
            ?: tree.flatMap { it.content }
                .takeIf { it.isNotEmpty() }
                ?.let { children ->
                    findNodeWithDepth(children, search)?.let { (node, depth) ->
                        Pair(node, depth + 1)
                    }
                }
    }

    fun findNodeWithDepth(id: String, initialTree: List<NodeDef>? = null): Pair<NodeDef, Int>? {
        return findNodeWithDepth(initialTree) { it.id == id }
    }

    fun findNode(id: String, initialTree: List<NodeDef>? = null): NodeDef? {
        return findNodeWithDepth(initialTree) { it.id == id }?.first
    }

    fun createNodeDef(nodeData: NodeData): NodeDef {
        return NodeDef(
            name = nodeData.name,
            nodeType = nodeData.nodeType,
            input = emptyMap(),
            output = emptyMap(),
            content = emptyList()
        )
    }

    fun addNode(node: NodeDef, parentID: String? = "0", index: Int? = null) {
        _automationUiState.update { state ->
            if (state !is AutomationUiState.Success) return@update state

            when (node.nodeType) {
                NodeType.ACTION -> state.copy(
                    automationDraft = state.automationDraft.copy(
                        topLevel = state.automationDraft.topLevel
                            .findAndTransformRecursive(
                                { it.id == parentID },
                                { parent ->
                                    parent.copy(content = parent.content.insertAt(index, node))
                                },
                                { it.content },
                                setContents = { node, contents -> node.copy(content = contents) }
                            )
                    )
                )

                NodeType.FUNCTION,
                NodeType.TRIGGER -> state.copy(
                    automationDraft = state.automationDraft.copy(
                        topLevel = state.automationDraft.topLevel.insertAt(index, node)
                    )
                )
            }
        }
    }

    fun deleteNode(nodeID: String) {
        _automationUiState.update { state ->
            if (state !is AutomationUiState.Success) return@update state

            state.copy(
                automationDraft = state.automationDraft.copy(
                    topLevel = state.automationDraft.topLevel
                        .filter { it.id != nodeID }
                        .findAndTransformRecursive(
                            { node -> node.content.any { it.id == nodeID } },
                            { node ->
                                node.copy(content = node.content.filter { it.id != nodeID })
                            },
                            { it.content },
                            setContents = { node, contents -> node.copy(content = contents) }
                        )
                )
            )
        }
    }

    fun moveNode(nodeId: String, parentID: String? = "0", index: Int) {
        val state = automationUiState.value
        if (state !is AutomationUiState.Success) return

        val nodeDef = getNode(nodeId)!!
        val previousParent =
            findNodeWithDepth { node -> node.content.any { it.id == nodeId } }?.first

        val previousIndex = previousParent?.content?.indexOfFirst { it.id == nodeId }
            ?: state.automationDraft.topLevel.indexOfFirst { it.id == nodeId }

        val indexCorrection = if (parentID == previousParent?.id && previousIndex < index) -1 else 0

        deleteNode(nodeId)
        addNode(nodeDef, parentID, index + indexCorrection)
    }

    fun editNode(nodeID: String, newData: NodeDef) {
        _automationUiState.update { data ->
            if (data !is AutomationUiState.Success) return@update data

            data.copy(
                automationDraft = data.automationDraft.copy(
                    topLevel = data.automationDraft.topLevel
                        .findAndReplaceRecursive(
                            { it.id == nodeID },
                            newData,
                            { it.content },
                            { node, contents -> node.copy(content = contents) }
                        )
                )
            )
        }
    }

    fun getVariables(): Set<VariableDef>? {
        val state = automationUiState.value
        if (state !is AutomationUiState.Success) return null

        return state.automationDraft.variables
    }

    fun findVariable(id: String): VariableDef? {
        return getVariables()?.find { it.id == id }
    }

    fun addVariable(variable: VariableDef) {
        if (findVariable(variable.id) != null) return

        _automationUiState.update { data ->
            if (data !is AutomationUiState.Success) return@update data

            data.copy(
                automationDraft = data.automationDraft.copy(
                    variables = data.automationDraft.variables.toSet() + variable
                )
            )
        }
    }

    fun deleteVariable(id: String) {
        _automationUiState.update { data ->
            if (data !is AutomationUiState.Success) return@update data

            data.copy(
                automationDraft = data.automationDraft.copy(
                    variables = data.automationDraft.variables
                        .filter { it.id != id }
                        .toSet()
                )
            )
        }
    }

    fun renameVariable(id: String, newName: String) {
        val data = automationUiState.value
        if (data !is AutomationUiState.Success) return
        val variable = findVariable(id) ?: return

        deleteVariable(id)
        addVariable(variable.copy(name = newName))
    }

    fun changeVariableValue(id: String, newValue: Value) {
        val data = automationUiState.value
        if (data !is AutomationUiState.Success) return
        val variable = findVariable(id) ?: return

        deleteVariable(id)
        addVariable(variable.copy(value = newValue))
    }

    fun addLog(log: AutomationLogEntry) {
        val data = automationUiState.value
        if (data !is AutomationUiState.Success) return

        CoroutineScope(Dispatchers.Main).launch {
            automationsRepository.addLog(data.automationId, log)
        }
    }

    fun deleteLog(logID: String) {
        val data = automationUiState.value
        if (data !is AutomationUiState.Success) return

        CoroutineScope(Dispatchers.Main).launch {
            automationsRepository.removeLog(data.automationId, logID)
        }
    }
}