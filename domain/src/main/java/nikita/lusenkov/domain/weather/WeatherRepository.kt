package nikita.lusenkov.domain.weather

import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun getForecast(
        lat: Double,
        lon: Double,
        days: Int = 3,
        lang: String = "ru"
    ): Flow<Result<Forecast>>
}