package com.jxd20072.automationapp.ui.shared.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jxd20072.automationapp.ui.shared.modifiers.borderWithShadow
import com.jxd20072.automationapp.ui.shared.modifiers.clickableWithIndication
import com.jxd20072.automationapp.ui.shared.modifiers.getPaddingForNestedLevel
import com.jxd20072.automationapp.ui.theme.LocalTheme
import com.jxd20072.automationapp.ui.theme.NestedLevel
import com.jxd20072.automationapp.ui.theme.getNextNestedLevel

@Composable
fun IconLabelButton(
    label: String,
    textStyle: TextStyle,
    drawableRes: Int,
    color: Color,
    iconColor: Color,
    iconSize: Dp,
    nestedLevel: NestedLevel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .borderWithShadow(LocalTheme, nestedLevel)
            .background(color)
            .padding(getPaddingForNestedLevel(LocalTheme, getNextNestedLevel(nestedLevel)))
            .clickableWithIndication { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(end = 3.dp), // To account for the icon having some extra padding
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                painter = painterResource(drawableRes),
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(iconSize)
            )

            Text(
                text = label,
                style = textStyle,
                color = iconColor,
                modifier = Modifier
            )
        }
    }
}