package com.jxd20072.automationapp.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.ui.settings.categories.GeneralSettingsCategory
import com.jxd20072.automationapp.ui.shared.components.AppScreen
import com.jxd20072.automationapp.ui.shared.components.SettingsResetButton
import com.jxd20072.automationapp.ui.shared.components.clickableWithIndication
import com.jxd20072.automationapp.ui.theme.LocalTheme

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    returnHome: () -> Unit
) {
    val settingsUiState by
    settingsViewModel.userSettingsUiState.collectAsStateWithLifecycle()

    AppScreen {
        Column(
            verticalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel1),
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(top = LocalTheme.layoutSystem.spacing.paddingLevel1 * 2)
                .padding(horizontal = LocalTheme.layoutSystem.spacing.paddingLevel1),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel2)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.round_arrow_back_24),
                        contentDescription = "back",
                        tint = LocalTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .size(28.dp)
                            .clickableWithIndication { returnHome() }
                    )
                    Text(
                        text = "Settings",
                        color = LocalTheme.colorScheme.onBackground,
                        style = LocalTheme.typography.title
                    )
                }
                SettingsResetButton(settingsViewModel::resetSettingsToDefault)
            }
            if (settingsUiState is UserSettingsUiState.Success) {
                val settings = settingsUiState as UserSettingsUiState.Success

                Column(
                    verticalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel1),
                    modifier = Modifier
                        .verticalScroll(rememberScrollState(0))
                ) {
                    GeneralSettingsCategory(
                        userSettingsUiState = settings,
                        onAutoSaveChanged = settingsViewModel::setAutoSaveEnabled,
                        onAutoOpenKeyboardChanged = settingsViewModel::setAutoOpenKeyboard,
                    )
                }
            }
        }
    }
}