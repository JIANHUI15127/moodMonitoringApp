package com.example.moodmonitoringapp.recognizer

import android.database.Observable
import android.net.Uri
import com.example.moodmonitoringapp.model.Recognized

interface Recognizer {
    fun recognize(imageUri: Uri): io.reactivex.Observable<Recognized>
}