package com.jxd20072.automationapp.ui.shared.settingsItems

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.jxd20072.automationapp.core.nodes.props.ValidationResult
import com.jxd20072.automationapp.ui.shared.components.ValidationMessage
import com.jxd20072.automationapp.ui.theme.LocalTheme

@Composable
fun SettingsItemContainerContent(
    displayName: String,
    description: String = "",
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel3),
        modifier = modifier
    ) {
        Text(
            text = displayName,
            color = LocalTheme.colorScheme.onBackground,
            style = LocalTheme.typography.header,
        )
        if (description.isNotEmpty()) {
            Text(
                text = description,
                color = LocalTheme.colorScheme.onSurface,
                style = LocalTheme.typography.body,
            )
        }
    }
}

@Composable
fun HorizontalSettingsItemContainer(
    displayName: String,
    description: String = "",
    validationResult: ValidationResult? = null,
    modifier: Modifier = Modifier,
    trailingContent: (@Composable RowScope.() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit
) {
    GlobalSettingsItemContainer(validationResult) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
        ) {
            SettingsItemContainerContent(
                displayName = displayName,
                description = description,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(LocalTheme.layoutSystem.spacing.paddingLevel1))
            Row(
                horizontalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel2),
                modifier = Modifier
                    .height(IntrinsicSize.Min)
            ) {
                content()
                if (trailingContent != null) {
                    trailingContent()
                }
            }
        }
    }
}

@Composable
fun VerticalSettingsItemContainer(
    displayName: String,
    description: String = "",
    validationResult: ValidationResult? = null,
    modifier: Modifier = Modifier,
    trailingContent: (@Composable RowScope.() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit
) {
    GlobalSettingsItemContainer(validationResult) {
        Column(
            modifier = modifier
                .fillMaxWidth()
        ) {
            SettingsItemContainerContent(
                displayName = displayName,
                description = description,
            )
            Spacer(modifier = Modifier.height(LocalTheme.layoutSystem.spacing.paddingLevel3))
            Row(
                horizontalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel2),
                modifier = Modifier
                    .height(IntrinsicSize.Min)
            ) {
                content()
                if (trailingContent != null) {
                    trailingContent()
                }
            }
        }
    }
}

@Composable
fun GlobalSettingsItemContainer(
    validationResult: ValidationResult?,
    content: @Composable () -> Unit
) {
    if (validationResult != null) {
        Column {
            content()
            ValidationMessage(
                validationResult,
                Modifier.padding(top = LocalTheme.layoutSystem.spacing.paddingLevel3)
            )
        }
    } else content()
}