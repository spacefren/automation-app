package com.jxd20072.automationapp.ui.editor.viewmodel.managers

import androidx.compose.runtime.Immutable
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

enum class EditorBottomSheetScreen {
    EMPTY, NODE_DROP, NODE_SELECT, NODE_EDIT, VARIABLES, VARIABLE_EDIT, LOGS
}

data object Closed : NavKey
data object NodeDrop : NavKey
data class NodeSelect(
    val screenType: NodeSelectScreenType
) : NavKey

data class NodeEdit(
    val nodeID: String
) : NavKey

data object Variables : NavKey
data class VariableEdit(
    val variableId: String
) : NavKey

data object Logs : NavKey

@Immutable
data class EditorBottomSheetState(
    val backStack: List<NavKey> = listOf(Closed)
)

@Singleton
class BottomSheetManager @Inject constructor(
    val nodeEditManager: NodeEditManager,
    val variablesManager: VariablesManager,
    val variableEditManager: VariableEditManager,
    val logsManager: LogsManager,
) {
    private val _bottomSheetState = MutableStateFlow<EditorBottomSheetState>(
        EditorBottomSheetState()
    )
    val bottomSheetState = _bottomSheetState.asStateFlow()

    fun closeBottomSheet() {
        _bottomSheetState.update { EditorBottomSheetState() }
    }

    fun navigateBack() {
        if (_bottomSheetState.value.backStack.size > 1) {
            _bottomSheetState.update { state ->
                state.copy(
                    backStack = state.backStack.dropLast(1)
                )
            }
        }
    }

    fun navigateTo(screen: NavKey) {
        _bottomSheetState.update { state ->
            state.copy(
                backStack = state.backStack + screen
            )
        }
    }

    fun navigateNodeSelect(screenType: NodeSelectScreenType) {
        navigateTo(NodeSelect(screenType))
    }

    fun navigateNodeDrop() {
        navigateTo(NodeDrop)
    }

    fun navigateNodeEdit(nodeID: String) {
        nodeEditManager.startEditNode(nodeID)
        navigateTo(NodeEdit(nodeID))
    }

    fun navigateVariables(onSelectVariable: ((variableId: String) -> Unit)? = null) {
        navigateTo(Variables)
        variablesManager.overrideVariableSelect(onSelectVariable)
    }

    fun onVariableClick(variableID: String, isCreating: Boolean = false) {
        val override = variablesManager.variablesUiState.value.onVariableSelect
        if (override != null) {
            override(variableID)
            return
        }
        navigateTo(VariableEdit(variableID))
        variableEditManager.startVariableEdit(variableID, isCreating)
    }

    fun openLogs() {
        navigateTo(Logs)
    }
}