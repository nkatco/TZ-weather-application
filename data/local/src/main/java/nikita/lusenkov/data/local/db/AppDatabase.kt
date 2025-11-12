package nikita.lusenkov.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [HttpCacheEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun httpCacheDao(): HttpCacheDao
}