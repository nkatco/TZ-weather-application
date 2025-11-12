package nikita.lusenkov.data.remote.dto

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class AstroDto(
    val sunrise: String? = null,
    val sunset: String? = null,
    val moonrise: String? = null,
    val moonset: String? = null,
    val moon_phase: String? = null,
    val moon_illumination: Int? = null,
    val is_moon_up: Int? = null,
    val is_sun_up: Int? = null
)