package com.jxd20072.automationapp.ui.shared

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

val LocalPermissionRequester =
    staticCompositionLocalOf<ManagedActivityResultLauncher<String, Boolean>> {
        throw Exception("No permission requester provided")
    }

@Composable
fun PermissionRequestProvider(
    content: @Composable () -> Unit
) {
    val requester =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }

    CompositionLocalProvider(
        value = LocalPermissionRequester provides requester,
        content = content
    )
}