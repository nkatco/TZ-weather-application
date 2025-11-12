package nikita.lusenkov.data.local.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import nikita.lusenkov.data.local.db.AppDatabase
import nikita.lusenkov.data.local.db.HttpCacheDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalDbModule {

    @Provides
    @Singleton
    fun provideDb(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "local.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideHttpCacheDao(db: AppDatabase): HttpCacheDao = db.httpCacheDao()
}