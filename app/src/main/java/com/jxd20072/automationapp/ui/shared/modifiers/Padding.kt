package com.jxd20072.automationapp.ui.shared.modifiers

import androidx.compose.ui.unit.Dp
import com.jxd20072.automationapp.ui.theme.AppTheme
import com.jxd20072.automationapp.ui.theme.NestedLevel

fun getPaddingForNestedLevel(theme: AppTheme, nestedLevel: NestedLevel): Dp {
    return when (nestedLevel) {
        NestedLevel.ONE -> theme.layoutSystem.spacing.paddingLevel1
        NestedLevel.TWO -> theme.layoutSystem.spacing.paddingLevel2
        NestedLevel.THREE -> theme.layoutSystem.spacing.paddingLevel3
    }
}