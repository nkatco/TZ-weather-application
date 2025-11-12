package nikita.lusenkov.features.main

import nikita.lusenkov.domain.weather.Forecast

data class UiState(
    val city: CityUi = CityUi.Moscow,
    val period: PeriodUi = PeriodUi.Today,
    val isLoading: Boolean = true,
    val forecast: Forecast? = null,
    val error: String? = null
)