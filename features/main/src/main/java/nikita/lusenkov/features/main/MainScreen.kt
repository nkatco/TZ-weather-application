package nikita.lusenkov.features.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    val ptrState = rememberPullToRefreshState()
    val refreshing = state.isLoading

    Box(Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Погода") },
                    actions = {
                        DaysMenuAction(
                            selected = state.period,
                            onPick = { selected ->
                                when (selected) {
                                    MainViewModel.Period.Current -> viewModel.setPeriod(MainViewModel.Period.Current)
                                    MainViewModel.Period.Today -> viewModel.setPeriod(MainViewModel.Period.Today)
                                    MainViewModel.Period.Three -> viewModel.setPeriod(MainViewModel.Period.Three)
                                }
                            }
                        )
                    }
                )
            }
        ) { padding ->

            PullToRefreshBox(
                isRefreshing = refreshing,
                onRefresh = { viewModel.refresh() },
                state = ptrState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                indicator = {}
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        TopCitySection(
                            iconRes = state.city.iconRes,
                            cityTitle = state.city.title,
                            tempNow = state.forecast?.tempC,
                            condNow = state.forecast?.conditionText,
                            onPrev = { viewModel.prevCity() },
                            onNext = { viewModel.nextCity() }
                        )
                    }

                    item {
                        Divider()
                        Text(
                            text = when (state.period) {
                                MainViewModel.Period.Current -> "Текущая погода"
                                MainViewModel.Period.Today -> "Сегодня: почасовой прогноз"
                                MainViewModel.Period.Three -> "Прогноз на 3 дня"
                            },
                            style = MaterialTheme.typography.titleMedium
                        )
                    }


                    when {
                        state.isLoading && state.forecast == null -> {
                            item { LoadingBlock() }
                        }
                        state.error != null && state.forecast == null -> {
                            item { ErrorBlock(message = state.error!!, onRetry = { viewModel.retry() }, modifier = Modifier.fillMaxWidth()) }
                        }
                        else -> {
                            val f = state.forecast
                            if (f != null) {
                                when (state.period) {
                                    MainViewModel.Period.Current -> {
                                        item { CurrentBlock(f) }
                                    }
                                    MainViewModel.Period.Today -> {
                                        if (f.todayHours.isEmpty()) {
                                            item { EmptyBlock("Нет данных по часам") }
                                        } else {
                                            items(f.todayHours) { h ->
                                                HourRow(h)
                                            }
                                        }
                                    }
                                    MainViewModel.Period.Three -> {
                                        items(f.days) { d ->
                                            DayCard(d)
                                        }
                                    }
                                }
                            } else if (!state.isLoading) {
                                item { ErrorBlock(message = state.error ?: "Данных нет", onRetry = { viewModel.retry() }, modifier = Modifier.fillMaxWidth()) }
                            }
                        }
                    }

                    item { Spacer(Modifier.height(12.dp)) }
                }
            }
        }

        PullToRefreshDefaults.Indicator(
            state = ptrState,
            isRefreshing = refreshing,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .zIndex(2f)
        )

        if (state.isLoading && state.forecast != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(3f),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        }
    }
}

@Composable
private fun LoadingBlock() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) { CircularProgressIndicator() }
}

@Composable
private fun DayCard(d: nikita.lusenkov.domain.weather.Day) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Text(d.date, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text("Макс: ${d.maxTempC}°C • Мин: ${d.minTempC}°C")
            Text(d.conditionText)
        }
    }
}

@Composable
private fun CurrentBlock(f: nikita.lusenkov.domain.weather.Forecast) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Text("${f.current.conditionText}", fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(6.dp))
            Text("Температура: ${f.current.tempC}°C")
            f.current.feelsLikeC?.let { Text("Ощущается как: ${it}°C") }
            Row(Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                f.current.windKph?.let { Text("Ветер: ${it} км/ч") }
                f.current.humidity?.let { Text("Влажн.: ${it}%") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                f.current.pressureMb?.let { Text("Давление: ${it} мбар") }
                f.current.uv?.let { Text("UV: ${it}") }
            }
        }
    }
}
@Composable
private fun HourRow(h: nikita.lusenkov.domain.weather.Hour) {
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(h.time.substringAfter(' '), fontWeight = FontWeight.SemiBold)
                Text(h.conditionText)
            }
            Text("${h.tempC}°C")
        }
    }
}
@Composable
private fun EmptyBlock(message: String) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) { Text(message) }
}

@Composable
private fun TopCitySection(
    iconRes: Int,
    cityTitle: String,
    tempNow: Double?,
    condNow: String?,
    onPrev: () -> Unit,
    onNext: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = onPrev) {
                Icon(Icons.Filled.KeyboardArrowLeft, contentDescription = "Предыдущий город")
            }
            Image(
                painter = painterResource(iconRes),
                contentDescription = cityTitle,
                modifier = Modifier
                    .size(160.dp)
                    .padding(horizontal = 8.dp)
            )
            IconButton(onClick = onNext) {
                Icon(Icons.Filled.KeyboardArrowRight, contentDescription = "Следующий город")
            }
        }
        Text(cityTitle, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        if (tempNow != null && condNow != null) {
            Spacer(Modifier.height(4.dp))
            Text("Сейчас: ${tempNow}°C, ${condNow}", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun DaysMenuAction(
    selected: MainViewModel.Period,
    onPick: (MainViewModel.Period) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }


    val label = when (selected) {
        MainViewModel.Period.Current -> "Текущая"
        MainViewModel.Period.Today -> "Сегодня"
        MainViewModel.Period.Three -> "3 дня"
    }


    Box {
        TextButton(onClick = { expanded = true }) { Text("Период: $label") }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(text = { Text("Текущая") }, onClick = { expanded = false; onPick(MainViewModel.Period.Current) })
            DropdownMenuItem(text = { Text("Сегодня") }, onClick = { expanded = false; onPick(MainViewModel.Period.Today) })
            DropdownMenuItem(text = { Text("3 дня") }, onClick = { expanded = false; onPick(MainViewModel.Period.Three) })
        }
    }
}

@Composable
private fun ErrorBlock(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Ошибка: $message")
        Spacer(Modifier.height(12.dp))
        Button(onClick = onRetry) { Text("Повторить") }
    }
}