package com.orlove101.android.mvvmstoragetask.persistence.Room

import androidx.room.*
import com.orlove101.android.mvvmstoragetask.data.models.Cat
import kotlinx.coroutines.flow.Flow

@Dao
interface CatsDao {
    fun getCats(query: String, sortOrder: SortOrder): Flow<List<Cat>> {
        return when(sortOrder) {
            SortOrder.BY_NAME -> getCatsSortedByName(query)
            SortOrder.BY_AGE -> getCatsSortedByAge(query)
            SortOrder.BY_BREED -> getCatsSortedByBreed(query)
        }
    }

    @Query("SELECT * FROM cats_table WHERE name LIKE '%' || :searchQuery || '%' ORDER BY name")
    fun getCatsSortedByName(searchQuery: String): Flow<List<Cat>>

    @Query("SELECT * FROM cats_table WHERE age LIKE '%' || :searchQuery || '%' ORDER BY age")
    fun getCatsSortedByAge(searchQuery: String): Flow<List<Cat>>

    @Query("SELECT * FROM cats_table WHERE breed LIKE '%' || :searchQuery || '%' ORDER BY breed")
    fun getCatsSortedByBreed(searchQuery: String): Flow<List<Cat>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cat: Cat)

    @Update
    suspend fun update(cat: Cat)

    @Delete
    suspend fun delete(cat: Cat)
}