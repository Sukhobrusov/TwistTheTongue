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
                "[\n" +
                        "  {\n" +
                        "    \"id\": 0,\n" +
                        "    \"text\": \"Peter Piper picked a peck of pickled peppers\\nA peck of pickled peppers Peter Piper picked\\nIf Peter Piper picked a peck of pickled peppers\\nWhere’s the peck of pickled peppers Peter Piper picked?\",\n" +
                        "    \"title\": \"Peter Piper\",\n" +
                        "    \"mark\": 0,\n" +
                        "    \"visits\": 0,\n" +
                        "    \"favorite\": false\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"id\": 1,\n" +
                        "    \"text\": \"How much wood would a woodchuck chuck if a woodchuck could chuck wood?\\nHe would chuck, he would, as much as he could, and chuck as much wood\\nAs a woodchuck would if a woodchuck could chuck wood\",\n" +
                        "    \"title\": \"Woodchunk chunk\",\n" +
                        "    \"mark\": 0,\n" +
                        "    \"visits\": 0,\n" +
                        "    \"favorite\": false\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"id\": 2,\n" +
                        "    \"text\": \"Betty Botter bought some butter\\nBut she said the butter’s bitter\\nIf I put it in my batter, it will make my batter bitter\\nBut a bit of better butter will make my batter better\\nSo ‘twas better Betty Botter bought a bit of better butter\",\n" +
                        "    \"title\": \"Betty Botter\",\n" +
                        "    \"mark\": 0,\n" +
                        "    \"visits\": 0,\n" +
                        "    \"favorite\": false\n" +
                        "  }\n" +
                        "]\n"
            )
            var patter = Patter(
                jsonArray.getJSONObject(0)["id"] as Int,
                jsonArray.getJSONObject(0)["text"] as String,
                jsonArray.getJSONObject(0)["title"] as String,
                jsonArray.getJSONObject(0)["mark"] as Int,
                jsonArray.getJSONObject(0)["visits"] as Int,
                jsonArray.getJSONObject(0)["favorite"] as Boolean
            )
            patterDao.insert(patter)

            patter = Patter(
                jsonArray.getJSONObject(1)["id"] as Int,
                jsonArray.getJSONObject(1)["text"] as String,
                jsonArray.getJSONObject(1)["title"] as String,
                jsonArray.getJSONObject(1)["mark"] as Int,
                jsonArray.getJSONObject(1)["visits"] as Int,
                jsonArray.getJSONObject(1)["favorite"] as Boolean
            )
            patterDao.insert(patter)

            patter = Patter(
                jsonArray.getJSONObject(2)["id"] as Int,
                jsonArray.getJSONObject(2)["text"] as String,
                jsonArray.getJSONObject(2)["title"] as String,
                jsonArray.getJSONObject(2)["mark"] as Int,
                jsonArray.getJSONObject(2)["visits"] as Int,
                jsonArray.getJSONObject(2)["favorite"] as Boolean
            )
            patterDao.insert(patter)

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