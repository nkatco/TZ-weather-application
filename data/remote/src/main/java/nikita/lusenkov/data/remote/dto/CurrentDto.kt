package nikita.lusenkov.data.remote.dto

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class CurrentDto(
    val last_updated_epoch: Long,
    val last_updated: String,
    val temp_c: Double,
    val temp_f: Double,
    val is_day: Int,
    val condition: ConditionDto,
    val wind_mph: Double? = null,
    val wind_kph: Double? = null,
    val wind_degree: Int? = null,
    val wind_dir: String? = null,
    val pressure_mb: Double? = null,
    val pressure_in: Double? = null,
    val precip_mm: Double? = null,
    val precip_in: Double? = null,
    val humidity: Int? = null,
    val cloud: Int? = null,
    val feelslike_c: Double? = null,
    val feelslike_f: Double? = null,
    val windchill_c: Double? = null,
    val windchill_f: Double? = null,
    val heatindex_c: Double? = null,
    val heatindex_f: Double? = null,
    val dewpoint_c: Double? = null,
    val dewpoint_f: Double? = null,
    val vis_km: Double? = null,
    val vis_miles: Double? = null,
    val uv: Double? = null,
    val gust_mph: Double? = null,
    val gust_kph: Double? = null
)