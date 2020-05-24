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
      "text": "Лавировали, лавировали, да не вылавировали",
      "title": "Лавировали",
      "mark": 0,
      "visits": 0,
      "favorite": false
    }, 
    { 
      "id": 1,
      "text": "Мама мыла Милу мылом, Мила мыло не любила.",
      "title": "Мама мыла милу",
      "mark": 0,
      "visits": 0,
      "favorite": false
    }, 
    { 
      "id": 2,
      "text": "Ехал Грека через реку,\nВидит Грека в реке — рак.\nСунул Грека руку в реку,\nРак за руку Грека — цап!",
      "title": "Ехал Грека",
      "mark": 0,
      "visits": 0,
      "favorite": false
    }
]
"""
            )

            for (i in 0..2) {
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