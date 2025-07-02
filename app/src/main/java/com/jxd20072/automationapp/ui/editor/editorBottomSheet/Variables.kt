package com.jxd20072.automationapp.ui.editor.editorBottomSheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.core.data.model.VariableDef
import com.jxd20072.automationapp.ui.editor.viewmodel.bottomSheet.VariablesScreenState
import com.jxd20072.automationapp.ui.shared.SearchBar
import com.jxd20072.automationapp.ui.shared.SwipeableContainer
import com.jxd20072.automationapp.ui.theme.LocalTheme

@Composable
fun <T> EditorVariableEntry(
	variableDef: VariableDef<T>,
	modifier: Modifier = Modifier,
	editVariable: () -> Unit,
	deleteVariable: () -> Unit
) {
	val iconSize = with(LocalDensity.current) {
		LocalTheme.typography.body.fontSize.toDp()
	}

	SwipeableContainer(
		icon1 = R.drawable.round_edit_24,
		icon2 = R.drawable.round_delete_24,
		backgroundColor1 = LocalTheme.colorScheme.tertiary,
		backgroundColor2 = LocalTheme.colorScheme.errorContainer,
		onAction1 = editVariable,
		onAction2 = deleteVariable,
		containerCornerRadius = LocalTheme.layoutSystem.shapes.cornerRadiusLevel1,
		modifier = modifier
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier
				.fillMaxWidth()
				.height(IntrinsicSize.Min)
				.shadow(
					LocalTheme.layoutSystem.shadows.secondary,
					RoundedCornerShape(LocalTheme.layoutSystem.shapes.cornerRadiusLevel1)
				)
				.background(LocalTheme.colorScheme.surface)
				.padding(LocalTheme.layoutSystem.spacing.paddingLevel3)
		) {
			Row(
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel3),
				modifier = Modifier
					.weight(1f)
			) {
				Box(
					modifier = Modifier
						.clip(RoundedCornerShape(LocalTheme.layoutSystem.shapes.cornerRadiusLevel2))
						.background(LocalTheme.colorScheme.tertiary)
						.padding(LocalTheme.layoutSystem.spacing.paddingLevel3)
				) {
					Icon(
						painter = painterResource(
							when (variableDef) {
								is VariableDef.BOOL -> R.drawable.round_bool_24
								is VariableDef.TEXT -> R.drawable.round_text_24
								is VariableDef.NUMBER -> R.drawable.round_number_24
							}
						),
						contentDescription = "",
						modifier = Modifier
							.size(iconSize)
					)
				}
				Text(
					text = variableDef.name,
					style = LocalTheme.typography.body,
				)
			}
			Icon(
				painter = painterResource(R.drawable.round_arrow_forward_24),
				contentDescription = "",
				modifier = Modifier.size(iconSize)
			)
			Box(modifier = Modifier.weight(1f)) {
				Text(
					text = variableDef.value.toString(),
					style = LocalTheme.typography.body.copy(
						textAlign = TextAlign.End
					),
					modifier = Modifier
						.align(Alignment.CenterEnd)
						.clip(RoundedCornerShape(LocalTheme.layoutSystem.shapes.cornerRadiusLevel2))
						.background(LocalTheme.colorScheme.tertiary)
						.padding(LocalTheme.layoutSystem.spacing.paddingLevel3)
				)
			}
		}
	}
}

@Composable
fun EditorVariablesScreen(
	variablesScreenState: VariablesScreenState,
	editVariable: (name: String) -> Unit,
	deleteVariable: (name: String) -> Unit,
	searchVariable: (String) -> Unit
) {
	Column(
		verticalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel1),
		modifier = Modifier
			.fillMaxSize()
			.padding(LocalTheme.layoutSystem.spacing.paddingLevel1)
			.windowInsetsPadding(WindowInsets.navigationBars)
	) {
		Text(
			text = "Variables",
			style = LocalTheme.typography.subTitle,
			color = LocalTheme.colorScheme.onBackground
		)
		SearchBar(
			searchText = "Search variables",
			queryText = variablesScreenState.searchQuery,
			autoFocus = false,
			onSearch = searchVariable
		)
		LazyColumn(
			verticalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel2),
			contentPadding = PaddingValues(bottom = LocalTheme.layoutSystem.spacing.paddingLevel1),
			modifier = Modifier.fillMaxSize()
		) {
			items(variablesScreenState.filteredVariables.sortedBy { it.name }, { it.name }) {
				EditorVariableEntry(
					variableDef = it,
					editVariable = { editVariable(it.name) },
					deleteVariable = { deleteVariable(it.name) },
					modifier = Modifier
						.animateItem()
				)
			}
		}
	}
}