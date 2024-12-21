package com.jxd20072.automationapp.ui.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AutomationEditor(
	editorViewModel: EditorViewModel = hiltViewModel()
) {
	val editorUiState by editorViewModel.editorUiState.collectAsState()


}