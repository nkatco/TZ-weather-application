package nikita.lusenkov.feature.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import nikita.lusenkov.domain.weather.Forecast
import nikita.lusenkov.features.main.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Погода (Москва)") },
                actions = {
                    TextButton(onClick = { viewModel.refresh() }) {
                        Text("Обновить")
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (val s = state) {
                is MainViewModel.UiState.Loading -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
                is MainViewModel.UiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Ошибка: ${s.message}")
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = { viewModel.refresh() }) {
                            Text("Повторить")
                        }
                    }
                }
                is MainViewModel.UiState.Success -> {
                    ForecastContent(s.data, onRefresh = { viewModel.refresh() })
                }
            }
        }
    }
}

@Composable
private fun ForecastContent(
    forecast: Forecast,
    onRefresh: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                "${forecast.locationName}, ${forecast.country}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text("Локальное время: ${forecast.localtime}")
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Сейчас: ${forecast.tempC}°C, ${forecast.conditionText}",
                style = MaterialTheme.typography.titleMedium
            )
        }

        item {
            Divider()
            Text("Прогноз на 3 дня", style = MaterialTheme.typography.titleMedium)
        }

        items(forecast.days) { d ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text(d.date, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(4.dp))
                    Text("Макс: ${d.maxTempC}°C • Мин: ${d.minTempC}°C")
                    Text(d.conditionText)
                }
            }
        }

        item { Spacer(Modifier.height(24.dp)) }
        item {
            OutlinedButton(onClick = onRefresh, modifier = Modifier.fillMaxWidth()) {
                Text("Обновить")
            }
        }
    }
}