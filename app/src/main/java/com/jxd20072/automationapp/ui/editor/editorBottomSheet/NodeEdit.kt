package com.jxd20072.automationapp.ui.editor.editorBottomSheet

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.core.data.model.NodeType
import com.jxd20072.automationapp.ui.editor.editorBottomSheet.components.EditorInputField
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.EditorSession
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.NodeEditState
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.NodeEditViewModel
import com.jxd20072.automationapp.ui.editor.viewmodel.utils.EditorProblem
import com.jxd20072.automationapp.ui.shared.components.AppScreen
import com.jxd20072.automationapp.ui.shared.components.SettingsResetButton
import com.jxd20072.automationapp.ui.shared.modifiers.clickableWithIndication
import com.jxd20072.automationapp.ui.theme.LocalTheme

@Composable
fun EditorNodeEditScreen(
    editorSession: EditorSession,
    nodeId: String,
    nodeEditViewModel: NodeEditViewModel = hiltViewModel<NodeEditViewModel, NodeEditViewModel.Factory>(
        creationCallback = { factory -> factory.create(nodeId, editorSession) },
    ),
) {
    val nodeEditScreenState by nodeEditViewModel.nodeEditScreenState.collectAsState()
    val automationUiState by nodeEditViewModel.automationUiState.collectAsState(null)

    if (nodeEditScreenState !is NodeEditState.Success) return
    if (automationUiState == null) return

    val automation = (automationUiState!!.automationDraft)
    val nodeData = (nodeEditScreenState as NodeEditState.Success).nodeData
    val nodeDef = (nodeEditScreenState as NodeEditState.Success).nodeDef
    val problems = (nodeEditScreenState as NodeEditState.Success).nodeProblems

    AppScreen(
        modifier = Modifier
            .padding(horizontal = LocalTheme.layoutSystem.spacing.paddingLevel1)
            .padding(bottom = LocalTheme.layoutSystem.spacing.paddingLevel1)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .imePadding()
    ) {
        Column {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Edit " + when (nodeDef.nodeType) {
                        NodeType.ACTION -> "action"
                        NodeType.TRIGGER -> "trigger"
                        NodeType.FUNCTION -> "function"
                    },
                    style = LocalTheme.typography.subTitle,
                    color = LocalTheme.colorScheme.onBackground
                )
                SettingsResetButton(nodeEditViewModel::resetDefaultConfig)
            }

            RequiredPermissionsCard(
                problems = problems.filter { it is EditorProblem.MissingPermission }.toSet()
                        as Set<EditorProblem.MissingPermission>,
                onPermissionGranted = nodeEditViewModel::onPermissionGranted
            )

            // Spacing already handled below
            nodeData.inputSchemas.forEach { (name, schema) ->
                val currentValue = nodeDef.input[name] ?: schema.accepts.first().defaultValue

                Spacer(modifier = Modifier.height(LocalTheme.layoutSystem.spacing.paddingLevel1))

                EditorInputField(
                    inputSchema = schema,
                    variablesList = automation.variables,
                    problem = problems.firstOrNull {
                        if (it !is EditorProblem.NodeInputProblem) return@firstOrNull false

                        it.originatingInput == name
                    } as EditorProblem.NodeInputProblem?,
                    currentValue = currentValue,
                    onValueChanged = { nodeEditViewModel.setNodeConfig(name, it) },
                    promptVariableSelect = nodeEditViewModel::promptSelectVariable
                )
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequiredPermissionsCard(
    problems: Set<EditorProblem.MissingPermission>,
    onPermissionGranted: () -> Unit,
) {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            onPermissionGranted()
        }
    }


    AnimatedVisibility(
        problems.isNotEmpty(),
        modifier = Modifier
            .padding(top = LocalTheme.layoutSystem.spacing.paddingLevel1)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel2),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    LocalTheme.layoutSystem.shadows.primary,
                    RoundedCornerShape(LocalTheme.layoutSystem.shapes.cornerRadiusLevel1)
                )
                .background(LocalTheme.colorScheme.errorContainer)
                .padding(LocalTheme.layoutSystem.spacing.paddingLevel2)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel2),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(R.drawable.round_error_24),
                    contentDescription = "errorIcon",
                    modifier = Modifier
                        .size(
                            with(LocalDensity.current) {
                                LocalTheme.typography.header.fontSize.toDp()
                            }
                        )
                )
                Text(
                    text = "Required permissions",
                    style = LocalTheme.typography.header,
                    color = LocalTheme.colorScheme.onBackground
                )
            }
            problems
                .map { problem ->
                    val displayName = when (problem.permission) {
                        Manifest.permission.POST_NOTIFICATIONS -> "Notifications"
                        else -> problem.permission.substringAfterLast('.')
                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickableWithIndication {
                                launcher.launch(problem.permission)
                            }
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(LocalTheme.layoutSystem.shapes.cornerRadiusLevel2))
                            .background(LocalTheme.colorScheme.errorSecondary)
                            .padding(LocalTheme.layoutSystem.spacing.paddingLevel2)
                    ) {
                        Text(
                            text = displayName,
                            style = LocalTheme.typography.body,
                            color = LocalTheme.colorScheme.onBackground
                        )
                        Icon(
                            painter = painterResource(R.drawable.round_arrow_forward_24),
                            contentDescription = "warningIcon",
                            modifier = Modifier
                                .size(
                                    with(LocalDensity.current) {
                                        LocalTheme.typography.body.fontSize.toDp()
                                    }
                                )
                        )
                    }
                }
        }
    }
}