package com.jxd20072.automationapp.ui.shared.settingsItems

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.jxd20072.automationapp.core.data.model.ConfigurationValue
import com.jxd20072.automationapp.ui.shared.modifiers.clickableWithIndication
import com.jxd20072.automationapp.ui.theme.LocalTheme

@Composable
fun SettingsItem(
    displayName: String,
    description: String = "",
    item: ConfigurationValue.CHOOSE,
    options: List<ConfigurationValue.CHOOSE>,
    modifier: Modifier = Modifier,
    onValueChanged: (newValue: ConfigurationValue.CHOOSE) -> Unit,
    trailingContent: (@Composable RowScope.() -> Unit)? = null
) {
    VerticalSettingsItemContainer(
        displayName = displayName,
        description = description,
        trailingContent = trailingContent,
        modifier = modifier
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel3),
            verticalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel3)
        ) {
            options.forEach { option ->
                Text(
                    text = option.value,
                    color = if (option == item) LocalTheme.colorScheme.onBackground
                    else LocalTheme.colorScheme.onSurface,
                    style = LocalTheme.typography.body,
                    modifier = Modifier
                        .clickableWithIndication { onValueChanged(option) }
                        .clip(RoundedCornerShape(LocalTheme.layoutSystem.shapes.cornerRadiusLevel2))
                        .background(
                            if (option == item) LocalTheme.colorScheme.tertiary
                            else LocalTheme.colorScheme.surface
                        )
                        .padding(LocalTheme.layoutSystem.spacing.paddingLevel3)
                )
            }
        }
    }
}
