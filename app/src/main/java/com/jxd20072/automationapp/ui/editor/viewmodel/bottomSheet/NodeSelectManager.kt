package com.jxd20072.automationapp.ui.editor.viewmodel.bottomSheet

import androidx.compose.runtime.Immutable
import com.jxd20072.automationapp.core.data.model.ActionDef
import com.jxd20072.automationapp.core.nodes.NodeData
import com.jxd20072.automationapp.core.nodes.actionNodes
import com.jxd20072.automationapp.ui.editor.viewmodel.AutomationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import me.xdrop.fuzzywuzzy.FuzzySearch
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

val defaultNodeList = actionNodes.values
	.map { it.data.category }
	.distinct()
	.map { categoryName ->
		CategoryListEntry(
			categoryName = categoryName,
			nodes = actionNodes.values
				.filter { node -> node.data.category == categoryName }
				.map { node ->
					NodeListEntry(
						searchMatchRatio = 0,
						data = node.data
					)
				},
			searchMatchRatio = 0
		)
	}

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
	val nodes: List<CategoryListEntry> = defaultNodeList,
)

@Singleton
class NodeSelectManager @Inject constructor(
	private val automationManager: AutomationManager,
	private val bottomSheetManager: Provider<BottomSheetManager>
) {
	private val _nodeSelectStateState = MutableStateFlow(NodeSelectState())
	val nodeSelectState = _nodeSelectStateState.asStateFlow()

	fun searchNodes() {
		_nodeSelectStateState.update { state ->
			val searchQuery = state.nodeSearchQuery.lowercase()

			if (searchQuery.isBlank()) {
				return@update state.copy(
					nodes = defaultNodeList
				)
			}

			val filteredNodes = state.nodes

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

	fun addActionClick(action: NodeData, parentID: String? = null, index: Int? = null) {
		val actionDef = ActionDef(
			name = action.name,
			input = emptyMap(),
			output = emptyMap()
		)
		automationManager.addAction(actionDef, parentID, index)
		bottomSheetManager.get().openNodeEdit(actionDef.id)
	}

	fun resetState() {
		_nodeSelectStateState.update { NodeSelectState() }
	}

	fun setPageCollapsed(collapsed: Boolean) {
		_nodeSelectStateState.update { state ->
			state.copy(
				isCollapsed = collapsed
			)
		}
	}
}