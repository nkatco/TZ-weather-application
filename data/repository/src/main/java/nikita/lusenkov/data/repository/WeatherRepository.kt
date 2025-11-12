package nikita.lusenkov.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import nikita.lusenkov.data.local.datas.WeatherCacheLocalDataSource
import nikita.lusenkov.data.remote.datas.WeatherRemoteDataSource
import nikita.lusenkov.data.remote.dto.WeatherResponseDto
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val remote: WeatherRemoteDataSource,
    private val local: WeatherCacheLocalDataSource
) {
    // общий Json-конфиг, как в DI
    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    private fun cacheKey(
        lat: Double,
        lon: Double,
        days: Int,
        lang: String = "ru"
    ) = "forecast?q=$lat,$lon&days=$days&lang=$lang&aqi=no&alerts=no"

    /**
     * Stale-while-revalidate:
     * 1) отдаем, если есть, кэш (любой давности),
     * 2) всегда идём в сеть; при успехе — кладём в кэш и эмитим обновление,
     * 3) если сети нет и кэша не было — эмитим failure.
     */
    fun getMoscow3Day(): Flow<Result<WeatherResponseDto>> = flow {
        val key = cacheKey(55.7569, 37.6151, 3, "ru")

        // 1) быстрый кэш
        val cachedPair = local.getRawOrNull(key)
        var emittedFromCache = false
        cachedPair?.let { (payload, _) ->
            runCatching { json.decodeFromString<WeatherResponseDto>(payload) }
                .onSuccess { cached ->
                    emit(Result.success(cached))
                    emittedFromCache = true
                }
        }

        // 2) сеть
        when (val net = remote.forecastByCoords(55.7569, 37.6151, days = 3, lang = "ru")) {
            is Result.Success -> {
                val fresh = net.getOrNull()!!
                val newJson = runCatching { json.encodeToString(fresh) }.getOrNull()
                val oldJson = cachedPair?.first
                val changed = newJson != null && newJson != oldJson
                if (changed) {
                    local.put(key, newJson!!)
                    emit(Result.success(fresh))
                } else if (!emittedFromCache) {
                    emit(Result.success(fresh))
                }
            }
            is Result.Failure -> {
                if (!emittedFromCache) {
                    emit(Result.failure(net.exceptionOrNull()!!))
                }
            }
        }
    }
}