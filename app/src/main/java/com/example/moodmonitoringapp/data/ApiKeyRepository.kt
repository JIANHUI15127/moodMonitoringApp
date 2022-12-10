package com.example.moodmonitoringapp.data

import android.content.Context
import android.content.SharedPreferences
import com.example.moodmonitoringapp.util.values.APPLICATION_KEY
import com.example.moodmonitoringapp.util.values.APPLICATION_SECRET_KEY
import com.example.moodmonitoringapp.util.values.EMPTY_STRING
import com.example.moodmonitoringapp.data.PreferenceHelper.get
import com.example.moodmonitoringapp.data.PreferenceHelper.set

class ApiKeyRepository(context: Context) {

    private val prefs: SharedPreferences = PreferenceHelper.prefs(context)

    fun getApplicationKey() = prefs[APPLICATION_KEY] ?: EMPTY_STRING

    fun getApplicationSecretKey() = prefs[APPLICATION_SECRET_KEY] ?: EMPTY_STRING

    fun updateApiKeys() = RxFirebase.getApiKeys()

    fun saveApplicationKey(key: String) {
        prefs[APPLICATION_KEY] = key
    }

    fun saveApplicationSecretKey(key: String) {
        prefs[APPLICATION_SECRET_KEY] = key
    }
}