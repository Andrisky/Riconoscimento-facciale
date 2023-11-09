package com.ndr.unlockwithface.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SettingsViewModel(application : Application) : AndroidViewModel(application) {

    private val repository = DataStoreRepository(application)

    val readFromDataStore = repository.readFromDataStore.asLiveData()

    fun saveToDataStore(myPin: String) = viewModelScope.launch(Dispatchers.IO) {
        repository.saveToDataStore(myPin)
    }
}