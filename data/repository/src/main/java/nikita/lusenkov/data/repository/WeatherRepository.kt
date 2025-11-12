package nikita.lusenkov.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import nikita.lusenkov.data.local.datas.WeatherCacheLocalDataSource
import nikita.lusenkov.data.remote.datas.WeatherRemoteDataSource
import nikita.lusenkov.data.remote.dto.WeatherResponseDto
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val remote: WeatherRemoteDataSource,
    private val local: WeatherCacheLocalDataSource
) {
    private val adapter = moshi.adapter(WeatherResponseDto::class.java)

    private fun cacheKey(
        lat: Double,
        lon: Double,
        days: Int,
        lang: String = "ru"
    ) = "forecast?q=$lat,$lon&days=$days&lang=$lang&aqi=no&alerts=no"

    /**
     * Stale-while-revalidate:
     * 1) Эмитим кэш, если есть (неважно свежий или нет — чтобы быстро показать данные).
     * 2) Всегда идём в сеть; при успехе — перезаписываем кэш и эмитим обновлённое.
     * 3) Если сети нет и кэша тоже не было — эмитим failure.
     */
    fun getMoscow3Day(): Flow<Result<WeatherResponseDto>> = flow {
        val key = cacheKey(55.7569, 37.6151, 3, "ru")

        // 1) Пробуем отдать кэш мгновенно (если есть)
        val cachedPair = local.getRawOrNull(key)
        var emittedFromCache = false
        cachedPair?.let { (payload, _) ->
            adapter.fromJson(payload)?.let { cachedDto ->
                emit(Result.success(cachedDto))
                emittedFromCache = true
            }
        }

        // 2) Идём в сеть в любом случае
        when (val net = remote.forecastByCoords(55.7569, 37.6151, days = 3, lang = "ru")) {
            is Result.Success -> {
                val freshDto = net.getOrNull()!!
                // сериализуем и кладём в кэш
                val json = adapter.toJson(freshDto)
                val oldJson = cachedPair?.first
                val changed = json != oldJson
                if (changed) {
                    local.put(key, json)
                    // 3) Эмитим обновлённое (из модели ответа сети)
                    emit(Result.success(freshDto))
                } else if (!emittedFromCache) {
                    // Кэша не эмитили (его не было), но сеть дала данные
                    emit(Result.success(freshDto))
                }
            }
            is Result.Failure -> {
                val error = net.exceptionOrNull()!!
                // Если кэш уже отдали — оставим его, доп. failure можно не слать (или можно слать ещё один Result.failure)
                if (!emittedFromCache) {
                    emit(Result.failure(error))
                }
            }
        }
    }
}