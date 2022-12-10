package com.example.moodmonitoringapp.data

import android.content.Context
import com.example.moodmonitoringapp.model.RecognizerName
import com.example.moodmonitoringapp.util.values.RECOGNIZER_KEY
import com.example.moodmonitoringapp.data.PreferenceHelper.get
import com.example.moodmonitoringapp.data.PreferenceHelper.set



class SettingsRepository(context: Context) {

    private val prefs = PreferenceHelper.prefs(context)

    fun getRecognizer(): RecognizerName {
        val recognizer: String? = prefs[RECOGNIZER_KEY]

        return if (recognizer == RecognizerName.LOCAL.name) {
            RecognizerName.LOCAL
        } else {
            RecognizerName.SKY_BIOMETRY
        }
    }

    fun saveRecognizer(recognizer: RecognizerName) {
        prefs[RECOGNIZER_KEY] = recognizer.name
    }
}