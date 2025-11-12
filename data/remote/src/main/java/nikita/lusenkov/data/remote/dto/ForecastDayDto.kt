package nikita.lusenkov.data.remote.dto

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class ForecastDayDto(
    val date: String,
    val date_epoch: Long,
    val day: DayDto,
    val astro: AstroDto,
    val hour: List<HourDto>
)