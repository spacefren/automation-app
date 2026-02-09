package com.jxd20072.automationapp.ui.shared.settingsItems

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.ui.shared.components.IconButton
import com.jxd20072.automationapp.ui.theme.LocalTheme
import com.jxd20072.automationapp.ui.theme.NestedLevel

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
            nestedLevel = NestedLevel.TWO,
            onClick = onActivate,
            modifier = modifier
                .fillMaxHeight()
                .aspectRatio(1f)
        )
    }
}
