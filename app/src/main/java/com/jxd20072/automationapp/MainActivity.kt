package com.jxd20072.automationapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.jxd20072.automationapp.ui.AutomationApp
import com.jxd20072.automationapp.ui.shared.SnackbarHost
import com.jxd20072.automationapp.ui.theme.AutomationAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
	@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()

		setContent {
			AutomationAppTheme(dynamicColor = false) {
				SnackbarHost()
				AutomationApp()
			}
		}
	}
}