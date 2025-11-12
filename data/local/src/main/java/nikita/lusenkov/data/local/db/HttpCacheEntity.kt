package nikita.lusenkov.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "http_cache")
data class HttpCacheEntity(
    @PrimaryKey val key: String,
    val payload: String,
    val updatedAt: Long
)