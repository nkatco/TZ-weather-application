package nikita.lusenkov.features.main

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nikita.lusenkov.domain.weather.Forecast
import nikita.lusenkov.domain.weather.GetKzForecastUseCase
import nikita.lusenkov.domain.weather.GetMoscowForecastUseCase
import nikita.lusenkov.domain.weather.GetSpbForecastUseCase
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getSpbForecast: GetSpbForecastUseCase,
    private val getKzForecast: GetKzForecastUseCase,
    private val getMoscowForecast: GetMoscowForecastUseCase,
) : ViewModel() {

    enum class City(
        val title: String,
        @DrawableRes val iconRes: Int
    ) {
        Moscow("Москва", R.drawable.moscow),
        Spb("Санкт-Петербург", R.drawable.spb),
        Kz("Алматы", R.drawable.kz)
    }

    enum class Period { Current, Today, Three }

    data class UiState(
        val city: City = City.Moscow,
        val period: Period = Period.Today,
        val isLoading: Boolean = true,
        val forecast: Forecast? = null,
        val error: String? = null
    )

    private val cities = City.entries
    private var loadJob: Job? = null

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    init { load() }

    fun nextCity() {
        val idx = cities.indexOf(_state.value.city)
        val next = cities[(idx + 1) % cities.size]
        _state.update { it.copy(city = next) }
        load()
    }


    fun prevCity() {
        val idx = cities.indexOf(_state.value.city)
        val prev = cities[(idx - 1 + cities.size) % cities.size]
        _state.update { it.copy(city = prev) }
        load()
    }


    fun setPeriod(period: Period) {
        if (period != _state.value.period) {
            _state.update { it.copy(period = period) }
            load()
        }
    }

    fun refresh(minLoadingMillis: Long = 0L) = load(minLoadingMillis)
    fun retry() = load(minLoadingMillis = 500L)

    private fun load(minLoadingMillis: Long = 0L) {
        loadJob?.cancel()
        val city = _state.value.city
        val period = _state.value.period


        val apiDays = when (period) {
            Period.Current, Period.Today -> 1
            Period.Three -> 3
        }


        loadJob = viewModelScope.launch {
            val start = System.currentTimeMillis()
            _state.update { it.copy(isLoading = true, error = null) }


            val flow = when (city) {
                City.Moscow -> getMoscowForecast(apiDays)
                City.Spb -> getSpbForecast(apiDays)
                City.Kz -> getKzForecast(apiDays)
            }


            flow.collect { result ->
                val elapsed = System.currentTimeMillis() - start
                val remain = (minLoadingMillis - elapsed).coerceAtLeast(0L)
                if (remain > 0) delay(remain)


                if (result.isSuccess) {
                    _state.update { it.copy(isLoading = false, forecast = result.getOrThrow(), error = null) }
                } else {
                    _state.update { it.copy(isLoading = false, error = result.exceptionOrNull()?.message ?: "Ошибка загрузки") }
                }
            }
        }
    }
}