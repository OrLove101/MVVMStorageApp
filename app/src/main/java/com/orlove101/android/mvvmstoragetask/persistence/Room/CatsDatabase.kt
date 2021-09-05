package com.orlove101.android.mvvmstoragetask.persistence.Room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.orlove101.android.mvvmstoragetask.data.models.Cat
import com.orlove101.android.mvvmstoragetask.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Cat::class], version = 1)
abstract class CatsDatabase: RoomDatabase() {

    abstract fun catsDao(): CatsDao

    class Callback @Inject constructor(
        private val database: Provider<CatsDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ): RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val dao = database.get().catsDao()

            applicationScope.launch {
                dao.insert(Cat(name = "Vasiliy", age = 1, breed = "Pers"))
                dao.insert(Cat(name = "Sonya", age = 2, breed = "Pers"))
                dao.insert(Cat(name = "Stepan", age = 5, breed = "Sfinks"))
                dao.insert(Cat(name = "Ocsana", age = 3, breed = "Britanec"))
                dao.insert(Cat(name = "Pushok", age = 4, breed = "Pers"))
            }
        }
    }
}