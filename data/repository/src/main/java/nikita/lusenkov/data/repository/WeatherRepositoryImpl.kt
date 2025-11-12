package nikita.lusenkov.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import nikita.lusenkov.data.local.datas.WeatherCacheLocalDataSource
import nikita.lusenkov.data.remote.datas.WeatherRemoteDataSource
import nikita.lusenkov.data.remote.dto.WeatherResponseDto
import nikita.lusenkov.domain.weather.Current
import nikita.lusenkov.domain.weather.Day
import nikita.lusenkov.domain.weather.Forecast
import nikita.lusenkov.domain.weather.Hour
import nikita.lusenkov.domain.weather.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val remote: WeatherRemoteDataSource,
    private val local: WeatherCacheLocalDataSource
) : WeatherRepository {

    private val json = Json { ignoreUnknownKeys = true; explicitNulls = false }

    private fun cacheKey(lat: Double, lon: Double, days: Int, lang: String) =
        "forecast?q=$lat,$lon&days=$days&lang=$lang&aqi=no&alerts=no"

    override fun getForecast(
        lat: Double,
        lon: Double,
        days: Int,
        lang: String
    ): Flow<Result<Forecast>> = flow {
        val key = cacheKey(lat, lon, days, lang)

        val cachedPair = local.getRawOrNull(key)
        var emittedFromCache = false
        cachedPair?.let { (payload, _) ->
            runCatching {
                json.decodeFromString(WeatherResponseDto.serializer(), payload)
                    .toDomain()
            }.onSuccess { cached ->
                emit(Result.success(cached))
                emittedFromCache = true
            }
        }

        val net: Result<WeatherResponseDto> =
            remote.forecastByCoords(lat, lon, days = days, lang = lang)

        if (net.isSuccess) {
            val freshDto = net.getOrThrow()
            val freshDomain = freshDto.toDomain()

            val newJson = runCatching {
                json.encodeToString(WeatherResponseDto.serializer(), freshDto)
            }.getOrNull()
            val oldJson = cachedPair?.first
            val changed = newJson != null && newJson != oldJson

            if (changed) {
                local.put(key, newJson!!)
                emit(Result.success(freshDomain))
            } else if (!emittedFromCache) {
                emit(Result.success(freshDomain))
            }
        } else {
            if (!emittedFromCache) {
                emit(Result.failure(net.exceptionOrNull()!!))
            }
        }
    }
}

/** DTO → Domain */
private fun WeatherResponseDto.toDomain(): Forecast =
    Forecast(
        locationName = location.name,
        country = location.country,
        localtime = location.localtime,
        tempC = current.temp_c,
        conditionText = current.condition.text,
        days = forecast.forecastday.map {
            Day(
                date = it.date,
                maxTempC = it.day.maxtemp_c,
                minTempC = it.day.mintemp_c,
                conditionText = it.day.condition.text
            )
        },
        current = Current(
            tempC = current.temp_c,
            feelsLikeC = current.feelslike_c,
            windKph = current.wind_kph,
            humidity = current.humidity,
            pressureMb = current.pressure_mb,
            uv = current.uv,
            conditionText = current.condition.text
        ),
        todayHours = forecast.forecastday.firstOrNull()?.hour?.map {
            Hour(
                time = it.time,
                tempC = it.temp_c,
                conditionText = it.condition.text
            )
        } ?: emptyList()
    )