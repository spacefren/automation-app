package com.jxd20072.automationapp.ui.home

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jxd20072.automationapp.R
import com.jxd20072.automationapp.ui.shared.SearchBar
import java.time.LocalDateTime

@Composable
fun TopBar(
	modifier: Modifier = Modifier
) {
	Column(
		verticalArrangement = Arrangement.spacedBy(15.dp),
		modifier = modifier
			.padding(top = 30.dp)
	) {

		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween,
			modifier = Modifier
				.fillMaxWidth()
		) {
			Text(
				text = "Home",
				color = MaterialTheme.colorScheme.onBackground,
				fontSize = 32.sp
			)

			Box {
				Icon(
					painter = painterResource(R.drawable.settings),
					contentDescription = "settings",
					modifier = Modifier
						.size(28.dp)
				)
			}
		}
	}
}

@Composable
private fun SearchBar(modifier: Modifier = Modifier) {
	SearchBar(
		searchText = "Search for automations",
		modifier = modifier
			.fillMaxWidth()
			.height(45.dp)
	)
}

@Composable
private fun AutomationsList(modifier: Modifier = Modifier) {
	Column(
		modifier = modifier
        .fillMaxHeight()
        .verticalScroll(state = ScrollState(0))
        .padding(bottom = 300.dp),

		verticalArrangement = Arrangement.spacedBy(10.dp)
	) {
		for (i in 1..100) {
			AutomationCard(
				name = "Automation $i",
				icon = R.drawable.function,
				lastUsed = LocalDateTime.now(),
			)
		}
	}
}

@Composable
fun HomeScreen(
	viewModel: HomeViewModel = viewModel(),
	modifier: Modifier
) {
	val sidePadding = PaddingValues(horizontal = 20.dp)
	val homeUiState by viewModel.state.collectAsState()

	Box(
		modifier = modifier
        .background(MaterialTheme.colorScheme.background)
        .fillMaxSize()
	) {
		Column(
			verticalArrangement = Arrangement.spacedBy(15.dp)
		) {
			TopBar(modifier = Modifier.padding(sidePadding))

			SearchBar(modifier = Modifier.padding(sidePadding))

			FilterChips(
				filters = homeUiState.filters,
				canEdit = true,
				sidePadding = sidePadding,
				onFilterChange = { filter, enabled ->
					viewModel.onFilterChanged(filter, enabled)
				}
			)

			AutomationsList(modifier = Modifier.padding(sidePadding))
		}

		FloatingNewButton(modifier = Modifier.align(Alignment.BottomEnd))
	}
}