package ru.alexander.twistthetongue.db

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.alexander.twistthetongue.model.Patter

class PatterRepository(private val patterDao: PatterDao) {

    val favoritePatters: LiveData<List<Patter>> = patterDao.getFavorites()
    val allPatters: LiveData<List<Patter>> = patterDao.getAll()
    val sortedPatters : LiveData<List<Patter>> = patterDao.getSortedByViewCount()

    suspend fun insert(patter: Patter) {
        patterDao.insert(patter)
    }

    suspend fun update(patter: Patter) {
        patterDao.updatePatter(patter)
    }

    suspend fun insertList(list: List<Patter>) {
        patterDao.insertAll(list)
    }

    fun findByTitle(
        searchString: String,
        liveData: MutableLiveData<Patter>
    ) {
        val disposable = patterDao.findByTitle(searchString)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                liveData.value = it
            }
    }
}