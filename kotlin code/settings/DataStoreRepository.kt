package com.ndr.unlockwithface.settings

import android.content.Context
import android.util.Log
import androidx.datastore.DataStore
import androidx.datastore.preferences.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

const val PREFERENCE_NAME = "my_preference"

class DataStoreRepository(context : Context) {

    private object PreferenceKeys {
        val pin = preferencesKey<String>("my_pin")
    }

    private val dataStore: DataStore<Preferences> = context.createDataStore(
        name = PREFERENCE_NAME
    )

    suspend fun saveToDataStore(pin: String){
        dataStore.edit { preference ->
            preference[PreferenceKeys.pin] = pin
        }
    }
    val readFromDataStore: Flow<String> = dataStore.data
        .catch { exception ->
            if(exception is IOException){
                Log.d("DataStore", exception.message.toString())
                emit(emptyPreferences())
            }else {
                throw exception
            }
        }
        .map { preference ->
            val myPin = preference[PreferenceKeys.pin] ?: "none"
            myPin
        }
}