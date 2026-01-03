package com.jxd20072.automationapp.ui.settings.settingsItems

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jxd20072.automationapp.core.nodes.props.ValidationResult
import com.jxd20072.automationapp.ui.shared.components.ValidationMessage
import com.jxd20072.automationapp.ui.theme.LocalTheme

@Composable
fun UserSettingsItemContainerContent(
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
fun HorizontalUserSettingsItemContainer(
    displayName: String,
    description: String = "",
    validationResult: ValidationResult? = null,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    GlobalUserSettingsItemContainer(validationResult) {
        Column(
            verticalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel3)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
                    .fillMaxWidth()
            ) {
                UserSettingsItemContainerContent(
                    displayName = displayName,
                    description = description,
                    modifier = Modifier.widthIn(max = 220.dp)
                )
                Spacer(modifier = Modifier.width(LocalTheme.layoutSystem.spacing.paddingLevel2))
                Box(
                    content = content,
                    modifier = Modifier
                        .height(35.dp)
                )
            }
        }
    }
}

@Composable
fun VerticalUserSettingsItemContainer(
    displayName: String,
    description: String = "",
    validationResult: ValidationResult? = null,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    GlobalUserSettingsItemContainer(validationResult) {
        Column(
            modifier = modifier
                .fillMaxWidth()
        ) {
            UserSettingsItemContainerContent(
                displayName = displayName,
                description = description,
            )
            Spacer(modifier = Modifier.height(LocalTheme.layoutSystem.spacing.paddingLevel3))
            Box(
                content = content,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun GlobalUserSettingsItemContainer(
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