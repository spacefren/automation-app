package com.jxd20072.automationapp.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.Text
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.core.data.AutomationsMapEntry
import com.jxd20072.automationapp.core.nodes.actionNodes
import com.jxd20072.automationapp.ui.home.getAutomationIconPainter
import com.jxd20072.automationapp.ui.shared.IconButton
import com.jxd20072.automationapp.ui.shared.IconLabelButton
import com.jxd20072.automationapp.ui.shared.Layer
import com.jxd20072.automationapp.ui.utils.getRelativeTime
import java.time.Instant

@Composable
fun EditorTopBar(
	automationData: AutomationsMapEntry,
	currentTime: Instant,
	modifier: Modifier = Modifier,
	returnHome: (saveChanges: Boolean) -> Unit,
	saveAutomation: () -> Unit,
	runAutomation: () -> Unit,
) {
	val elapsedTime =
		if (automationData.lastSaved != "")
			getRelativeTime(Instant.parse(automationData.lastSaved), currentTime)
		else null

	Column(
		verticalArrangement = Arrangement.spacedBy(10.dp),
		modifier = modifier
			.fillMaxWidth()
			.shadow(11.dp)
			.background(MaterialTheme.colorScheme.surface)
			.windowInsetsPadding(WindowInsets.statusBars)
			.padding(top = 20.dp)
			.padding(horizontal = 20.dp)
	) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween,
			modifier = Modifier
				.fillMaxWidth()
		) {
			Column(
				verticalArrangement = Arrangement.spacedBy(5.dp)
			) {
				Row(
					horizontalArrangement = Arrangement.spacedBy(10.dp),
					verticalAlignment = Alignment.CenterVertically
				) {
					Icon(
						painter = painterResource(R.drawable.round_arrow_back_24),
						contentDescription = "return to home screen",
						tint = MaterialTheme.colorScheme.onBackground,
						modifier = Modifier
							.size(22.dp)
							.clickable {
								returnHome(true)
							}
					)
					Icon(
						painter = getAutomationIconPainter(automationData.icon),
						contentDescription = "icon: ${automationData.icon}",
						tint = MaterialTheme.colorScheme.onBackground,
						modifier = Modifier.size(20.dp)
					)
					Text(
						text = automationData.name,
						color = MaterialTheme.colorScheme.onBackground,
						fontSize = 20.sp
					)
				}
				Text(
					text = "Last saved: ${elapsedTime ?: "never"}",
					color = MaterialTheme.colorScheme.onSurface,
					fontSize = 15.sp
				)
			}
			Row(
				horizontalArrangement = Arrangement.spacedBy(10.dp)
			) {
				IconButton(
					drawableRes = R.drawable.round_save_24,
					color = MaterialTheme.colorScheme.tertiary,
					iconColor = MaterialTheme.colorScheme.onSurface,
					padding = 6.dp,
					cornerRadius = 5.dp,
					onClick = saveAutomation,
					modifier = Modifier
						.size(45.dp),
				)

				IconButton(
					drawableRes = R.drawable.round_play_arrow_24,
					color = MaterialTheme.colorScheme.primary,
					iconColor = MaterialTheme.colorScheme.onPrimary,
					padding = 6.dp,
					cornerRadius = 5.dp,
					onClick = runAutomation,
					modifier = Modifier
						.size(45.dp)
				)
			}
		}
		Row(
			horizontalArrangement = Arrangement.spacedBy(10.dp)
		) {
			IconLabelButton(
				label = "Variables",
				fontSize = 15,
				drawableRes = R.drawable.round_data_object_24,
				color = MaterialTheme.colorScheme.tertiary,
				iconColor = MaterialTheme.colorScheme.onSecondary,
				cornerRadius = 5,
				elevation = 8,
				padding = 8,
				height = 18
			) { }

			IconLabelButton(
				label = "Logs",
				fontSize = 15,
				drawableRes = R.drawable.baseline_format_align_left_24,
				color = MaterialTheme.colorScheme.tertiary,
				iconColor = MaterialTheme.colorScheme.onSecondary,
				cornerRadius = 5,
				elevation = 8,
				padding = 8,
				height = 18
			) { }
		}
		Spacer(modifier = Modifier.height(5.dp))
	}
}

@Composable
fun AutomationEditor(
	editorViewModel: EditorViewModel = hiltViewModel(),
	returnHome: (saveChanges: Boolean) -> Unit
) {
	val editorUiState by editorViewModel.editorUiState.collectAsState()
	val currentTime by editorViewModel.currentTime.collectAsState()

	if (editorUiState is EditorUiState.Success) {
		val automationDraft = (editorUiState as EditorUiState.Success).automationDraft
		val automationData = (editorUiState as EditorUiState.Success).automationData

		Layer(
			modifier = Modifier
				.fillMaxSize()
				.background(MaterialTheme.colorScheme.background)
		) {
			Column {
				EditorTopBar(
					automationData = automationData,
					currentTime = currentTime,
					returnHome = returnHome,
					saveAutomation = { },
					runAutomation = { },
				)
				Column(
					horizontalAlignment = Alignment.CenterHorizontally,
					verticalArrangement = Arrangement.spacedBy(20.dp),
					modifier = Modifier
						.padding(horizontal = 20.dp)
						.verticalScroll(rememberScrollState(0))
				) {
					Spacer(modifier = Modifier.height(5.dp))

					if (automationDraft.entryPoint.isNotEmpty()) {
						TopLevelNode(
							type = TopLevelNodeType.ENTRYPOINT,
							name = "onExecute",
							displayName = listOf("On Start"),
							icon = R.drawable.round_play_arrow_24,
							content = automationDraft.entryPoint.map { actionNode ->
								{
									val actionData = actionNodes[actionNode.name]?.data
									if (actionData != null) {
										ChildNode(
											type = ChildNodeType.ACTION,
											name = actionData.displayName,
											displayName = actionData.editorActionDescription(actionNode.input),
											icon = actionData.icon
										)
									} else {
										Box {}
									}
									ChildNode(
										type = ChildNodeType.ACTION,
										name = "is this one smaller",
										displayName = listOf("is this one smaller"),
										icon = R.drawable.round_search_24
									)
								}
							}
						)
					}
					Spacer(modifier = Modifier.height(50.dp))
				}
			}
		}
	}
}