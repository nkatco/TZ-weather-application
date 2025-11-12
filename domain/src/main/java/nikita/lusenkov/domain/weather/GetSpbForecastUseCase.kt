package nikita.lusenkov.domain.weather

import javax.inject.Inject

class GetSpbForecastUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    operator fun invoke(days: Int = 3) = repository.getForecast(
        lat = 59.937500, lon = 30.308611, days = days, lang = "ru"
    )
}