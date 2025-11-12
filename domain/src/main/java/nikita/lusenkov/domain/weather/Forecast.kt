package nikita.lusenkov.domain.weather

data class Forecast(
    val locationName: String,
    val country: String,
    val localtime: String,
    val tempC: Double,
    val conditionText: String,
    val days: List<Day>
)

data class Day(
    val date: String,
    val maxTempC: Double,
    val minTempC: Double,
    val conditionText: String
)