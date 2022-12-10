package com.example.moodmonitoringapp.viewModel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moodmonitoringapp.data.RecognizedRepository
import com.example.moodmonitoringapp.data.ScreenState
import com.example.moodmonitoringapp.model.Recognized
import com.example.moodmonitoringapp.recognizer.Recognizer
import com.example.moodmonitoringapp.viewModel.di.App
import com.example.moodmonitoringapp.util.extensions.subscribe
import javax.inject.Inject

class ImageViewModel: ViewModel() {

    @Inject
    lateinit var recognizer: Recognizer

    @Inject
    lateinit var recognizedRepository: RecognizedRepository

    val state = MutableLiveData(ScreenState.DEFAULT)
    val recognized = MutableLiveData<Recognized>()

    var errorMessage: String? = ""

    init {
        App.getComponent().inject(this)
    }

    fun recognize(imageUri: Uri) {
        state.postValue(ScreenState.LOADING)

        val observable = recognizer.recognize(imageUri)
            .flatMap { recognizedRepository.save(it) }

        subscribe(observable, {
            recognized.postValue(it)
            state.postValue(ScreenState.SUCCESS)
        }, {
            errorMessage = it.message
            state.postValue(ScreenState.ERROR)
        })
    }

    fun updateRecognized(recognized: Recognized) {
        this.recognized.postValue(recognized)
        state.postValue(ScreenState.SUCCESS)
    }
}