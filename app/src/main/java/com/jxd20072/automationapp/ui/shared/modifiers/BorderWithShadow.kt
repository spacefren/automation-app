package com.jxd20072.automationapp.ui.shared.modifiers

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import com.jxd20072.automationapp.ui.theme.AppTheme

enum class NestedLevel { ONE, TWO, THREE }

fun Modifier.borderWithShadow(theme: AppTheme, nestedLevel: NestedLevel) = then(
    when (nestedLevel) {
        NestedLevel.ONE -> shadow(
            theme.layoutSystem.shadows.primary,
            RoundedCornerShape(theme.layoutSystem.shapes.cornerRadiusLevel1)
        )

        NestedLevel.TWO -> shadow(
            theme.layoutSystem.shadows.secondary,
            RoundedCornerShape(theme.layoutSystem.shapes.cornerRadiusLevel2)
        )

        NestedLevel.THREE -> clip(
            RoundedCornerShape(theme.layoutSystem.shapes.cornerRadiusLevel3)
        )
    }
)