package com.jxd20072.automationapp.ui.shared.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.ui.theme.LocalTheme

@Composable
fun BaseTextInput(
    placeholderText: String,
    text: String,
    imeAction: ImeAction,
    icon: Int? = null,
    autoFocus: Boolean = false,
    modifier: Modifier = Modifier,
    onTextChanged: ((text: String) -> Unit),
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val closeInteractionSource = remember { MutableInteractionSource() }
    var state by remember {
        mutableStateOf(TextFieldValue(text))
    }
    state = state.copy(text = text)

    val iconSize = with(LocalDensity.current) {
        LocalTheme.typography.header.fontSize.toDp()
    }

    LaunchedEffect(Unit) {
        if (autoFocus) {
            focusRequester.requestFocus()
        }
    }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .shadow(
                LocalTheme.layoutSystem.shadows.secondary,
                RoundedCornerShape(LocalTheme.layoutSystem.shapes.cornerRadiusLevel1)
            )
            .background(LocalTheme.colorScheme.surface)
            .clickable(interactionSource = null, indication = ripple()) {
                focusRequester.requestFocus()
            }
            .padding(LocalTheme.layoutSystem.spacing.paddingLevel2)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel3),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon != null) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = "search",
                    tint = LocalTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .size(iconSize)
                )
            }
            Box {
                BasicTextField(
                    value = state,
                    onValueChange = {
                        onTextChanged(it.text)
                        state = it
                    },
                    singleLine = true,
                    enabled = true,
                    cursorBrush = SolidColor(LocalTheme.colorScheme.onSurface),
                    textStyle = LocalTheme.typography.body.copy(
                        color = LocalTheme.colorScheme.onBackground
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = imeAction),
                    keyboardActions = KeyboardActions(onAny = {
                        focusManager.clearFocus()
                    }),
                    modifier = Modifier
                        .wrapContentHeight(align = Alignment.CenterVertically)
                        .focusRequester(focusRequester)
                        .onFocusChanged { focusState ->
                            if (focusState.hasFocus) {
                                state = state.copy(
                                    selection = TextRange(0, state.text.length)
                                )
                            }
                        }
                )

                if (text == "") {
                    Text(
                        text = placeholderText,
                        color = LocalTheme.colorScheme.onSurface,
                        style = LocalTheme.typography.body,
                        modifier = Modifier
                            .wrapContentHeight(align = Alignment.CenterVertically)
                    )
                }
            }
        }

        if (text != "") {
            Icon(
                painter = painterResource(R.drawable.round_close_24),
                contentDescription = "search",
                tint = LocalTheme.colorScheme.onSurface,
                modifier = Modifier
                    .size(iconSize)
                    .clickable(interactionSource = closeInteractionSource, indication = null) {
                        state = state.copy(text = "")
                        onTextChanged("")
                    }
            )
        }
    }
}