package com.example.moodmonitoringapp.recognizer

import android.net.Uri
import androidx.core.net.toFile
import com.example.moodmonitoringapp.model.Recognized
import com.example.moodmonitoringapp.network.NetworkService
import com.example.moodmonitoringapp.viewModel.di.App

import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject


class SkyBiometryRecognizer : Recognizer {

    @Inject
    lateinit var networkService: NetworkService

    init {
        App.getComponent().inject(this)
    }

    override fun recognize(imageUri: Uri): Observable<Recognized> {
        val file = imageUri.toFile()

        val filePart = MultipartBody.Part.createFormData(
            "urls",
            file.name,
            RequestBody.create(null, file)
        )

        return networkService.recognize(filePart)
            .map { it.convert() }
    }
}