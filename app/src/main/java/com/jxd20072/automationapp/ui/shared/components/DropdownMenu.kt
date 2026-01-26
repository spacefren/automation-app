package com.jxd20072.automationapp.ui.shared.components

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.zIndex
import com.jxd20072.automationapp.ui.shared.modifiers.NestedLevel
import com.jxd20072.automationapp.ui.shared.modifiers.borderWithShadow
import com.jxd20072.automationapp.ui.theme.LocalTheme

@Immutable
data class DropdownOption(
    val icon: Int,
    val text: String,
    val danger: Boolean,
    val action: () -> Unit,
)

data class DropdownData(
    val dropdownPosition: Offset = Offset(0f, 0f),
    val options: List<DropdownOption> = emptyList(),
)

@Immutable
sealed interface DropdownState {
    data object Inactive : DropdownState
    data class Active(
        val data: DropdownData
    ) : DropdownState
}

class DropdownHostState() {
    var state by mutableStateOf<DropdownState>(DropdownState.Inactive)

    fun showDropdown(dropdownData: DropdownData) {
        state = DropdownState.Active(data = dropdownData)
    }

    fun hideDropdown() {
        state = DropdownState.Inactive
    }
}

val LocalDropdownHostState = staticCompositionLocalOf {
    DropdownHostState()
}

@Composable
fun DropdownOption(
    option: DropdownOption,
    modifier: Modifier = Modifier
) {
    val localDropdownHostState = LocalDropdownHostState.current

    Row(
        horizontalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel3),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable {
                localDropdownHostState.hideDropdown()
                option.action()
            }
            .fillMaxWidth()
            .padding(LocalTheme.layoutSystem.spacing.paddingLevel2)
    ) {
        Icon(
            painter = painterResource(option.icon),
            contentDescription = "",
            tint = if (option.danger) LocalTheme.colorScheme.error
            else LocalTheme.colorScheme.onBackground,
            modifier = Modifier
                .size(with(LocalDensity.current) {
                    LocalTheme.typography.body.fontSize.toDp()
                })
        )
        Text(
            text = option.text,
            color = if (option.danger) LocalTheme.colorScheme.error
            else LocalTheme.colorScheme.onBackground,
            style = LocalTheme.typography.body,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}


@Composable
fun DropdownHost(
    content: @Composable () -> Unit
) {
    val dropdownHostState = remember { DropdownHostState() }

    val dropdownState = dropdownHostState.state
    var lastActiveDropdown by remember { mutableStateOf(DropdownData()) }

    if (dropdownState is DropdownState.Active) {
        lastActiveDropdown = dropdownState.data
    }

    AnimatedVisibility(
        dropdownState is DropdownState.Active,
        enter = EnterTransition.None,
        exit = ExitTransition.None,
        modifier = Modifier
            .zIndex(100f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(interactionSource = null, indication = null) {
                    dropdownHostState.hideDropdown()
                }
        ) {
            Box(
                modifier = Modifier
                    .graphicsLayer(
                        translationX = lastActiveDropdown.dropdownPosition.x,
                        translationY = lastActiveDropdown.dropdownPosition.y
                    )
                    .animateEnterExit()
            ) {
                Column(
                    modifier = Modifier
                        .borderWithShadow(LocalTheme, NestedLevel.TWO)
                        .background(LocalTheme.colorScheme.surface)
                        .width(IntrinsicSize.Min)
                ) {
                    lastActiveDropdown.options.forEach {
                        DropdownOption(it)
                    }
                }
            }
        }
    }

    CompositionLocalProvider(
        LocalDropdownHostState provides dropdownHostState
    ) {
        content()
    }
}

@Composable
fun DropdownMenu(
    options: List<DropdownOption>,
    longPress: Boolean = false,
    onNormalClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    var dropdownPosition by remember { mutableStateOf(Offset(0f, 0f)) }
    val localDropdownState = LocalDropdownHostState.current

    fun openMenu(offset: Offset) {
        localDropdownState.showDropdown(
            DropdownData(
                dropdownPosition = dropdownPosition + offset,
                options = options,
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned {
                dropdownPosition = it.positionOnScreen()
            }
            .clickableWithIndication(
                onClick = {
                    if (onNormalClick != null) {
                        onNormalClick()
                        return@clickableWithIndication
                    }
                    if (longPress) return@clickableWithIndication

                    Log.d("APP_A", "DropdownMenu: AAA")
                    openMenu(it)
                },
                onLongPress = {
                    if (!longPress) return@clickableWithIndication

                    Log.d("APP_A", "DropdownMenu: BBB")
                    openMenu(it)
                }
            )
    ) {
        content()
    }
}
