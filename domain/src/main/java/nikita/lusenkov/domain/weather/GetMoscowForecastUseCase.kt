package nikita.lusenkov.domain.weather

import javax.inject.Inject

class GetMoscowForecastUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    operator fun invoke() = repository.getForecast(
        lat = 55.7569, lon = 37.6151, days = 3, lang = "ru"
    )
}