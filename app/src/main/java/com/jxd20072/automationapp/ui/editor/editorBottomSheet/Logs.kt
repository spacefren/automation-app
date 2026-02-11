package com.jxd20072.automationapp.ui.editor.editorBottomSheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.wear.compose.material.Text
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.core.data.AutomationLogEntry
import com.jxd20072.automationapp.core.data.LogLevel
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.EditorSession
import com.jxd20072.automationapp.ui.editor.viewmodel.managers.LogsViewModel
import com.jxd20072.automationapp.ui.shared.components.AppScreen
import com.jxd20072.automationapp.ui.shared.components.IconButton
import com.jxd20072.automationapp.ui.shared.components.SearchBar
import com.jxd20072.automationapp.ui.shared.components.SwipeableContainer
import com.jxd20072.automationapp.ui.theme.LocalTheme
import com.jxd20072.automationapp.ui.theme.NestedLevel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Composable
fun LogEntry(
    entry: AutomationLogEntry,
    modifier: Modifier = Modifier,
    deleteLog: () -> Unit,
) {
    SwipeableContainer(
        icon1 = R.drawable.round_delete_24,
        icon2 = R.drawable.round_delete_24,
        backgroundColor1 = LocalTheme.colorScheme.errorSecondary,
        backgroundColor2 = LocalTheme.colorScheme.errorSecondary,
        onAction1 = deleteLog,
        onAction2 = deleteLog,
        containerCornerRadius = LocalTheme.layoutSystem.shapes.cornerRadiusLevel2,
        modifier = modifier,
    ) {
        val dateTime = remember {
            LocalDateTime
                .ofInstant(Instant.parse(entry.time), ZoneOffset.systemDefault())
        }
        val timeNow = LocalDateTime.now()

        val format = (if (timeNow.year == dateTime.year && timeNow.dayOfYear == dateTime.dayOfYear)
            "" else "yyyy/MM/dd @ ") + "HH:mm:ss"
        val timestamp = dateTime.format(DateTimeFormatter.ofPattern(format))

        Text(
            text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        color = LocalTheme.colorScheme.onSurface
                    )
                ) {
                    append("[$timestamp] ")
                }
                append(entry.message)
            },
            style = LocalTheme.typography.body.copy(
                fontFamily = FontFamily.Monospace
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(LocalTheme.layoutSystem.shapes.cornerRadiusLevel2))
                .background(
                    when (entry.level) {
                        LogLevel.ERROR -> LocalTheme.colorScheme.errorContainer
                        LogLevel.WARNING -> LocalTheme.colorScheme.warningContainer
                        else -> LocalTheme.colorScheme.surface
                    }
                )
                .padding(LocalTheme.layoutSystem.spacing.paddingLevel2)
        )
    }
}

@Composable
fun EditorLogsScreen(
    editorSession: EditorSession,
    logsViewModel: LogsViewModel = hiltViewModel<LogsViewModel, LogsViewModel.Factory>(
        creationCallback = { factory -> factory.create(editorSession) }
    )
) {
    val logsScreenState by logsViewModel.logsScreenState.collectAsState()
    val filteredLogs by logsViewModel.filteredLogs.collectAsState(emptyList())

    AppScreen {
        Column(
            verticalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel1),
            modifier = Modifier
                .fillMaxSize()
                .padding(LocalTheme.layoutSystem.spacing.paddingLevel1)
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            Text(
                text = "Logs",
                style = LocalTheme.typography.subTitle,
                color = LocalTheme.colorScheme.onBackground
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel2),
            ) {
                SearchBar(
                    searchText = "Search logs",
                    queryText = logsScreenState.searchQuery,
                    autoFocus = false,
                    onSearch = logsViewModel::searchLogs,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    color = LocalTheme.colorScheme.surface,
                    iconColor = LocalTheme.colorScheme.onSurface,
                    drawableRes = R.drawable.round_filter_24,
                    nestedLevel = NestedLevel.ONE,
                    modifier = Modifier.size(37.dp),
                ) {

                }
            }
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(LocalTheme.layoutSystem.spacing.paddingLevel2),
                contentPadding = PaddingValues(bottom = LocalTheme.layoutSystem.spacing.paddingLevel1),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredLogs, { it.id }) {
                    LogEntry(
                        entry = it,
                        deleteLog = {
                            logsViewModel.deleteLog(it.id)
                        },
                        modifier = Modifier
                            .animateItem()
                    )
                }
            }
        }
    }
}