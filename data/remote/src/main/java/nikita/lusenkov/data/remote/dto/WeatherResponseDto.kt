package nikita.lusenkov.data.remote.dto

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class WeatherResponseDto(
    val location: LocationDto,
    val current: CurrentDto,
    val forecast: ForecastDto
)