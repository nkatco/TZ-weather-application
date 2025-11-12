package nikita.lusenkov.features.main

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import kotlin.math.roundToInt
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val Ru = Locale("ru")
@RequiresApi(Build.VERSION_CODES.O)
private val inDateFmt = DateTimeFormatter.ISO_LOCAL_DATE
@RequiresApi(Build.VERSION_CODES.O)
private val inDateTimeFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Ru)
@RequiresApi(Build.VERSION_CODES.O)
private val outDayFmt = DateTimeFormatter.ofPattern("d MMMM", Ru)
@RequiresApi(Build.VERSION_CODES.O)
private val outHourFmt = DateTimeFormatter.ofPattern("HH:mm", Ru)

@RequiresApi(Build.VERSION_CODES.O)
private fun prettyDayLabel(dateStr: String): String {
    return runCatching {
        val d = LocalDate.parse(dateStr, inDateFmt)
        val today = LocalDate.now()
        val string = when {
            d == today -> "Сегодня"
            d == today.plusDays(1) -> "Завтра"
            else -> outDayFmt.format(d).replaceFirstChar { it.titlecase(Ru) }
        }
        string
    }.getOrElse { dateStr }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun prettyHourLabel(timeStr: String): String {
    return runCatching {
        val dt = LocalDateTime.parse(timeStr, inDateTimeFmt)
        outHourFmt.format(dt) // "14:00"
    }.getOrElse { timeStr.substringAfter(' ') }
}

@RequiresApi(Build.VERSION_CODES.O)
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
            containerColor = Color(0xFFF1F4FD),
            topBar = {
                TopAppBar(
                    title = { Text("Погода") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White,
                        scrolledContainerColor = Color.White,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                        actionIconContentColor = MaterialTheme.colorScheme.primary
                    ),
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
                        Text(
                            text = when (state.period) {
                                MainViewModel.Period.Current -> "Текущая погода"
                                MainViewModel.Period.Today -> "Сегодня: почасовой прогноз"
                                MainViewModel.Period.Three -> "Прогноз на 3 дня"
                            },
                            style = MaterialTheme.typography.titleLarge
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DayCard(d: nikita.lusenkov.domain.weather.Day) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Bottom) {
                Column(Modifier.weight(1f)) {
                    Text(prettyDayLabel(d.date), style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        d.conditionText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.Bottom) {
                    Text(
                        "${d.maxTempC.roundToInt()}°",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "/ ${d.minTempC.roundToInt()}°",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun CurrentBlock(f: nikita.lusenkov.domain.weather.Forecast) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(f.locationName, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "${f.current.tempC.roundToInt()}°",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(f.current.conditionText, style = MaterialTheme.typography.titleMedium)
                        f.current.feelsLikeC?.let {
                            Text("Ощущается: ${it.roundToInt()}°C", style = MaterialTheme.typography.bodyMedium)
                        }
                        Text(
                            "Обновлено: ${f.localtime.substringAfter(' ')}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(title = "Ветер", value = f.current.windKph?.let { "${it} км/ч" } ?: "—", modifier = Modifier.weight(1f))
            StatCard(title = "Влажность", value = f.current.humidity?.let { "${it}%" } ?: "—", modifier = Modifier.weight(1f))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(title = "Давление", value = f.current.pressureMb?.let { "${it} мбар" } ?: "—", modifier = Modifier.weight(1f))
            StatCard(title = "UV", value = f.current.uv?.let { "$it" } ?: "—", modifier = Modifier.weight(1f))
        }

        if (f.todayHours.isNotEmpty()) {
            Text("Ближайшие часы", style = MaterialTheme.typography.titleMedium)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(f.todayHours.take(8)) { h ->
                    HourMiniCard(h)
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(6.dp))
            Text(
                value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun HourMiniCard(h: nikita.lusenkov.domain.weather.Hour) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.width(88.dp)
    ) {
        Column(
            Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                h.time.substringAfter(' '),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "${h.tempC.roundToInt()}°C",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun HourRow(h: nikita.lusenkov.domain.weather.Hour) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    prettyHourLabel(h.time),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    h.conditionText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                "${h.tempC.roundToInt()}°C",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
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
                    .size(240.dp)
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
        TextButton(
            onClick = {
                expanded = true
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) { Text("Период: $label") }
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
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) { Text("Повторить") }
    }
}