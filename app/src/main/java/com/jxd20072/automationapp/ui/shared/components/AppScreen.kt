package com.jxd20072.automationapp.ui.shared.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jxd20072.automationapp.ui.theme.LocalTheme

@Composable
fun AppScreen(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    DropdownHost {
        Layer(
            modifier = modifier
                .background(LocalTheme.colorScheme.background),
            content = content
        )
    }
}