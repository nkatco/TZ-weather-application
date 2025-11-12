package nikita.lusenkov.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class HourDto(
    val time_epoch: Long,
    val time: String,
    val temp_c: Double,
    val temp_f: Double? = null,
    val is_day: Int,
    val condition: ConditionDto,
    val wind_kph: Double? = null,
    val wind_mph: Double? = null,
    val wind_degree: Int? = null,
    val wind_dir: String? = null,
    val pressure_mb: Double? = null,
    val precip_mm: Double? = null,
    val humidity: Int? = null,
    val cloud: Int? = null,
    val feelslike_c: Double? = null,
    val windchill_c: Double? = null,
    val heatindex_c: Double? = null,
    val dewpoint_c: Double? = null,
    val will_it_rain: Int? = null,
    val chance_of_rain: Int? = null,
    val will_it_snow: Int? = null,
    val chance_of_snow: Int? = null,
    val vis_km: Double? = null,
    val gust_kph: Double? = null,
    val uv: Double? = null
)