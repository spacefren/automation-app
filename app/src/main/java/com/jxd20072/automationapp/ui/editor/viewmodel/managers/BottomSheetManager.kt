package com.jxd20072.automationapp.ui.editor.viewmodel.managers

import androidx.compose.runtime.Immutable
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

sealed class EditorBottomSheetScreen {
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
}


@Immutable
data class EditorBottomSheetState(
    val backStack: List<NavKey> = listOf(EditorBottomSheetScreen.Closed)
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

    fun navigateAndClearStack(screen: NavKey) {
        _bottomSheetState.update { state ->
            state.copy(
                backStack = listOf(EditorBottomSheetScreen.Closed, screen)
            )
        }
    }

    fun navigateNodeSelect(screenType: NodeSelectScreenType) {
        navigateTo(EditorBottomSheetScreen.NodeSelect(screenType))
    }

    fun navigateNodeDrop() {
        navigateTo(EditorBottomSheetScreen.NodeDrop)
    }

    fun navigateNodeEdit(nodeID: String) {
        nodeEditManager.startEditNode(nodeID)
        navigateTo(EditorBottomSheetScreen.NodeEdit(nodeID))
    }

    fun navigateVariables(onSelectVariable: ((variableId: String) -> Unit)? = null) {
        navigateTo(EditorBottomSheetScreen.Variables)
        variablesManager.overrideVariableSelect(onSelectVariable)
    }

    fun onVariableClick(variableID: String, isCreating: Boolean = false) {
        val override = variablesManager.variablesUiState.value.onVariableSelect
        if (override != null) {
            override(variableID)
            return
        }
        navigateTo(EditorBottomSheetScreen.VariableEdit(variableID))
        variableEditManager.startVariableEdit(variableID, isCreating)
    }

    fun openLogs() {
        navigateTo(EditorBottomSheetScreen.Logs)
    }
}