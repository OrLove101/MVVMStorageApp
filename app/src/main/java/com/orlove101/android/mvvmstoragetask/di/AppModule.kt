package com.orlove101.android.mvvmstoragetask.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.orlove101.android.mvvmstoragetask.persistence.Room.CatsDao
import com.orlove101.android.mvvmstoragetask.persistence.Room.CatsDatabase
import com.orlove101.android.mvvmstoragetask.ui.MainActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        app: Application,
        callback: CatsDatabase.Callback
    ): CatsDatabase {
        return Room.databaseBuilder(app, CatsDatabase::class.java, "cats_database")
            .fallbackToDestructiveMigration()
            .addCallback(callback)
            .build()
    }

    @Provides
    fun provideCatsDao(db: CatsDatabase): CatsDao = db.catsDao()

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope(): CoroutineScope = CoroutineScope(SupervisorJob())
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope