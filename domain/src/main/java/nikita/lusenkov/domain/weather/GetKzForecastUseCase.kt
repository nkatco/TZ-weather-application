package nikita.lusenkov.domain.weather

import javax.inject.Inject

class GetKzForecastUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    operator fun invoke(days: Int = 3) = repository.getForecast(
        lat = 55.796391, lon = 49.108891, days = days, lang = "ru"
    )
}