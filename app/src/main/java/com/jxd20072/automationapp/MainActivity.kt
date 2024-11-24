package com.jxd20072.automationapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.jxd20072.automationapp.ui.AutomationApp
import com.jxd20072.automationapp.ui.theme.AutomationAppTheme

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			AutomationAppTheme(dynamicColor = false) {
				Scaffold(
					modifier = Modifier.fillMaxSize(),
				) { innerPadding ->
					AutomationApp(modifier = Modifier.padding(innerPadding))
				}
			}
		}
	}
}