package com.jxd20072.automationapp.ui.shared.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.jxd20072.automationapp.core.nodes.props.ValidationResult
import com.jxd20072.automationapp.ui.editor.viewmodel.utils.ProblemSeverity
import com.jxd20072.automationapp.ui.theme.LocalTheme

@Composable
fun ValidationMessage(
    status: ValidationResult,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = status !is ValidationResult.Success,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = modifier.fillMaxWidth()
        ) {
            Text(
                text = when (status) {
                    is ValidationResult.Success -> ""
                    is ValidationResult.Error -> status.message
                },
                color = when (status) {
                    is ValidationResult.Error if status.severity == ProblemSeverity.ERROR
                        -> LocalTheme.colorScheme.error

                    is ValidationResult.Error if status.severity == ProblemSeverity.WARNING
                        -> LocalTheme.colorScheme.warning

                    else -> LocalTheme.colorScheme.onBackground
                },
                fontSize = 14.sp
            )
        }
    }
}