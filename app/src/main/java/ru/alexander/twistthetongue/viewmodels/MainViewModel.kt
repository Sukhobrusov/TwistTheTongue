package ru.alexander.twistthetongue.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val currentName: MutableLiveData<List<String>> by lazy {
        MutableLiveData<List<String>>()
    }

    fun setName(name : String){
        currentName.value = listOf(name)
    }

    fun getNames() : LiveData<List<String>> = currentName

}
