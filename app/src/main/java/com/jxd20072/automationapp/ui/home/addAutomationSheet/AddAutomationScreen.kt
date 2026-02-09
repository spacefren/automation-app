package com.jxd20072.automationapp.ui.home.addAutomationSheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.core.nodes.props.ValidationResult
import com.jxd20072.automationapp.ui.shared.components.BaseTextInput
import com.jxd20072.automationapp.ui.shared.components.IconButton
import com.jxd20072.automationapp.ui.shared.components.ValidationMessage
import com.jxd20072.automationapp.ui.shared.modifiers.clickableWithIndication
import com.jxd20072.automationapp.ui.theme.LocalTheme
import com.jxd20072.automationapp.ui.theme.NestedLevel

@Composable
fun AddAutomationScreen(
    selectedIcon: String,
    name: String,
    autoOpenKeyboard: Boolean,
    validateAutomationName: (name: String) -> ValidationResult,
    onNameChanged: (name: String) -> Unit,
    onIconChangeRequest: () -> Unit,
    onCreateAutomation: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel1),
        modifier = Modifier
            .imePadding()
            .padding(LocalTheme.layoutSystem.spacing.paddingLevel1)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "New automation",
                color = LocalTheme.colorScheme.onBackground,
                style = LocalTheme.typography.subTitle,
            )
            Icon(
                painter = painterResource(R.drawable.round_check_24),
                contentDescription = "confirm",
                tint = LocalTheme.colorScheme.onBackground,
                modifier = Modifier
                    .size(25.dp)
                    .clickableWithIndication {
                        if (validateAutomationName(name) is ValidationResult.Success) {
                            onCreateAutomation()
                        }
                    }
            )
        }
        Column {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IconButton(
                    color = LocalTheme.colorScheme.surface,
                    iconColor = LocalTheme.colorScheme.onSurface,
                    iconName = selectedIcon,
                    nestedLevel = NestedLevel.ONE,
                    modifier = Modifier
                        .size(36.dp)
                ) {
                    onIconChangeRequest()
                }
                BaseTextInput(
                    placeholderText = "Name",
                    imeAction = ImeAction.Done,
                    autoFocus = if (autoOpenKeyboard) name.isEmpty() else false,
                    text = name,
                    modifier = Modifier.fillMaxWidth(),
                ) { text ->
                    onNameChanged(text)
                }
            }
            ValidationMessage(
                validateAutomationName(name),
                Modifier.padding(top = LocalTheme.layoutSystem.spacing.paddingLevel3)
            )
        }
        Spacer(modifier = Modifier.height(100.dp))
    }
}