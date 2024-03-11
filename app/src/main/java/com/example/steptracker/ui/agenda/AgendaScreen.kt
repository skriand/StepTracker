package com.example.steptracker.ui.agenda

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.records.StepsRecord
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.steptracker.R
import com.example.steptracker.ui.ViewModelProvider
import com.example.steptracker.ui.home.HomeBody
import com.example.steptracker.ui.navigation.NavigationDestination
import java.time.LocalDateTime

object AgendaDestination : NavigationDestination {
    override val route = "agenda"
    override val titleResourceId: Int = R.string.agenda
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaScreen(
    isSinglePane: Boolean,
    modifier: Modifier = Modifier,
    viewModel: AgendaViewModel = viewModel(factory = ViewModelProvider.Factory)
) {
    viewModel.updateAggregationData()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    if (isSinglePane) {
        AgendaBody(
            isSinglePane = true,
            dailySteps = viewModel.dailySteps.value,
            dayRecords = viewModel.dayRecords.value
        )
    } else
        Scaffold(
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                LargeTopAppBar(
                    title = { Text(stringResource(AgendaDestination.titleResourceId)) },
                    scrollBehavior = scrollBehavior,
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { viewModel.addData() },
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add)
                    )
                }
            },
        ) { innerPadding ->
            AgendaBody(
                isSinglePane = false,
                dailySteps = viewModel.dailySteps.value,
                dayRecords = viewModel.dayRecords.value,
                modifier = Modifier.padding(innerPadding)
            )
        }
}

@Composable
fun AgendaBody(
    isSinglePane: Boolean,
    dailySteps: List<Pair<LocalDateTime, Long?>>,
    dayRecords: List<List<StepsRecord>>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        LazyColumn(
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            if (isSinglePane) {
                item {
                    HomeBody(stepsRecords = if (dayRecords.isNotEmpty()) dayRecords.last() else listOf())
                }
                item {
                    Text(
                        stringResource(R.string.agenda),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            itemsIndexed(items = dailySteps) { index, item ->
                Row {
                    Column {
                        Column {
                            Text(
                                text = item.first.month.toString(),
                                style = MaterialTheme.typography.labelSmall
                            )
                            Text(
                                text = item.first.dayOfMonth.toString(),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Column {
                            Text(
                                text = stringResource(id = R.string.steps).uppercase(),
                                style = MaterialTheme.typography.labelSmall
                            )
                            Text(
                                text = item.second.toString(),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Column(modifier = Modifier.padding(start = 16.dp)) {
                        if (index == 0) Text(
                            stringResource(id = R.string.no_data),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (index < dayRecords.size && dayRecords.isNotEmpty())
                            dayRecords[index].forEach {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp)
                                ) {
                                    Text(
                                        text = "time",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(
                                            top = 8.dp,
                                            start = 8.dp,
                                            end = 8.dp
                                        )
                                    )
                                    Text(
                                        text = it.count.toString(),
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(
                                            bottom = 8.dp,
                                            start = 8.dp,
                                            end = 8.dp
                                        )
                                    )
                                }
                            }
                    }
                }
            }
        }
    }
}