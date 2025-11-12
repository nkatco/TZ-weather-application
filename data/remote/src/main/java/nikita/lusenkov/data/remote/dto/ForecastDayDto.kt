package nikita.lusenkov.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ForecastDayDto(
    val date: String,
    val date_epoch: Long,
    val day: DayDto,
    val astro: AstroDto,
    val hour: List<HourDto>
)