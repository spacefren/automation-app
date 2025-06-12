package com.jxd20072.automationapp.ui.editor.viewmodel.bottomSheet

import androidx.compose.runtime.Immutable
import com.jxd20072.automationapp.core.data.model.NodeType
import com.jxd20072.automationapp.core.nodes.NodeData
import com.jxd20072.automationapp.core.nodes.actionNodes
import com.jxd20072.automationapp.core.nodes.triggerNodes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import me.xdrop.fuzzywuzzy.FuzzySearch
import javax.inject.Inject
import javax.inject.Singleton

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
	val isCollapsed: Boolean = false,
	val nodeSearchQuery: String = "",
	val nodeSelectType: NodeType = NodeType.ACTION,
	val nodes: List<CategoryListEntry> = emptyList(),
)

@Singleton
class NodeSelectManager @Inject constructor() {
	private val _nodeSelectStateState = MutableStateFlow(NodeSelectState())
	val nodeSelectState = _nodeSelectStateState.asStateFlow()

	private val allActions = setupNodes(NodeType.ACTION)
	private val allTriggers = setupNodes(NodeType.TRIGGER)

	fun setupNodes(nodeSelectType: NodeType): List<CategoryListEntry> {
		return when (nodeSelectType) {
			NodeType.ACTION -> actionNodes
			NodeType.TRIGGER -> triggerNodes
			else -> emptyMap()
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

	fun getNodes(nodeSelectType: NodeType): List<CategoryListEntry> {
		return when (nodeSelectType) {
			NodeType.ACTION -> allActions
			NodeType.TRIGGER -> allTriggers
			else -> emptyList()
		}
	}

	fun loadState(nodeSelectType: NodeType) {
		_nodeSelectStateState.update { state ->
			state.copy(
				nodeSelectType = nodeSelectType,
				nodes = getNodes(nodeSelectType)
			)
		}
	}

	fun searchNodes() {
		_nodeSelectStateState.update { state ->
			val searchQuery = state.nodeSearchQuery.lowercase()
			val nodeSelectType = state.nodeSelectType
			val allNodes = getNodes(nodeSelectType)

			if (searchQuery.isBlank()) {
				return@update state.copy(
					nodes = allNodes
				)
			}

			val filteredNodes = allNodes

				// Search nodes
				.map { categoryListEntry ->
					categoryListEntry.copy(
						nodes = categoryListEntry.nodes.map { nodeListEntry ->
							nodeListEntry.copy(
								searchMatchRatio = FuzzySearch.partialRatio(
									searchQuery,
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
							searchQuery,
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

			state.copy(
				nodes = filteredNodes
			)
		}
	}

	fun changeNodeSearchQuery(query: String) {
		_nodeSelectStateState.update { state ->
			state.copy(
				nodeSearchQuery = query
			)
		}
		searchNodes()
	}

	fun setPageCollapsed(collapsed: Boolean) {
		_nodeSelectStateState.update { state ->
			state.copy(
				isCollapsed = collapsed
			)
		}
	}
}