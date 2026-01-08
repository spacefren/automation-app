package com.jxd20072.automationapp.ui.shared.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jxd20072.automationapp.ui.theme.LocalTheme

@Composable
fun AppScreen(
    content: @Composable BoxScope.() -> Unit
) {
    DropdownHost {
        Layer(
            modifier = Modifier
                .fillMaxSize()
                .background(LocalTheme.colorScheme.background),
            content = content
        )
    }
}