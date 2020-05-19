package ru.alexander.twistthetongue.viewmodels

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.alexander.twistthetongue.db.PatterDatabase
import ru.alexander.twistthetongue.db.PatterRepository
import ru.alexander.twistthetongue.model.Patter

class FavouritesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PatterRepository

    val favorites: LiveData<List<Patter>>

    init {
        val patterDao = PatterDatabase.getDatabase(application, viewModelScope).patterDao()
        repository = PatterRepository(patterDao)
        favorites = repository.favoritePatters
    }

    fun update(patter: Patter) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(patter)
    }

    fun search(searchString: String, liveData: MutableLiveData<Patter>) {
        repository.findByTitle(searchString, liveData)
    }
}