package com.jxd20072.automationapp.ui.editor.editorBottomSheet

import android.Manifest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.core.data.model.AutomationDef
import com.jxd20072.automationapp.core.data.model.ConfigurationValue
import com.jxd20072.automationapp.core.data.model.NodeType
import com.jxd20072.automationapp.core.nodes.props.NodeInputDefinition
import com.jxd20072.automationapp.core.nodes.props.ValidationResult
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.NodeEditState
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.NodeEditViewModel
import com.jxd20072.automationapp.ui.settings.settingsItems.SettingsItem
import com.jxd20072.automationapp.ui.shared.components.AppScreen
import com.jxd20072.automationapp.ui.shared.components.SettingsResetButton
import com.jxd20072.automationapp.ui.shared.components.clickableWithIndication
import com.jxd20072.automationapp.ui.theme.LocalTheme

@Composable
fun EditorNodeEditScreen(
    nodeId: String,
    nodeEditViewModel: NodeEditViewModel = hiltViewModel<NodeEditViewModel, NodeEditViewModel.Factory>(
        creationCallback = { factory -> factory.create(nodeId) },
    ),
) {
    val nodeEditScreenState by nodeEditViewModel.nodeEditScreenState.collectAsState()
    val automationDraft by nodeEditViewModel.editingAutomationDraft.collectAsState(null)

    if (nodeEditScreenState !is NodeEditState.Success) return
    if (automationDraft == null) return

    val automation = (automationDraft as AutomationDef)
    val nodeData = (nodeEditScreenState as NodeEditState.Success).nodeData
    val nodeDef = (nodeEditScreenState as NodeEditState.Success).nodeDef

    AppScreen {
        Column(
            verticalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel1),
            modifier = Modifier
                .imePadding()
                .padding(LocalTheme.layoutSystem.spacing.paddingLevel1)
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
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
                permissions = nodeData.permissions
            )

            nodeData.inputDefinitions.forEach { (name, inputDef) ->
                val currentValue = nodeDef.input[name]

                NodeEditField(
                    inputDefinition = inputDef,
                    automationDraft = automation,
                    currentValue = currentValue,
                    onValueChanged = { nodeEditViewModel.setNodeConfig(name, it) },
                    promptSelectVariable = nodeEditViewModel::promptSelectVariable
                )
            }
        }
    }
}

@Composable
fun NodeEditField(
    automationDraft: AutomationDef,
    inputDefinition: NodeInputDefinition<*>,
    currentValue: ConfigurationValue<*>?, // Should be T but kotlin is just dumb
    onValueChanged: (ConfigurationValue<*>) -> Unit,
    promptSelectVariable: ((selected: String) -> Unit) -> Unit
) {
    when (inputDefinition.defaultValue) {
        is ConfigurationValue.BOOL -> {
            if (currentValue != null && currentValue !is ConfigurationValue.BOOL) return
            SettingsItem(
                item = currentValue ?: inputDefinition.defaultValue,
                displayName = inputDefinition.displayName,
                description = inputDefinition.description,
                validate = inputDefinition.validate as (ConfigurationValue.BOOL) -> ValidationResult,
                onValueChanged = onValueChanged
            )
        }

        is ConfigurationValue.TEXT -> {
            if (currentValue != null && currentValue !is ConfigurationValue.TEXT) return
            SettingsItem(
                item = currentValue ?: inputDefinition.defaultValue,
                displayName = inputDefinition.displayName,
                description = inputDefinition.description,
                validate = inputDefinition.validate as (ConfigurationValue.TEXT) -> ValidationResult,
                onValueChanged = onValueChanged
            )
        }

        is ConfigurationValue.NUMBER -> {
            if (currentValue != null && currentValue !is ConfigurationValue.NUMBER) return
            SettingsItem(
                item = currentValue ?: inputDefinition.defaultValue,
                displayName = inputDefinition.displayName,
                description = inputDefinition.description,
                validate = inputDefinition.validate as (ConfigurationValue.NUMBER) -> ValidationResult,
                onValueChanged = onValueChanged
            )
        }

        is ConfigurationValue.VARIABLE -> {
            if (currentValue != null && currentValue !is ConfigurationValue.VARIABLE) return
            SettingsItem(
                item = currentValue ?: inputDefinition.defaultValue,
                variablesList = automationDraft.variables,
                displayName = inputDefinition.displayName,
                description = inputDefinition.description,
                validate = inputDefinition.validate as (ConfigurationValue.VARIABLE) -> ValidationResult,
                onValueChanged = onValueChanged,
                promptVariableSelect = promptSelectVariable
            )
        }

        else -> {}
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequiredPermissionsCard(
    permissions: List<String>
) {
    val permissionState = permissions.map { it to rememberPermissionState(it) }

    AnimatedVisibility(
        permissionState.any { !it.second.status.isGranted }
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
            permissionState
                .filter { (_, state) -> !state.status.isGranted }
                .map { (permission, state) ->
                    val displayName = when (permission) {
                        Manifest.permission.POST_NOTIFICATIONS -> "Notifications"
                        else -> permission.substringAfterLast('.')
                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickableWithIndication {
                                state.launchPermissionRequest()
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