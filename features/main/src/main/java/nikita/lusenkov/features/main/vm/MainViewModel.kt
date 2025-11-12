package nikita.lusenkov.features.main.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import nikita.lusenkov.domain.weather.GetForecastUseCase
import nikita.lusenkov.features.main.CityUi
import nikita.lusenkov.features.main.PeriodUi
import nikita.lusenkov.features.main.UiState
import nikita.lusenkov.features.main.toDomain

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getForecast: GetForecastUseCase
) : ViewModel() {

    private val cities = CityUi.entries

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    private var loadJob: Job? = null

    init { load() }

    fun nextCity() {
        val idx = cities.indexOf(_state.value.city)
        _state.update { it.copy(city = cities[(idx + 1) % cities.size]) }
        load()
    }

    fun prevCity() {
        val idx = cities.indexOf(_state.value.city)
        _state.update { it.copy(city = cities[(idx - 1 + cities.size) % cities.size]) }
        load()
    }

    fun setPeriod(period: PeriodUi) {
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

        loadJob = viewModelScope.launch {
            val start = System.currentTimeMillis()
            _state.update { it.copy(isLoading = true, error = null) }

            getForecast(city.id, period.toDomain()).collect { result ->
                val remain = (minLoadingMillis - (System.currentTimeMillis() - start)).coerceAtLeast(0L)
                if (remain > 0) delay(remain)

                _state.update {
                    if (result.isSuccess)
                        it.copy(isLoading = false, forecast = result.getOrThrow(), error = null)
                    else
                        it.copy(isLoading = false, error = result.exceptionOrNull()?.message ?: "Ошибка загрузки")
                }
            }
        }
    }
}