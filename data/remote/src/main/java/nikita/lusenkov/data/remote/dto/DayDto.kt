package nikita.lusenkov.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class DayDto(
    val maxtemp_c: Double,
    val maxtemp_f: Double,
    val mintemp_c: Double,
    val mintemp_f: Double,
    val avgtemp_c: Double? = null,
    val avgtemp_f: Double? = null,
    val maxwind_kph: Double? = null,
    val totalprecip_mm: Double? = null,
    val totalsnow_cm: Double? = null,
    val avghumidity: Int? = null,
    val daily_will_it_rain: Int? = null,
    val daily_chance_of_rain: Int? = null,
    val daily_will_it_snow: Int? = null,
    val daily_chance_of_snow: Int? = null,
    val condition: ConditionDto,
    val uv: Double? = null
)