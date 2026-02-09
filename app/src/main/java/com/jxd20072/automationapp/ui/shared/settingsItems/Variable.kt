package com.jxd20072.automationapp.ui.shared.settingsItems

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.core.data.model.ConfigurationValue
import com.jxd20072.automationapp.core.data.model.VariableDef
import com.jxd20072.automationapp.core.data.model.VariableDefValue
import com.jxd20072.automationapp.core.nodes.props.ValidationResult
import com.jxd20072.automationapp.ui.shared.modifiers.borderWithShadow
import com.jxd20072.automationapp.ui.shared.modifiers.clickableWithIndication
import com.jxd20072.automationapp.ui.theme.LocalTheme
import com.jxd20072.automationapp.ui.theme.NestedLevel

@Composable
fun SettingsItem(
    item: ConfigurationValue.VARIABLE,
    variablesList: Set<VariableDef>,
    displayName: String,
    description: String = "",
    modifier: Modifier = Modifier,
    validate: ((ConfigurationValue.VARIABLE) -> ValidationResult)? = null,
    onValueChanged: (ConfigurationValue.VARIABLE) -> Unit,
    promptVariableSelect: ((variableId: String) -> Unit) -> Unit,
    trailingContent: (@Composable RowScope.() -> Unit)? = null,
) {
    val selectedVariable = variablesList.find { it.id == item.value }

    val iconSize = with(LocalDensity.current) {
        LocalTheme.typography.body.fontSize.toDp()
    }

    HorizontalSettingsItemContainer(
        displayName = displayName,
        description = description,
        modifier = modifier,
        trailingContent = trailingContent
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel3),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickableWithIndication {
                    promptVariableSelect {
                        onValueChanged(ConfigurationValue.VARIABLE(it))
                    }
                }
                .fillMaxHeight()
                .borderWithShadow(LocalTheme, NestedLevel.ONE)
                .background(LocalTheme.colorScheme.surface)
                .padding(LocalTheme.layoutSystem.spacing.paddingLevel2)
        ) {
            Icon(
                painter = painterResource(
                    when (selectedVariable?.value) {
                        is VariableDefValue.BOOL -> R.drawable.round_bool_24
                        is VariableDefValue.TEXT -> R.drawable.round_text_24
                        is VariableDefValue.NUMBER -> R.drawable.round_number_24
                        else -> R.drawable.round_error_24
                    }
                ),
                contentDescription = "",
                modifier = Modifier
                    .size(iconSize)
            )
            Text(
                text = selectedVariable?.name ?: "Unknown",
                style = LocalTheme.typography.body,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
