package nikita.lusenkov.data.remote.datas

import javax.inject.Inject
import nikita.lusenkov.data.remote.api.WeatherApi
import nikita.lusenkov.data.remote.core.safeApiCall
import nikita.lusenkov.data.remote.dto.WeatherResponseDto

class WeatherRemoteDataSource @Inject constructor(
    private val api: WeatherApi
) {
    /**
     * Возвращает Result<WeatherResponseDto>:
     *  - success -> данные прогноза
     *  - failure -> NetworkError (Http/Network/Serialization/Unknown)
     */
    suspend fun forecastByCoords(
        lat: Double,
        lon: Double,
        days: Int = 3,
        lang: String = "ru"
    ): Result<WeatherResponseDto> = safeApiCall {
        api.getForecast(
            query = "$lat,$lon",
            days = days,
            lang = lang
        )
    }
}