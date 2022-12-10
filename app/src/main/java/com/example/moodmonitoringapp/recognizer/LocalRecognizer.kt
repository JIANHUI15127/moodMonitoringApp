package com.example.moodmonitoringapp.recognizer

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.moodmonitoringapp.data.RxFirebase
import com.example.moodmonitoringapp.model.Recognized
import com.example.moodmonitoringapp.model.RecognizerName
import com.example.moodmonitoringapp.recognizer.tensorflow.EmotionClassifier
import com.example.moodmonitoringapp.util.generateId
import com.example.moodmonitoringapp.viewModel.di.App
import io.reactivex.Observable
import java.util.*
import javax.inject.Inject

class LocalRecognizer : Recognizer {

    @Inject
    lateinit var emotionClassifier: EmotionClassifier

    init {
        App.getComponent().inject(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun recognize(imageUri: Uri): Observable<Recognized> {
        val bitmap = BitmapFactory.decodeFile(imageUri.path)

        return RxFirebase.uploadImage(imageUri)
            .flatMap { link -> Observable.create<Recognized> { emitter ->
                val emotion = emotionClassifier.classify(bitmap)
                if (emotion != null) {
                    val recognized = Recognized(
                        id = generateId(7),
                        image = link,
                        date = Date(),
                        recognizer = RecognizerName.LOCAL,
                        emotions = emotion
                    )
                    emitter.onNext(recognized)
                    emitter.onComplete()
                } else {
                    emitter.onError(Throwable("Faces not found or emotion not recognized"))
                }
            } }
    }

}