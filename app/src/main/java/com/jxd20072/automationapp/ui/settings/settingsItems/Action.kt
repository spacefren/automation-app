package com.jxd20072.automationapp.ui.settings.settingsItems

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.ui.shared.components.IconButton
import com.jxd20072.automationapp.ui.theme.LocalTheme

@Composable
fun SettingsItem(
    displayName: String,
    description: String = "",
    modifier: Modifier = Modifier,
    onActivate: () -> Unit
) {
    HorizontalSettingsItemContainer(
        displayName = displayName,
        description = description,
        modifier = modifier
    ) {
        IconButton(
            color = LocalTheme.colorScheme.surface,
            iconColor = LocalTheme.colorScheme.onSurface,
            drawableRes = R.drawable.round_open_in_new_24,
            cornerRadius = 8.dp,
            padding = 6.dp,
            size = 55.dp,
            onClick = onActivate,
            modifier = modifier.aspectRatio(1f)
        )
    }
}
