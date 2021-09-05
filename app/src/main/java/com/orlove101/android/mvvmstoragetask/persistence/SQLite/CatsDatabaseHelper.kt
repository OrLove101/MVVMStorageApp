package com.orlove101.android.mvvmstoragetask.persistence.SQLite

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import com.orlove101.android.mvvmstoragetask.data.models.Cat
import com.orlove101.android.mvvmstoragetask.persistence.Room.SortOrder
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatsDatabaseHelper @Inject constructor(@ApplicationContext private val context: Context)
    : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        val query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_BREED + " TEXT, " +
                COLUMN_AGE + " INTEGER);"
        db?.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun getCats(query: String, sortOrder: SortOrder): Flow<List<Cat>> {
        return when(sortOrder) {
            SortOrder.BY_NAME -> getCatsSortedByName(query)
            SortOrder.BY_AGE -> getCatsSortedByAge(query)
            SortOrder.BY_BREED -> getCatsSortedByBreed(query)
        }
    }

    private fun getCatsSortedByName(searchQuery: String): Flow<List<Cat>> {
        val query = "SELECT * FROM my_cats WHERE cat_name LIKE '%$searchQuery%' ORDER BY cat_name"
        return getSortedCats(query)
    }

    private fun getCatsSortedByAge(searchQuery: String): Flow<List<Cat>> {
        val query = "SELECT * FROM my_cats WHERE cat_age LIKE '%$searchQuery%' ORDER BY cat_age"
        return getSortedCats(query)
    }

    private fun getCatsSortedByBreed(searchQuery: String): Flow<List<Cat>> {
        val query = "SELECT * FROM my_cats WHERE cate_breed LIKE '%$searchQuery%' ORDER BY cate_breed"
        return getSortedCats(query)
    }

    private fun getSortedCats(query: String): Flow<List<Cat>> {
        val cats = mutableListOf<Cat>()
        val db = readableDatabase
        val cursor = db.rawQuery(query, null)

        try {
            if (cursor.moveToFirst()) {
                do {
                    val catName = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
                    val catBreed = cursor.getString(cursor.getColumnIndex(COLUMN_BREED))
                    val catAge = cursor.getInt(cursor.getColumnIndex(COLUMN_AGE))
                    val catId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))

                    cats.add(Cat(catId, catName, catAge, catBreed))
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            Log.d(TAG, "getCatsSortedByName: Error while trying to get posts from database")
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
        }

        val catsFlowMapper = listOf<List<Cat>>(cats)
        return catsFlowMapper.asFlow()
    }

    fun insert(cat: Cat) {
        val db = writableDatabase

        db.beginTransaction()
        try {
            val cv = ContentValues()

            cv.put(COLUMN_NAME, cat.name)
            cv.put(COLUMN_AGE, cat.age)
            cv.put(COLUMN_BREED, cat.breed)

            val result = db.insertOrThrow(TABLE_NAME, null, cv)
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            Log.d(TAG, "insert: Error while trying to add cat to database")
        } finally {
            db.endTransaction()
        }
    }

    fun update(cat: Cat) {
        val db = writableDatabase

        db.beginTransaction()
        try {
            val cv = ContentValues()

            cv.put(COLUMN_ID, cat.id)
            cv.put(COLUMN_NAME, cat.name)
            cv.put(COLUMN_AGE, cat.age)
            cv.put(COLUMN_BREED, cat.breed)

            db.update(TABLE_NAME, cv, "$COLUMN_ID=?", arrayOf(cat.id.toString()))
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            Log.d(TAG, "update: Error while trying to update cat")
        } finally {
            db.endTransaction()
        }
    }

    fun delete(cat: Cat) {
        val db = writableDatabase

        db.beginTransaction()
        try {
            val cv = ContentValues()

            cv.put(COLUMN_ID, cat.id)
            cv.put(COLUMN_NAME, cat.name)
            cv.put(COLUMN_AGE, cat.age)
            cv.put(COLUMN_BREED, cat.breed)

            db.delete(TABLE_NAME, "$COLUMN_ID=${cat.id}", null)
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            Log.d(TAG, "delete: Error while trying delete cat")
        } finally {
            db.endTransaction()
        }
    }
}

private const val DATABASE_NAME = "Cats.db"
private const val DATABASE_VERSION = 1

private const val TABLE_NAME = "my_cats"
private const val COLUMN_ID = "_id"
private const val COLUMN_NAME = "cat_name"
private const val COLUMN_AGE = "cat_age"
private const val COLUMN_BREED = "cate_breed"

const val DB_FAILED = -1L

private const val TAG = "CatsDatabaseHelper"