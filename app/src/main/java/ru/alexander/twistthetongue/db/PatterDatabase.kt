package ru.alexander.twistthetongue.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import ru.alexander.twistthetongue.model.Patter

@Database(entities = arrayOf(Patter::class), version = 1)
abstract class PatterDatabase : RoomDatabase() {
    abstract fun patterDao(): PatterDao

    private class PatterDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.patterDao())
                }
            }
        }

        suspend fun populateDatabase(patterDao : PatterDao) {

            // Delete all content here.
            patterDao.deleteAll()

            // Add sample words.
            val jsonArray = JSONArray(
                """[
  { 
      "id": 0, 
      "text": "Peter Piper picked a peck of pickled peppers\nA peck of pickled peppers Peter Piper picked\nIf Peter Piper picked a peck of pickled peppers\nWhere’s the peck of pickled peppers Peter Piper picked?", 
      "title": "Peter Piper",
      "mark": 0, 
      "visits": 0, 
      "favorite": false 
    }, 
    { 
      "id": 1, 
      "text": "How much wood would a woodchuck chuck if a woodchuck could chuck wood?\nHe would chuck, he would, as much as he could, and chuck as much wood\nAs a woodchuck would if a woodchuck could chuck wood" ,
      "title": "Woodchunk chunk",
      "mark": 0, 
      "visits": 0, 
      "favorite": false 
    }, 
    { 
      "id": 2, 
      "text": "Betty Botter bought some butter\nBut she said the butter’s bitter\nIf I put it in my batter, it will make my batter bitter\nBut a bit of better butter will make my batter better\nSo ‘twas better Betty Botter bought a bit of better butter",
      "title": "Betty Botter",
      "mark": 0, 
      "visits": 0, 
      "favorite": false 
    }
    , 
    { 
      "id": 3, 
      "text": "Betty Botter bought some butter\nBut she said the butter’s bitter\nIf I put it in my batter, it will make my batter bitter\nBut a bit of better butter will make my batter better\nSo ‘twas better Betty Botter bought a bit of better butter",
      "title": "Betty Botter",
      "mark": 0, 
      "visits": 0, 
      "favorite": false 
    }
]
"""
            )

            for (i in 0..3) {
                val patter = Patter(
                    jsonArray.getJSONObject(i)["id"] as Int,
                    jsonArray.getJSONObject(i)["text"] as String,
                    jsonArray.getJSONObject(i)["title"] as String,
                    jsonArray.getJSONObject(i)["mark"] as Int,
                    jsonArray.getJSONObject(i)["visits"] as Int,
                    jsonArray.getJSONObject(i)["favorite"] as Boolean
                )
                patterDao.insert(patter)
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: PatterDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): PatterDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null)
                return tempInstance

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PatterDatabase::class.java,
                    "patter_database"
                )
                    .addCallback(PatterDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}