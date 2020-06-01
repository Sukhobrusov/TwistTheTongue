package ru.alexander.twistthetongue.db

import androidx.lifecycle.LiveData
import androidx.room.*
import io.reactivex.Maybe
import ru.alexander.twistthetongue.model.Patter

@Dao
interface PatterDao {
    @Query("SELECT * FROM patters ORDER BY title")
    fun getAll() : LiveData<List<Patter>>

    @Query("SELECT * FROM patters WHERE favorite = 1")
    fun getFavorites() : LiveData<List<Patter>>

    @Query("SELECT * FROM patters WHERE visits > 0 ORDER BY visits DESC LIMIT 5")
    fun getSortedByViewCount() : LiveData<List<Patter>>

    @Query("SELECT * FROM patters WHERE title LIKE :search LIMIT 1")
    fun findByTitle(search : String) : Maybe<Patter>

    @Query("SELECT * FROM patters WHERE title LIKE :search AND favorite = 1 LIMIT 1")
    fun findByTitleFromFavorites(search : String) : Maybe<Patter>

    @Insert
    fun insert(patter : Patter)

    @Insert
    fun insertAll(patters : List<Patter>)

    @Update
    suspend fun updatePatter(patter: Patter)

    @Update
    suspend fun updateAll(patters : List<Patter>)

    @Query("DELETE FROM patters")
    suspend fun deleteAll()

}