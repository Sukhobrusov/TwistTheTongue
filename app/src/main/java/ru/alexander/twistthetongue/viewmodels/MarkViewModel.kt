package ru.alexander.twistthetongue.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.alexander.twistthetongue.network.YandexApi

class MarkViewModel : ViewModel() {

    val api = YandexApi.create()

    val mark : MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    fun recognize(content : ByteArray){
        val disposable = api.recognize("",content)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {mark.value = 25},
                {mark.value = 100}
            )
    }
}