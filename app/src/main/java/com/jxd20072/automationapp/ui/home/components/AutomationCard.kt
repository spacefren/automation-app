package com.jxd20072.automationapp.ui.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.svg.SvgDecoder
import com.jxd20072.automationapp.AutomationApplication
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.ui.shared.components.IconButton
import com.jxd20072.automationapp.ui.shared.components.SwipeableContainer
import com.jxd20072.automationapp.ui.theme.LocalTheme
import com.jxd20072.automationapp.ui.theme.NestedLevel

@Composable
fun getAutomationIconPainter(name: String): Painter {
    val context = LocalContext.current

    return rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data("file:///android_asset/icons/$name.svg")
            .decoderFactory(SvgDecoder.Factory())
            .build(),
        imageLoader = (context.applicationContext as AutomationApplication).imageLoader
    )
}

@Composable
fun AutomationCard(
    name: String,
    icon: String,
    lastUsed: String,
    modifier: Modifier = Modifier,
    runAutomation: () -> Unit,
    editAutomation: () -> Unit,
) {
    val iconSize = with(LocalDensity.current) {
        LocalTheme.typography.header.fontSize.toDp()
    }

    SwipeableContainer(
        icon1 = R.drawable.round_archive_24,
        backgroundColor1 = LocalTheme.colorScheme.tertiary,
        containerCornerRadius = LocalTheme.layoutSystem.shapes.cornerRadiusLevel1,
        onAction1 = { },
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .shadow(
                        LocalTheme.layoutSystem.shadows.primary,
                        RoundedCornerShape(LocalTheme.layoutSystem.shapes.cornerRadiusLevel1)
                    )
                    .background(LocalTheme.colorScheme.surface)
                    .padding(LocalTheme.layoutSystem.spacing.paddingLevel2),
                horizontalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel2),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel3),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    Row(
                        horizontalArrangement =
                            Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel3)
                    ) {
                        Box {
                            Image(
                                painter = getAutomationIconPainter(icon),
                                contentDescription = "",
                                colorFilter = ColorFilter.tint(LocalTheme.colorScheme.onBackground),
                                modifier = Modifier
                                    .size(iconSize)
                            )
                        }
                        Text(
                            text = name,
                            color = LocalTheme.colorScheme.onBackground,
                            style = LocalTheme.typography.header,
                        )
                    }
                    Text(
                        text = "Last ran: $lastUsed",
                        color = LocalTheme.colorScheme.onSurface,
                        style = LocalTheme.typography.body
                        //modifier = Modifier.padding(start = 2.dp) // Align with the icon's live area
                    )
                }

                IconButton(
                    color = LocalTheme.colorScheme.tertiary,
                    iconColor = LocalTheme.colorScheme.onTertiary,
                    drawableRes = R.drawable.round_edit_24,
                    nestedLevel = NestedLevel.TWO,
                    onClick = editAutomation,
                    modifier = Modifier
                        .size(42.dp)
                )

                IconButton(
                    color = LocalTheme.colorScheme.secondary,
                    iconColor = LocalTheme.colorScheme.onSecondary,
                    drawableRes = R.drawable.round_play_arrow_24,
                    nestedLevel = NestedLevel.TWO,
                    onClick = runAutomation,
                    modifier = Modifier
                        .size(42.dp)
                )
            }
        }
    }
}
