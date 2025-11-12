package nikita.lusenkov.domain.weather

data class Current(
    val tempC: Double,
    val feelsLikeC: Double?,
    val windKph: Double?,
    val humidity: Int?,
    val pressureMb: Double?,
    val uv: Double?,
    val conditionText: String
)


data class Hour(
    val time: String,
    val tempC: Double,
    val conditionText: String
)


data class Day(
    val date: String,
    val maxTempC: Double,
    val minTempC: Double,
    val conditionText: String
)


data class Forecast(
    val locationName: String,
    val country: String,
    val localtime: String,
    val tempC: Double,
    val conditionText: String,
    val days: List<Day>,
    val current: Current,
    val todayHours: List<Hour>
)