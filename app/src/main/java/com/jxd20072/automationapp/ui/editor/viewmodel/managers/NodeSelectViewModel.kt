package com.jxd20072.automationapp.ui.editor.viewmodel.managers

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import com.jxd20072.automationapp.core.nodes.NodeData
import com.jxd20072.automationapp.core.nodes.actionNodes
import com.jxd20072.automationapp.core.nodes.triggerNodes
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import me.xdrop.fuzzywuzzy.FuzzySearch

enum class NodeSelectScreenType { ACTION, TRIGGER }

@Immutable
data class NodeListEntry(
    val data: NodeData,
    val searchMatchRatio: Int,
)

@Immutable
data class CategoryListEntry(
    val categoryName: String,
    val nodes: List<NodeListEntry>,
    val searchMatchRatio: Int
)

@Immutable
data class NodeSelectState(
    val nodeSearchQuery: String = "",
    val nodes: List<CategoryListEntry> = emptyList(),
)

@HiltViewModel(assistedFactory = NodeSelectViewModel.Factory::class)
class NodeSelectViewModel @AssistedInject constructor(
    @Assisted val screenType: NodeSelectScreenType,
    val bottomSheetManager: BottomSheetManager,
    val dragManager: DragManager,
    val automationManager: AutomationManager
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun create(screenType: NodeSelectScreenType): NodeSelectViewModel
    }

    private val allNodes = getAllNodes()

    private val _nodeSelectStateState = MutableStateFlow(
        NodeSelectState(
            nodes = getFilteredNodes("")
        )
    )
    val nodeSelectState = _nodeSelectStateState.asStateFlow()

    fun getAllNodes(): List<CategoryListEntry> {
        return when (screenType) {
            NodeSelectScreenType.ACTION -> actionNodes
            NodeSelectScreenType.TRIGGER -> triggerNodes
        }.values
            .groupBy { it.data.category }
            .map { category ->
                CategoryListEntry(
                    categoryName = category.key,
                    nodes = category.value
                        .map { node ->
                            NodeListEntry(
                                searchMatchRatio = 0,
                                data = node.data
                            )
                        },
                    searchMatchRatio = 0
                )
            }
    }

    fun getFilteredNodes(searchQuery: String): List<CategoryListEntry> {
        val lowercaseSearchQuery = searchQuery.lowercase()

        if (lowercaseSearchQuery.isBlank()) {
            return allNodes
        }

        return allNodes

            // Search nodes
            .map { categoryListEntry ->
                categoryListEntry.copy(
                    nodes = categoryListEntry.nodes.map { nodeListEntry ->
                        nodeListEntry.copy(
                            searchMatchRatio = FuzzySearch.partialRatio(
                                lowercaseSearchQuery,
                                nodeListEntry.data.displayName.lowercase()
                            )
                        )
                    }
                )
            }

            // Search categories
            .map { categoryListEntry ->
                categoryListEntry.copy(
                    searchMatchRatio = FuzzySearch.partialRatio(
                        lowercaseSearchQuery,
                        categoryListEntry.categoryName.lowercase()
                    )
                )
            }

            // Filter
            .map { categoryListEntry ->
                categoryListEntry.copy(
                    nodes = categoryListEntry.nodes.filter { it.searchMatchRatio > 75 }
                )
            }

            .filter { categoryListEntry ->
                categoryListEntry.searchMatchRatio > 75 || categoryListEntry.nodes.isNotEmpty()
            }
    }

    fun onNodeCreateDrag(nodeData: NodeData, nodePosition: Offset, pressedPosition: Offset) {
        bottomSheetManager.navigateNodeDrop()
        dragManager.dragCreateNode(nodeData, nodePosition, pressedPosition)
    }

    fun onCreateNodeClick(nodeData: NodeData) {
        val nodeDef = automationManager.createNodeDef(nodeData)
        automationManager.addNode(nodeDef)
        bottomSheetManager.navigateAndClearStack(
            EditorBottomSheetScreen.NodeEdit(nodeDef.id)
        )
    }

    fun changeNodeSearchQuery(query: String) {
        _nodeSelectStateState.update { state ->
            state.copy(
                nodeSearchQuery = query,
                nodes = getFilteredNodes(query)
            )
        }
    }
}