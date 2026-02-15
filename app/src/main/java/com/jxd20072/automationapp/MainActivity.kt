package com.jxd20072.automationapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.jxd20072.automationapp.ui.AutomationApp
import com.jxd20072.automationapp.ui.shared.components.SnackbarHost
import com.jxd20072.automationapp.ui.shared.utils.PermissionRequestProvider
import com.jxd20072.automationapp.ui.theme.AutomationAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity() : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            AutomationAppTheme(dynamicColor = false) {
                PermissionRequestProvider {
                    SnackbarHost()
                    AutomationApp()
                }
            }
        }
    }
}