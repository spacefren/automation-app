package com.jxd20072.automationapp.ui.shared.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import com.jxd20072.automationapp.ui.home.components.getAutomationIconPainter
import com.jxd20072.automationapp.ui.shared.modifiers.borderWithShadow
import com.jxd20072.automationapp.ui.shared.modifiers.clickableWithIndication
import com.jxd20072.automationapp.ui.shared.modifiers.padding
import com.jxd20072.automationapp.ui.theme.LocalTheme
import com.jxd20072.automationapp.ui.theme.NestedLevel
import com.jxd20072.automationapp.ui.theme.getPreviousNestedLevel

//@Composable
//fun Button(
//    color: Color,
//    cornerRadius: Dp,
//    onClick: () -> Unit,
//    modifier: Modifier = Modifier,
//    content: @Composable BoxScope.() -> Unit
//) {
//    Box(
//        modifier = modifier
//            .clickableWithIndication { onClick() }
//            .shadow(LocalTheme.layoutSystem.shadows.secondary, RoundedCornerShape(cornerRadius))
//            .background(color),
//        content = content
//    )
//}

@Composable
fun IconButton(
    color: Color,
    iconColor: Color,
    iconName: String,
    nestedLevel: NestedLevel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .borderWithShadow(LocalTheme, nestedLevel)
            .background(color)
            .padding(LocalTheme, getPreviousNestedLevel(nestedLevel))
            .clickableWithIndication { onClick() }
    ) {
        Image(
            painter = getAutomationIconPainter(iconName),
            contentDescription = "icon: $iconName",
            colorFilter = ColorFilter.tint(iconColor),
            modifier = Modifier
                .fillMaxSize()
        )
    }
}

@Composable
fun IconButton(
    color: Color,
    iconColor: Color,
    drawableRes: Int,
    nestedLevel: NestedLevel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .borderWithShadow(LocalTheme, nestedLevel)
            .background(color)
            .padding(LocalTheme, getPreviousNestedLevel(nestedLevel))
            .clickableWithIndication { onClick() }
    ) {
        Icon(
            painter = painterResource(drawableRes),
            contentDescription = "",
            tint = iconColor,
            modifier = Modifier
                .fillMaxSize()
        )
    }
}