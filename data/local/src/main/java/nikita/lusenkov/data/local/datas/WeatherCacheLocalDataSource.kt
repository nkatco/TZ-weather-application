package nikita.lusenkov.data.local.datas

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import nikita.lusenkov.data.local.db.HttpCacheDao
import nikita.lusenkov.data.local.db.HttpCacheEntity
import javax.inject.Inject

class WeatherCacheLocalDataSource @Inject constructor(
    private val dao: HttpCacheDao
) {
    companion object {
        const val TTL_MILLIS: Long = 60 * 60 * 1000
    }

    private fun isFresh(updatedAt: Long, now: Long = System.currentTimeMillis()) =
        now - updatedAt <= TTL_MILLIS

    /**
     * вернет кэш
     */
    suspend fun getRawOrNull(key: String): Pair<String, Long>? = withContext(Dispatchers.IO) {
        dao.get(key)?.let { it.payload to it.updatedAt }
    }

    /**
     * вернет только свежий кэш (< TTL).
     */
    suspend fun getFreshRawOrNull(key: String): String? = withContext(Dispatchers.IO) {
        dao.get(key)?.takeIf { isFresh(it.updatedAt) }?.payload
    }

    suspend fun put(key: String, payload: String) = withContext(Dispatchers.IO) {
        dao.upsert(
            HttpCacheEntity(
                key = key,
                payload = payload,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun cleanup() = withContext(Dispatchers.IO) {
        val monthish = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000
        dao.deleteOlderThan(monthish)
    }
}