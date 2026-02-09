package com.jxd20072.automationapp.ui.shared.modifiers

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import com.jxd20072.automationapp.ui.theme.AppTheme
import com.jxd20072.automationapp.ui.theme.NestedLevel

@SuppressLint("SuspiciousModifierThen")
fun Modifier.padding(theme: AppTheme, nestedLevel: NestedLevel) = this.then(
    when (nestedLevel) {
        NestedLevel.ONE -> padding(theme.layoutSystem.spacing.paddingLevel1)
        NestedLevel.TWO -> padding(theme.layoutSystem.spacing.paddingLevel2)
        NestedLevel.THREE -> padding(theme.layoutSystem.spacing.paddingLevel3)
    }
)