package nikita.lusenkov.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HttpCacheDao {

    @Query("SELECT * FROM http_cache WHERE key = :key LIMIT 1")
    suspend fun get(key: String): HttpCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: HttpCacheEntity)

    @Query("DELETE FROM http_cache WHERE updatedAt < :olderThan")
    suspend fun deleteOlderThan(olderThan: Long)
}