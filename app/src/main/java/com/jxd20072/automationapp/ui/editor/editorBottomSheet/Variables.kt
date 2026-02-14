package com.jxd20072.automationapp.ui.editor.editorBottomSheet

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.core.data.model.Value
import com.jxd20072.automationapp.core.data.model.VariableDef
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.EditorSession
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.VariablesViewModel
import com.jxd20072.automationapp.ui.shared.components.AppScreen
import com.jxd20072.automationapp.ui.shared.components.DropdownMenu
import com.jxd20072.automationapp.ui.shared.components.IconButton
import com.jxd20072.automationapp.ui.shared.components.SearchBar
import com.jxd20072.automationapp.ui.shared.components.SwipeableContainer
import com.jxd20072.automationapp.ui.theme.LocalTheme
import com.jxd20072.automationapp.ui.theme.NestedLevel
import kotlin.reflect.KClass

@Composable
fun EditorVariableEntry(
    variableDef: VariableDef,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    deleteVariable: () -> Unit
) {
    val iconSize = with(LocalDensity.current) {
        LocalTheme.typography.body.fontSize.toDp()
    }

    DropdownMenu(
        longPress = true,
        onNormalClick = onClick,
        options = listOf(),
        modifier = modifier
            .padding(horizontal = LocalTheme.layoutSystem.spacing.paddingLevel1)
    ) {
        SwipeableContainer(
            icon1 = R.drawable.round_delete_24,
            backgroundColor1 = LocalTheme.colorScheme.errorSecondary,
            onAction1 = deleteVariable,
            containerCornerRadius = LocalTheme.layoutSystem.shapes.cornerRadiusLevel1,
            modifier = Modifier
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel2),
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
                                when (variableDef.value) {
                                    is Value.BOOL -> R.drawable.round_bool_24
                                    is Value.TEXT -> R.drawable.round_text_24
                                    is Value.FLOAT -> R.drawable.round_number_24
                                    is Value.INT -> R.drawable.round_number_24
                                    else -> TODO()
                                }
                            ),
                            contentDescription = "",
                            modifier = Modifier
                                .size(iconSize)
                        )
                    }
                    Text(
                        text = variableDef.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = LocalTheme.typography.body,
                    )
                }
                Icon(
                    painter = painterResource(R.drawable.round_equal_24),
                    contentDescription = "",
                    modifier = Modifier.size(iconSize)
                )
                Box(modifier = Modifier.weight(1f)) {
                    Text(
                        text = variableDef.value.valueAsString(),
                        style = LocalTheme.typography.body.copy(
                            textAlign = TextAlign.End
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
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
}

@Composable
fun EditorVariablesScreen(
    editorSession: EditorSession,
    initialTypeFilters: Set<KClass<out Value>> = emptySet(),
    onVariableSelect: ((variableId: String) -> Unit)? = null,
    variablesViewModel: VariablesViewModel = hiltViewModel<VariablesViewModel, VariablesViewModel.Factory>(
        creationCallback = { factory ->
            factory.create(
                editorSession = editorSession,
                initialTypeFilters = initialTypeFilters,
                onVariableSelected = onVariableSelect,
            )
        },
    ),
) {
    val variablesScreenState by variablesViewModel.variablesUiState.collectAsState()

    AppScreen(
        modifier = Modifier.imePadding()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel1),
        ) {
            Text(
                text = if (!variablesScreenState.isSelecting) "Variables" else "Select variable",
                style = LocalTheme.typography.subTitle,
                color = LocalTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = LocalTheme.layoutSystem.spacing.paddingLevel1)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel2),
                modifier = Modifier.padding(horizontal = LocalTheme.layoutSystem.spacing.paddingLevel1)
            ) {
                SearchBar(
                    searchText = "Search variables",
                    queryText = variablesScreenState.searchQuery,
                    autoFocus = false,
                    onSearch = variablesViewModel::changeSearchQuery,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    color = LocalTheme.colorScheme.surface,
                    iconColor = LocalTheme.colorScheme.onSurface,
                    drawableRes = R.drawable.round_add_24,
                    nestedLevel = NestedLevel.ONE,
                    onClick = variablesViewModel::onCreateVariable,
                    modifier = Modifier
                        .size(37.dp)
                )
            }
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(
                    LocalTheme.layoutSystem.spacing.paddingLevel2,
                    Alignment.Bottom
                ),
                contentPadding = PaddingValues(
                    bottom = 25.dp
                            + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                ),
                modifier = Modifier
                    .animateContentSize()
            ) {
                items(variablesScreenState.filteredVariables.sortedBy { it.name }, { it.id }) {
                    EditorVariableEntry(
                        variableDef = it,
                        onClick = { variablesViewModel.onVariableClick(it.id) },
                        deleteVariable = { variablesViewModel.deleteVariable(it.id) },
                        modifier = Modifier
                            .animateItem()
                    )
                }
            }
        }
    }
}