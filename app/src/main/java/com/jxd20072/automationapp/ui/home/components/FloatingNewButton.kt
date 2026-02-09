package com.jxd20072.automationapp.ui.home.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.ui.shared.components.IconLabelButton
import com.jxd20072.automationapp.ui.theme.LocalTheme
import com.jxd20072.automationapp.ui.theme.NestedLevel

@Composable
fun FloatingNewButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier.offset(-(20).dp, -(30).dp),
    ) {
        IconLabelButton(
            label = "New",
            textStyle = LocalTheme.typography.subTitle,
            drawableRes = R.drawable.round_add_24,
            color = LocalTheme.colorScheme.primary,
            iconColor = LocalTheme.colorScheme.onPrimary,
            nestedLevel = NestedLevel.ONE,
            iconSize = 20.dp,
            onClick = onClick
        )
    }
}