package nikita.lusenkov.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ForecastDto(
    val forecastday: List<ForecastDayDto>
)