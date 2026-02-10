package com.jxd20072.automationapp.ui.editor.viewmodel.managers

import androidx.compose.runtime.Immutable
import androidx.navigation3.runtime.NavKey
import com.jxd20072.automationapp.core.data.model.ConfigurationValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.KClass

sealed class EditorBottomSheetScreen {
    data object Closed : NavKey
    data object NodeDrop : NavKey
    data class NodeSelect(
        val screenType: NodeSelectScreenType
    ) : NavKey

    data class NodeEdit(
        val nodeID: String
    ) : NavKey

    data class Variables(
        val initialTypeFilters: Set<KClass<ConfigurationValue<*>>> = emptySet(),
        val onVariableSelect: ((variableId: String) -> Unit)? = null
    ) : NavKey

    data class VariableEdit(
        val variableId: String,
        val isCreating: Boolean
    ) : NavKey

    data object Logs : NavKey
}


@Immutable
data class EditorBottomSheetState(
    val backStack: List<NavKey> = listOf(EditorBottomSheetScreen.Closed)
)

@Singleton
class BottomSheetManager @Inject constructor() {
    private val _bottomSheetState = MutableStateFlow(
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
        navigateTo(EditorBottomSheetScreen.NodeEdit(nodeID))
    }

    fun navigateVariables(
        initialFilters: Set<KClass<ConfigurationValue<*>>> = emptySet(),
        onSelectVariable: ((variableId: String) -> Unit)? = null
    ) {
        navigateTo(
            EditorBottomSheetScreen.Variables(
                initialTypeFilters = initialFilters,
                onVariableSelect = onSelectVariable
            )
        )
    }

    fun openLogs() {
        navigateTo(EditorBottomSheetScreen.Logs)
    }
}