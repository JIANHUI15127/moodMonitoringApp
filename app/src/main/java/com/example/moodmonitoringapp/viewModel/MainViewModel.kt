package com.example.moodmonitoringapp.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moodmonitoringapp.data.RecognizedRepository
import com.example.moodmonitoringapp.data.ScreenState
import com.example.moodmonitoringapp.data.SettingsRepository
import com.example.moodmonitoringapp.model.Recognized
import com.example.moodmonitoringapp.model.RecognizerName
import com.example.moodmonitoringapp.viewModel.di.App
import com.example.moodmonitoringapp.util.extensions.subscribe

import javax.inject.Inject

class MainViewModel : ViewModel() {

    @Inject
    lateinit var recognizedRepository: RecognizedRepository

    @Inject
    lateinit var settingsRepository: SettingsRepository

    val state = MutableLiveData(ScreenState.DEFAULT)
    val recognized = MutableLiveData(listOf<Recognized>())

    var errorMessage: String? = null

    init {
        App.getComponent().inject(this)
    }

    fun fetch() {
        state.postValue(ScreenState.LOADING)
        subscribe(recognizedRepository.fetch(), {
            if (it.isEmpty()) {
                state.postValue(ScreenState.EMPTY)
            } else {
                recognized.postValue(it)
                state.postValue(ScreenState.SUCCESS)
            }
        }, {
            errorMessage = it.message
            state.postValue(ScreenState.ERROR)
        })
    }

    fun update() {
        recognized.postValue(recognizedRepository.recognized)
    }

    fun getRecognizer(): RecognizerName = settingsRepository.getRecognizer()

    fun saveRecognizer(isSkyBiometryRecognizer: Boolean) {
        if (isSkyBiometryRecognizer) {
            settingsRepository.saveRecognizer(RecognizerName.SKY_BIOMETRY)
        } else {
            settingsRepository.saveRecognizer(RecognizerName.LOCAL)
        }
    }
}