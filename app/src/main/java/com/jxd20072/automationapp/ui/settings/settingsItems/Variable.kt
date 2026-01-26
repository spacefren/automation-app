package com.jxd20072.automationapp.ui.settings.settingsItems

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jxd20072.automationapp.core.data.model.ConfigurationValue
import com.jxd20072.automationapp.core.data.model.VariableDef
import com.jxd20072.automationapp.core.nodes.props.ValidationResult

@Composable
fun SettingsItem(
    item: ConfigurationValue.VARIABLE,
    variablesList: Set<VariableDef>,
    displayName: String,
    description: String = "",
    modifier: Modifier = Modifier,
    validate: ((ConfigurationValue.VARIABLE) -> ValidationResult)? = null,
    onValueChanged: (ConfigurationValue.VARIABLE) -> Unit
) {
    HorizontalSettingsItemContainer(
        displayName = displayName,
        description = description,
        modifier = modifier
    ) {
        
    }
}
